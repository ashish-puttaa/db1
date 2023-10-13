package org.example.entities.directory;

import org.example.util.ByteUtil;

import java.nio.ByteBuffer;
import java.nio.charset.StandardCharsets;

public class VarcharAttribute implements Attribute<String> {
    public short length;
    public String value;

    public VarcharAttribute(short length, String value) {
        this.length = length;
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
        byteBuffer.putShort(this.length);
        byteBuffer.put(valueBytes);

        return byteBuffer.array();
    }

    public static VarcharAttribute deserialize(byte[] bytes) {
        ByteBuffer byteBuffer = ByteBuffer.wrap(bytes);

        short length = byteBuffer.getShort();
        byte[] valueBytes = ByteUtil.readNBytes(byteBuffer, byteBuffer.remaining());
        String value = new String(valueBytes, StandardCharsets.UTF_8).trim();

        return new VarcharAttribute(length, value);
    }
}
