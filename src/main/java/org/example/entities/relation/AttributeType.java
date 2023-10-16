package org.example.entities.relation;

public enum AttributeType {
    INTEGER((byte) 1, 4),
    CHAR((byte) 2, 255),
    VARCHAR((byte) 3, 255);

    public final byte id;
    public final int size;

    AttributeType(byte id, int size) {
        this.id = id;
        this.size = size;
    }

    public static AttributeType getTypeById(byte id) {
        for (AttributeType type : AttributeType.values()) {
            if (type.id == id) return type;
        }
        throw new IllegalArgumentException("No AttributeType found with id: " + id);
    }
}