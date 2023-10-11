package org.example.types;

import org.example.Constants;

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
    public PageHeader pageHeader;
    public List<Tuple> tupleList;

    public Page(PageHeader pageHeader, List<Tuple> tupleList) {
        this.pageHeader = pageHeader;
        this.tupleList = tupleList;
    }

    public byte[] serialize() {
        ByteBuffer byteBuffer = ByteBuffer.allocate(Constants.PAGE_SIZE);
        byteBuffer.put(this.pageHeader.serialize());
        this.tupleList.stream().map(Tuple::serialize).forEach(byteBuffer::put);
        return byteBuffer.array();
    }
}
