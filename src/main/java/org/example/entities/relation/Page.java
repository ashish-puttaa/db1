package org.example.entities.relation;

import org.example.Constants;
import org.example.entities.common.Dirtyable;
import org.example.iterators.PageTupleIterator;
import org.example.util.ByteUtil;

import java.nio.ByteBuffer;
import java.util.Arrays;
import java.util.Iterator;
import java.util.List;

// Done: Add page unique identifier which will be maintained in the directory
// TODO: Add table identifier/schemas
// Done: Add slot array to map (2byte) slots to tuple offsets in page. It will also contain the length
// TODO: Move tuples will fill empty spaces after deletion during compaction
// Done: Not here, but every tuple will have a record id somewhere which will contain the page id and the slot array index. Its called ctid in postgres (6 bytes).
// TODO: Add a overflow page for very large tuple values
public class Page extends Dirtyable {
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

    private PageTuple.RecordIdentifier constructTupleRecordIdentifier(short slotIndex) {
        int pageId = this.header.pageIdentifier;
        return new PageTuple.RecordIdentifier(pageId, slotIndex);
    }

    public PageTuple.RecordIdentifier insertTuple(PageTuple tuple) throws PageFullException {
        short desiredLength = (short) tuple.getSerializedLength();
        short offset = this.slotArray.getHole(desiredLength).orElseThrow(PageFullException::new);

        boolean isSlotArrayAppend = this.slotArray.getEmptySlotIndex().isEmpty();

        if(isSlotArrayAppend) {
            int slotArrayOffsetEnd = this.slotArray.getTupleOffsetStart() - 1;
            int slotArrayOffsetEndAfterAppend = slotArrayOffsetEnd + PageSlot.getSerializedLength();

            if(slotArrayOffsetEndAfterAppend >= offset) {
                throw new PageFullException();
            }

            this.header.incrementSlotCount();
        }

        short slotIndex = this.slotArray.insertSlot(offset, desiredLength);

        if(isSlotArrayAppend) {
            this.serializedTuples = ByteUtil.shrinkByteArrayFromFront(this.serializedTuples, PageSlot.getSerializedLength());
        }

        byte[] tupleBytes = tuple.serialize();
        int tupleOffset = offset - this.slotArray.getTupleOffsetStart();
        System.arraycopy(tupleBytes, 0, this.serializedTuples, tupleOffset, desiredLength);

        this.markAsDirty();
        return this.constructTupleRecordIdentifier(slotIndex);
    }

    public void removeTuple(PageSlot slotEntry) {
        byte[] tupleBytes = new byte[slotEntry.tupleLength];
        Arrays.fill(tupleBytes, (byte) 0);

        int tupleBytesOffset = slotEntry.pageOffset - this.slotArray.getTupleOffsetStart();
        System.arraycopy(tupleBytes, 0, this.serializedTuples, tupleBytesOffset, tupleBytes.length);

        this.slotArray.emptySlot(slotEntry.slotIndex);
        this.markAsDirty();
    }

    public void removeTuple(int slotIndex) {
        PageSlot slot = this.slotArray.getSlot(slotIndex);
        this.removeTuple(slot);
    }

    public PageTuple readTuple(PageSlot slotEntry) {
        byte[] tupleBytes = new byte[slotEntry.tupleLength];

        int tupleBytesOffset = slotEntry.pageOffset - this.slotArray.getTupleOffsetStart();
        System.arraycopy(this.serializedTuples, tupleBytesOffset, tupleBytes, 0, slotEntry.tupleLength);

        PageTuple.RecordIdentifier recordId = this.constructTupleRecordIdentifier(slotEntry.slotIndex);
        return PageTuple.deserialize(tupleBytes, this.columnMetadataArray, recordId);
    }

    public PageTuple readTuple(int slotIndex) {
        PageSlot slot = this.slotArray.getSlot(slotIndex);
        return this.readTuple(slot);
    }

    public Iterator<PageTuple> getTuplesIterator() {
        return new PageTupleIterator(this, this.slotArray.getIterator());
    }

    public static class PageFullException extends Exception {}
}
