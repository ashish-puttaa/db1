package org.example.types;

import org.example.Constants;
import org.example.Util;

import java.nio.ByteBuffer;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

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
