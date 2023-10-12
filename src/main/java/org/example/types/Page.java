package org.example.types;

import org.example.Constants;
import org.example.Util;

import java.nio.ByteBuffer;
import java.util.Arrays;
import java.util.List;
import java.util.stream.Collectors;
import java.util.stream.IntStream;

//TODO: Add page unique identifier which will be maintained in the directory
//TODO: Add table identifier/schemas
//TODO: Add slot array to map (2byte) slots to tuple offsets in page. It will also contain the length
//TODO: Move tuples will fill empty spaces after deletion during compaction
//TODO: Not here, but every tuple will have a record id somewhere which will contain the page id and the slot array index.
// Its called ctid in postgres (6 bytes)
//TODO: Add a overflow page for very large tuple values
public class Page {
    public PageHeader header;
    public List<Tuple> tupleList;

    public Page(PageHeader header, List<Tuple> tupleList) {
        this.header = header;
        this.tupleList = tupleList;
    }

    public static Page deserialize(byte[] bytes) {
        int headerColumnCount = bytes[0];
        int headerEndOffset = PageHeader.getSerializedLength(headerColumnCount);

        byte[] pageHeaderBytes = Arrays.copyOfRange(bytes, 0, headerEndOffset);
        PageHeader pageHeader = PageHeader.deserialize(pageHeaderBytes);

        byte[] tuplesBytes = Arrays.copyOfRange(bytes, headerEndOffset, bytes.length);
        List<byte[]> tupleBytesList = Util.splitByteArray(tuplesBytes, pageHeader.getTupleLength());

        List<Tuple> tupleList = IntStream.range(0, pageHeader.tupleCount)
                .mapToObj(tupleBytesList::get)
                .map(tupleBytes -> Tuple.deserialize(tupleBytes, pageHeader))
                .toList();

        return new Page(pageHeader, tupleList);
    }

    public byte[] serialize() {
        ByteBuffer byteBuffer = ByteBuffer.allocate(Constants.PAGE_SIZE);
        byteBuffer.put(this.header.serialize());
        this.tupleList.stream().map(Tuple::serialize).forEach(byteBuffer::put);
        return byteBuffer.array();
    }
}
