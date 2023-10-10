package org.example.types;

import org.example.types.attributes.Attribute;

import java.nio.ByteBuffer;
import java.util.ArrayList;
import java.util.List;

public class PageHeader {
    public final List<PageHeaderColumn> columnList;
    public final byte columnCount;

    public PageHeader(List<PageHeaderColumn> columnList, byte columnCount) {
        this.columnList = columnList;
        this.columnCount = columnCount;
    }

    public byte[] serialize() {
        ByteBuffer byteBuffer = ByteBuffer.allocate(1 + columnCount * PageHeaderColumn.SIZE);
        byteBuffer.put(columnCount);
        columnList.stream().map(PageHeaderColumn::serialize).forEach(byteBuffer::put);
        return byteBuffer.array();
    }

    public static PageHeader deserialize(byte[] bytes) {
        byte columnCount = bytes[0];
        List<PageHeaderColumn> columns = new ArrayList<>();

        for(int i=0; i<columnCount; i++) {
            byte columnNumber = bytes[i*2];
            byte attributeType = bytes[(i*2)+1];

            columns.add(new PageHeaderColumn(columnNumber, attributeType));
        }

        return new PageHeader(columns, columnCount);
    }

    public static PageHeader fromAttributes(List<Attribute.TYPES> attributeTypes) {
        List<PageHeaderColumn> columnList = new ArrayList<>();

        for(int i=0; i<attributeTypes.size(); i++) {
            Attribute.TYPES type = attributeTypes.get(i);

            byte columnNumber = (byte) (i+1);
            byte attributeNumber = type.id;

            columnList.add(new PageHeaderColumn(columnNumber, attributeNumber));
        }

        return new PageHeader(columnList, (byte) columnList.size());
    }
}
