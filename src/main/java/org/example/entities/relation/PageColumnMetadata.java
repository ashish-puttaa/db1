package org.example.entities.relation;

public class PageColumnMetadata {
    public byte columnNumber;
    public AttributeType attributeType;

    public PageColumnMetadata(byte columnNumber, AttributeType attributeType) {
        this.columnNumber = columnNumber;
        this.attributeType = attributeType;
    }

    public byte[] serialize() {
        return new byte[] { columnNumber, attributeType.id };
    }

    public static PageColumnMetadata deserialize(byte[] bytes) {
        byte columnNumber = bytes[0];
        byte attributeTypeId = bytes[1];

        AttributeType attributeType = AttributeType.getTypeById(attributeTypeId);
        return new PageColumnMetadata(columnNumber, attributeType);
    }

    public static int getSerializedLength() {
        return Byte.BYTES + Byte.BYTES;
    }
}
