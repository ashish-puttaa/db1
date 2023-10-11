package org.example.types;

import java.nio.ByteBuffer;
import java.util.List;

public class PageHeader {
    public final List<PageHeaderColumn> columnList;
    public int tupleCount;

    public PageHeader(List<PageHeaderColumn> columnList, int tupleCount) {
        this.columnList = columnList;
        this.tupleCount = tupleCount;
    }

    public int getTupleLength() {
        return getTupleLength(this.columnList);
    }

    public byte[] serialize() {
        byte columnCount = (byte) this.columnList.size();
        ByteBuffer byteBuffer = ByteBuffer.allocate(getSerializedLength(columnCount));
        byteBuffer.put(columnCount);

        this.columnList.stream().map(PageHeaderColumn::serialize).forEach(byteBuffer::put);
        byteBuffer.putInt(this.tupleCount);

        return byteBuffer.array();
    }



    public static int getSerializedLength(int columnCount) {
        return 1 + columnCount * PageHeaderColumn.SIZE + 4;
    }

    public static int getTupleLength(List<PageHeaderColumn> columnList) {
        return columnList.stream().mapToInt(column -> column.attributeType.size).sum();
    }
}
