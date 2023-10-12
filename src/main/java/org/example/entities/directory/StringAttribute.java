package org.example.entities.directory;

import org.example.util.ByteUtil;

import java.nio.charset.StandardCharsets;

public class StringAttribute implements Attribute<String> {
    public String value;

    public StringAttribute(String value) {
        this.value = value;
    }

    public String getValue() {
        return this.value;
    }

    @Override
    public AttributeType getType() {
        return AttributeType.STRING;
    }

    @Override
    public byte[] serialize() {
        return ByteUtil.convertStringToByteArray(this.value, this.getType().size);
    }

    public static StringAttribute deserialize(byte[] bytes) {
        String value = new String(bytes, StandardCharsets.UTF_8).trim();
        return new StringAttribute(value);
    }
}
