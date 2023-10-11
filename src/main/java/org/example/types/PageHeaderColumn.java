package org.example.types;

import org.example.types.attributes.Attribute;

public class PageHeaderColumn {
    byte columnNumber;
    Attribute.TYPES attributeType;
    public static final int SIZE = 2;

    public PageHeaderColumn(byte columnNumber, Attribute.TYPES attributeType) {
        this.columnNumber = columnNumber;
        this.attributeType = attributeType;
    }

    public byte[] serialize() {
        return new byte[] { columnNumber, attributeType.id };
    }

    public static PageHeaderColumn deserialize(byte[] bytes) {
        Attribute.TYPES attributeType = Attribute.TYPES.getTypeById(bytes[1]);
        return new PageHeaderColumn(bytes[0], attributeType);
    }
}
