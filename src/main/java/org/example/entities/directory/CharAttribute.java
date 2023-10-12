package org.example.entities.directory;

import org.example.util.ByteUtil;

import java.nio.charset.StandardCharsets;

public class CharAttribute implements Attribute<String> {
    public String value;

    public CharAttribute(String value) {
        this.value = value;
    }

    public String getValue() {
        return this.value;
    }

    @Override
    public AttributeType getType() {
        return AttributeType.CHAR;
    }

    @Override
    public byte[] serialize() {
        return ByteUtil.convertToPaddedByteArray(this.value, this.getType().size);
    }

    public static CharAttribute deserialize(byte[] bytes) {
        String value = new String(bytes, StandardCharsets.UTF_8).trim();
        return new CharAttribute(value);
    }
}
