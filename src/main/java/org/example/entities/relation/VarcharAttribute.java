package org.example.entities.relation;

import org.example.util.ByteUtil;

import java.nio.ByteBuffer;
import java.nio.charset.StandardCharsets;

public class VarcharAttribute implements Attribute<String> {
    public short size;
    public String value;

    private VarcharAttribute(short length, String value) {
        this.size = length;
        this.value = value;
    }

    public VarcharAttribute(String value) {
        this.size = (short) value.length();
        this.value = value;
    }

    @Override
    public String getValue() {
        return this.value;
    }

    @Override
    public void setValue(String value) {
        this.value = value;
    }

    @Override
    public AttributeType getType() {
        return AttributeType.VARCHAR;
    }

    @Override
    public byte[] serialize() {
        byte[] valueBytes = ByteUtil.convertToByteArray(this.value);

        ByteBuffer byteBuffer = ByteBuffer.allocate(Short.BYTES + valueBytes.length);
        byteBuffer.putShort(this.size);
        byteBuffer.put(valueBytes);

        return byteBuffer.array();
    }

    @Override
    public int getSerializedLength() {
        return Short.BYTES + this.value.getBytes(StandardCharsets.UTF_8).length;
    }

    public static VarcharAttribute deserialize(byte[] bytes) {
        ByteBuffer byteBuffer = ByteBuffer.wrap(bytes);

        short length = byteBuffer.getShort();
        byte[] valueBytes = ByteUtil.readNBytes(byteBuffer, byteBuffer.remaining());
        String value = new String(valueBytes, StandardCharsets.UTF_8).trim();

        return new VarcharAttribute(length, value);
    }

    public static short getSerializedSizeLength() {
        return Short.BYTES;
    }

    public static short getSize(byte[] bytes) {
        return ByteBuffer.wrap(bytes).getShort();
    }
}
