package org.example.types;

import org.example.Constants;
import org.example.Util;

import java.nio.ByteBuffer;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

//TODO: Add page unique identifier which will be maintained in the directory
//TODO: Add table identifier/schemas
//TODO: Add slot array to map (2byte) slots to tuple offsets in page. It will also contain the length
//TODO: Move tuples will fill empty spaces after deletion during compaction
//TODO: Not here, but every tuple will have a record id somewhere which will contain the page id and the slot array index.
// Its called ctid in postgres (6 bytes)
//TODO: Add a overflow page for very large tuple values
public class Page {
    public PageHeader pageHeader;
    public List<Tuple> tupleList;

    public Page(PageHeader pageHeader, List<Tuple> tupleList) {
        this.pageHeader = pageHeader;
        this.tupleList = tupleList;
    }

    public static Page deserialize(byte[] pageBytes) {
        int headerColumnCount = pageBytes[0];
        int headerEndOffset = PageHeader.getSerializedLength(headerColumnCount);

        byte[] pageHeaderBytes = Arrays.copyOfRange(pageBytes, 0, headerEndOffset);
        byte[] tuplesBytes = Arrays.copyOfRange(pageBytes, headerEndOffset, pageBytes.length);

        PageHeader pageHeader = PageHeader.deserialize(pageHeaderBytes);

        List<byte[]> tupleBytesList = Util.splitByteArray(tuplesBytes, pageHeader.getTupleLength());

        List<Tuple> tupleList = new ArrayList<>();
        for(int i=0; i<pageHeader.tupleCount; i++) {
            byte[] tupleBytes = tupleBytesList.get(i);
            tupleList.add(Tuple.deserialize(tupleBytes, pageHeader));
        }

        return new Page(pageHeader, tupleList);
    }

    public byte[] serialize() {
        ByteBuffer byteBuffer = ByteBuffer.allocate(Constants.PAGE_SIZE);
        byteBuffer.put(this.pageHeader.serialize());
        this.tupleList.stream().map(Tuple::serialize).forEach(byteBuffer::put);
        return byteBuffer.array();
    }
}
