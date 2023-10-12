package org.example.entities.directory;

import org.example.Constants;
import org.example.util.ByteUtil;
import org.example.util.CommonUtil;

import java.nio.ByteBuffer;
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
    public List<Tuple> tupleList;

    public Page(PageHeader header, PageColumnMetadataArray columnMetadataArray, PageSlotArray slotArray, List<Tuple> tupleList) {
        this.header = header;
        this.columnMetadataArray = columnMetadataArray;
        this.slotArray = slotArray;
        this.tupleList = tupleList;
    }

    public static Page deserialize(byte[] bytes) {
        ByteBuffer byteBuffer = ByteBuffer.wrap(bytes);

        byte[] headerBytes = ByteUtil.readNBytes(byteBuffer, PageHeader.getSerializedLength());
        PageHeader header = PageHeader.deserialize(headerBytes);

        byte[] columnMetadataArrayBytes = ByteUtil.readNBytes(byteBuffer, PageColumnMetadataArray.getSerializedLength(header.columnCount));
        PageColumnMetadataArray columnMetadataArray = PageColumnMetadataArray.deserialize(columnMetadataArrayBytes, header.columnCount);

        byte[] slotArrayBytes = ByteUtil.readNBytes(byteBuffer, PageSlotArray.getSerializedLength(header.slotCount));
        PageSlotArray slotArray = PageSlotArray.deserialize(slotArrayBytes, header.slotCount);

        byte[] tupleListBytes = ByteUtil.readNBytes(byteBuffer, columnMetadataArray.getTupleLength() * header.slotCount);

        List<byte[]> tupleBytesList = CommonUtil.splitByteArray(tupleListBytes, columnMetadataArray.getTupleLength());

        List<Tuple> tupleList = tupleBytesList.stream()
                .map(tupleBytes -> Tuple.deserialize(tupleBytes, columnMetadataArray))
                .toList();

        return new Page(header, columnMetadataArray, slotArray, tupleList);
    }

    public byte[] serialize() {
        ByteBuffer byteBuffer = ByteBuffer.allocate(Constants.PAGE_SIZE);
        byteBuffer.put(this.header.serialize());
        byteBuffer.put(this.columnMetadataArray.serialize());
        byteBuffer.put(this.slotArray.serialize());
        this.tupleList.stream().map(Tuple::serialize).forEach(byteBuffer::put);
        return byteBuffer.array();
    }
}
