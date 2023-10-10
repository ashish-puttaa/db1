package org.example.types.attributes;

import org.example.types.Attribute;

import java.nio.charset.StandardCharsets;

public class StringAttribute extends Attribute {
    public String value;
    public static final TYPES type = TYPES.STRING;

    public StringAttribute(String value) {
        this.value = value;
    }

    public static StringAttribute fromBytes(byte[] bytes) {
        String value = new String(bytes, StandardCharsets.UTF_8);
        return new StringAttribute(value);
    }
}
