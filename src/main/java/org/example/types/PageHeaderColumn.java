package org.example.types;

public class PageHeaderColumn {
    byte columnNumber;
    byte attributeType;
    public static final int SIZE = 2;

    public PageHeaderColumn(byte columnNumber, byte attributeType) {
        this.columnNumber = columnNumber;
        this.attributeType = attributeType;
    }

    public byte[] serialize() {
        return new byte[] { columnNumber, attributeType };
    }

    public static PageHeaderColumn deserialize(byte[] bytes) {
        return new PageHeaderColumn(bytes[0], bytes[1]);
    }
}
