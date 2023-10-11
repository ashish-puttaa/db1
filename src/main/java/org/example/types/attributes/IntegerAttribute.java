package org.example.types.attributes;

import java.nio.ByteBuffer;

public class IntegerAttribute implements Attribute<Integer> {
    public Integer value;
    public final Attribute.TYPES type = Attribute.TYPES.INTEGER;

    public IntegerAttribute(int value) {
        this.value = value;
    }

    @Override
    public Integer getValue() {
        return this.value;
    }

    @Override
    public TYPES getType() {
        return this.type;
    }

    @Override
    public byte[] serialize() {
        ByteBuffer byteBuffer = ByteBuffer.allocate(this.type.size);
        byteBuffer.putInt(this.value);
        return byteBuffer.array();
    }

    public static IntegerAttribute deserialize(byte[] bytes) {
        ByteBuffer buffer = ByteBuffer.wrap(bytes);
        return new IntegerAttribute(buffer.getInt());
    }
}
