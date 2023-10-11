package org.example.types;

import org.example.types.attributes.Attribute;

import java.nio.ByteBuffer;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

public class PageHeader {
    public final List<PageHeaderColumn> columnList;

    public PageHeader(List<PageHeaderColumn> columnList) {
        this.columnList = columnList;
    }

    public int getTupleLength() {
        return this.columnList.stream().mapToInt(column -> column.attributeType.size).sum();
    }

    public byte[] serialize() {
        byte columnCount = (byte) this.columnList.size();
        ByteBuffer byteBuffer = ByteBuffer.allocate(1 + columnCount * PageHeaderColumn.SIZE);
        byteBuffer.put(columnCount);
        this.columnList.stream().map(PageHeaderColumn::serialize).forEach(byteBuffer::put);
        return byteBuffer.array();
    }

    public static PageHeader deserialize(byte[] bytes) {
        byte columnCount = bytes[0];
        List<PageHeaderColumn> columns = new ArrayList<>();

        for(int i=0; i<columnCount; i++) {
            int offset = i * PageHeaderColumn.SIZE;
            byte[] pageHeaderBytes = Arrays.copyOfRange(bytes, offset, offset + PageHeaderColumn.SIZE);
            columns.add(PageHeaderColumn.deserialize(pageHeaderBytes));
        }

        return new PageHeader(columns);
    }

    public static PageHeader fromAttributes(List<Attribute.TYPES> attributeTypes) {
        List<PageHeaderColumn> columnList = new ArrayList<>();

        for(int i=0; i<attributeTypes.size(); i++) {
            Attribute.TYPES type = attributeTypes.get(i);
            byte columnNumber = (byte) (i+1);
            columnList.add(new PageHeaderColumn(columnNumber, type));
        }

        return new PageHeader(columnList);
    }

    public static int getSerializedLength(int columnCount) {
        return 1 + columnCount * PageHeaderColumn.SIZE;
    }
}
