package org.example.types.attributes;

public interface Attribute {
    enum TYPES {
        INTEGER((byte) 1, 4),
        STRING((byte) 2, 1024);

        public final byte id;
        public final int size;
        TYPES(byte id, int size) {
            this.id = id;
            this.size = size;
        }
    }

    String toString();
}
