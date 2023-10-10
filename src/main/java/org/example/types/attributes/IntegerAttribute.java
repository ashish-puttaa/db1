package org.example.types.attributes;

import org.example.types.Attribute;
import java.nio.ByteBuffer;

public class IntegerAttribute extends Attribute {
    public int value;
    public static final Attribute.TYPES type = Attribute.TYPES.INTEGER;

    public IntegerAttribute(int value) {
        this.value = value;
    }

    public static IntegerAttribute fromBytes(byte[] bytes) {
        ByteBuffer buffer = ByteBuffer.wrap(bytes);
        return new IntegerAttribute(buffer.getInt());
    }
}
