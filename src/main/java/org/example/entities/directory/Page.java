package org.example.entities.directory;

import org.example.Constants;
import org.example.iterators.PageTupleIterator;
import org.example.util.ByteUtil;

import java.nio.ByteBuffer;
import java.util.Iterator;
import java.util.List;

//TODO: Add page unique identifier which will be maintained in the directory
//TODO: Add table identifier/schemas
//TODO: Add slot array to map (2byte) slots to tuple offsets in page. It will also contain the length
//TODO: Move tuples will fill empty spaces after deletion during compaction
//TODO: Not here, but every tuple will have a record id somewhere which will contain the page id and the slot array index.
// Its called ctid in postgres (6 bytes)
//TODO: Add a overflow page for very large tuple values
public class Page {
    public PageHeader header;
    public PageColumnMetadataArray columnMetadataArray;
    public PageSlotArray slotArray;
    public byte[] serializedTuples;

    private Page(PageHeader header, PageColumnMetadataArray columnMetadataArray, PageSlotArray slotArray, byte[] serializedTuples) {
        this.header = header;
        this.columnMetadataArray = columnMetadataArray;
        this.slotArray = slotArray;
        this.serializedTuples = serializedTuples;
    }

    public Page(int pageIdentifier, List<AttributeType> attributeTypeList) {
        byte numColumns = (byte) attributeTypeList.size();
        short pageSlotArrayOffsetStart = (short) (PageHeader.getSerializedLength() + PageColumnMetadataArray.getSerializedLength(numColumns));

        this.header = new PageHeader(numColumns, pageIdentifier);;
        this.columnMetadataArray = PageColumnMetadataArray.fromAttributes(attributeTypeList);;
        this.slotArray = new PageSlotArray(pageSlotArrayOffsetStart);

        int tupleLength = Constants.PAGE_SIZE - PageHeader.getSerializedLength() - this.columnMetadataArray.getSerializedLength() - this.slotArray.getSerializedLength();
        this.serializedTuples = new byte[tupleLength];
    }

    public static Page deserialize(byte[] bytes) {
        ByteBuffer byteBuffer = ByteBuffer.wrap(bytes);

        byte[] headerBytes = ByteUtil.readNBytes(byteBuffer, PageHeader.getSerializedLength());
        PageHeader header = PageHeader.deserialize(headerBytes);

        byte[] columnMetadataArrayBytes = ByteUtil.readNBytes(byteBuffer, PageColumnMetadataArray.getSerializedLength(header.columnCount));
        PageColumnMetadataArray columnMetadataArray = PageColumnMetadataArray.deserialize(columnMetadataArrayBytes, header.columnCount);

        byte[] slotArrayBytes = ByteUtil.readNBytes(byteBuffer, PageSlotArray.getSerializedLength(header.slotCount));
        short slotArrayOffsetStart = (short) (headerBytes.length + columnMetadataArrayBytes.length);
        PageSlotArray slotArray = PageSlotArray.deserialize(slotArrayBytes, header.slotCount, slotArrayOffsetStart);

        byte[] tupleBytes = ByteUtil.readNBytes(byteBuffer, byteBuffer.remaining());

        return new Page(header, columnMetadataArray, slotArray, tupleBytes);
    }

    public byte[] serialize() {
        ByteBuffer byteBuffer = ByteBuffer.allocate(Constants.PAGE_SIZE);
        byteBuffer.put(this.header.serialize());
        byteBuffer.put(this.columnMetadataArray.serialize());
        byteBuffer.put(this.slotArray.serialize());
        byteBuffer.put(this.serializedTuples);
        return byteBuffer.array();
    }

    public int insertTuple(PageTuple tuple) throws PageFullException {
        short desiredLength = (short) tuple.getSerializedLength();
        short offset = this.slotArray.getHole(desiredLength).orElseThrow(PageFullException::new);

        boolean isSlotArrayAppend = this.slotArray.getEmptySlotIndex().isEmpty();

        if(isSlotArrayAppend) {
            int slotArrayOffsetEnd = this.slotArray.getTupleOffsetStart() - 1;
            int slotArrayOffsetEndAfterAppend = slotArrayOffsetEnd + PageSlotArrayEntry.getSerializedLength();

            if(slotArrayOffsetEndAfterAppend >= offset) {
                throw new PageFullException();
            }

            this.header.incrementSlotCount();
        }

        int slotIndex = this.slotArray.insertSlot(offset, desiredLength);

        if(isSlotArrayAppend) {
            this.serializedTuples = ByteUtil.shrinkByteArrayFromFront(this.serializedTuples, PageSlotArrayEntry.getSerializedLength());
        }

        byte[] tupleBytes = tuple.serialize();
        int tupleOffset = offset - this.slotArray.getTupleOffsetStart();
        System.arraycopy(tupleBytes, 0, this.serializedTuples, tupleOffset, desiredLength);

        return slotIndex;
    }

    public void removeTuple(int slotIndex) {
        this.slotArray.emptySlot(slotIndex);
    }

    public PageTuple readTuple(PageSlotArrayEntry slotEntry) {
        byte[] tupleBytes = new byte[slotEntry.tupleLength];

        int tupleBytesOffset = slotEntry.pageOffset - this.slotArray.getTupleOffsetStart();
        System.arraycopy(this.serializedTuples, tupleBytesOffset, tupleBytes, 0, slotEntry.tupleLength);
        return PageTuple.deserialize(tupleBytes, this.columnMetadataArray);
    }

    public PageTuple readTuple(int slotIndex) {
        PageSlotArrayEntry slot = this.slotArray.getSlot(slotIndex);
        return this.readTuple(slot);
    }

    public Iterator<PageTuple> getTuplesIterator() {
        return new PageTupleIterator(this, this.slotArray.getIterator());
    }

    public static class PageFullException extends Exception {}
}
