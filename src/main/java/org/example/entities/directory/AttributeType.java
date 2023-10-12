package org.example.entities.directory;

public enum AttributeType {
    INTEGER((byte) 1, 4),
    STRING((byte) 2, 255);

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