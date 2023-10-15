package org.example.entities.directory;

import org.example.util.ByteUtil;

import java.nio.charset.StandardCharsets;

public class CharAttribute implements Attribute<String> {
    private String value;

    public CharAttribute(String value) {
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
        return AttributeType.CHAR;
    }

    @Override
    public byte[] serialize() {
        return ByteUtil.convertToPaddedByteArray(this.value, this.getType().size);
    }

    @Override
    public int getSerializedLength() {
        return this.getType().size;
    }

    public static CharAttribute deserialize(byte[] bytes) {
        String value = new String(bytes, StandardCharsets.UTF_8).trim();
        return new CharAttribute(value);
    }
}
