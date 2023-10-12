package org.example.entities.directory;

import org.example.util.ByteUtil;

import java.nio.ByteBuffer;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

public class PageHeader {
    public byte columnCount;
    public int pageIdentifier;
    public final List<PageHeaderColumn> columnList;
    public int tupleCount;

    public PageHeader(byte columnCount, int pageIdentifier, List<PageHeaderColumn> columnList, int tupleCount) {
        this.columnCount = columnCount;
        this.pageIdentifier = pageIdentifier;
        this.columnList = columnList;
        this.tupleCount = tupleCount;
    }

    public int getTupleLength() {
        return getTupleLength(this.columnList);
    }

    public byte[] serialize() {
        ByteBuffer byteBuffer = ByteBuffer.allocate(getSerializedLength(this.columnCount));

        byteBuffer.put(this.columnCount);
        byteBuffer.putInt(this.pageIdentifier);

        this.columnList.stream().map(PageHeaderColumn::serialize).forEach(byteBuffer::put);
        byteBuffer.putInt(this.tupleCount);

        return byteBuffer.array();
    }

    public static PageHeader deserialize(byte[] bytes) {
        byte columnCount = bytes[0];
        int pageIdentifier = ByteUtil.getInteger(bytes, 1, 5);

        List<PageHeaderColumn> columns = new ArrayList<>();
        int columnOffsetStart = 5;

        for(int i=0; i<columnCount; i++) {
            int offset = columnOffsetStart + (i * PageHeaderColumn.SIZE);
            byte[] pageHeaderBytes = Arrays.copyOfRange(bytes, offset, offset + PageHeaderColumn.SIZE);
            columns.add(PageHeaderColumn.deserialize(pageHeaderBytes));
        }

        int tupleCountOffsetStart = columnOffsetStart + columnCount * PageHeaderColumn.SIZE;
        int tupleCount = ByteUtil.getInteger(bytes, tupleCountOffsetStart, tupleCountOffsetStart + 4);

        return new PageHeader(columnCount, pageIdentifier, columns, tupleCount);
    }

    public static int getSerializedLength(int columnCount) {
        int columnCountLength = 1;
        int pageIdentifierLength = 4;
        int tupleListLength = columnCount * PageHeaderColumn.SIZE;
        int tupleCountLength = 4;

        return columnCountLength + pageIdentifierLength + tupleListLength + tupleCountLength;
    }

    public static int getTupleLength(List<PageHeaderColumn> columnList) {
        return columnList.stream().mapToInt(column -> column.attributeType.size).sum();
    }
}
