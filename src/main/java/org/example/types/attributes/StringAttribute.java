package org.example.types.attributes;

import org.example.Util;

import java.nio.charset.StandardCharsets;

public class StringAttribute implements Attribute {
    public String value;
    public final TYPES type = TYPES.STRING;

    public StringAttribute(String value) {
        this.value = value;
    }

    @Override
    public TYPES getType() {
        return this.type;
    }

    @Override
    public byte[] serialize() {
        return Util.convertStringToByteArray(this.value, this.type.size);
    }

    public static StringAttribute deserialize(byte[] bytes) {
        String value = new String(bytes, StandardCharsets.UTF_8);
        return new StringAttribute(value);
    }
}
