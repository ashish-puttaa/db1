package org.example.types.attributes;

import java.nio.ByteBuffer;

public class IntegerAttribute implements Attribute {
    public int value;
    public static final Attribute.TYPES type = Attribute.TYPES.INTEGER;

    public IntegerAttribute(int value) {
        this.value = value;
    }

    public static IntegerAttribute fromBytes(byte[] bytes) {
        ByteBuffer buffer = ByteBuffer.wrap(bytes);
        return new IntegerAttribute(buffer.getInt());
    }

    @Override
    public String toString() {
        return String.valueOf(this.value);
    }
}
