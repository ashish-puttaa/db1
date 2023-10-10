package org.example.types;

public class Attribute {
    public static enum TYPES {
        INTEGER(4),
        STRING(1024);

        final int size;
        TYPES(int size) {
            this.size = size;
        }
    }
}
