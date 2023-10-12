package org.example.entities.directory;

public class PageColumnMetadata {
    public byte columnNumber;
    public Attribute.TYPES attributeType;

    public PageColumnMetadata(byte columnNumber, Attribute.TYPES attributeType) {
        this.columnNumber = columnNumber;
        this.attributeType = attributeType;
    }

    public byte[] serialize() {
        return new byte[] { columnNumber, attributeType.id };
    }

    public static PageColumnMetadata deserialize(byte[] bytes) {
        byte columnNumber = bytes[0];
        byte attributeTypeId = bytes[1];

        Attribute.TYPES attributeType = Attribute.TYPES.getTypeById(attributeTypeId);
        return new PageColumnMetadata(columnNumber, attributeType);
    }

    public static int getSerializedLength() {
        return Byte.BYTES + Byte.BYTES;
    }
}
