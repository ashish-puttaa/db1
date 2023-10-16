package org.example.entities.relation;

import java.nio.ByteBuffer;

public class IntegerAttribute implements Attribute<Integer> {
    private Integer value;

    public IntegerAttribute(int value) {
        this.value = value;
    }

    @Override
    public Integer getValue() {
        return this.value;
    }

    @Override
    public void setValue(Integer value) {
        this.value = value;
    }

    @Override
    public AttributeType getType() {
        return AttributeType.INTEGER;
    }

    @Override
    public byte[] serialize() {
        ByteBuffer byteBuffer = ByteBuffer.allocate(this.getType().size);
        byteBuffer.putInt(this.value);
        return byteBuffer.array();
    }

    @Override
    public int getSerializedLength() {
        return this.getType().size;
    }

    public static IntegerAttribute deserialize(byte[] bytes) {
        ByteBuffer buffer = ByteBuffer.wrap(bytes);
        return new IntegerAttribute(buffer.getInt());
    }
}
