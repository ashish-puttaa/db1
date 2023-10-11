package org.example.types.attributes;

public interface Attribute<T> {
    enum TYPES {
        INTEGER((byte) 1, 4),
        STRING((byte) 2, 255);

        public final byte id;
        public final int size;

        TYPES(byte id, int size) {
            this.id = id;
            this.size = size;
        }

        public static TYPES getTypeById(byte id) {
            for (TYPES type : TYPES.values()) {
                if (type.id == id) {
                    return type;
                }
            }
            // Handle the case when no matching type is found
            throw new IllegalArgumentException("No TYPE found with id: " + id);
        }
    }

    T getValue();
    TYPES getType();
    byte[] serialize();
    static Attribute deserialize(byte[] bytes) { return null; }
}
