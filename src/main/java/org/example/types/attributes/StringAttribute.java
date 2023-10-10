package org.example.types.attributes;

import java.nio.charset.StandardCharsets;

public class StringAttribute implements Attribute {
    public String value;
    public static final TYPES type = TYPES.STRING;

    public StringAttribute(String value) {
        this.value = value;
    }

    public static StringAttribute fromBytes(byte[] bytes) {
        String value = new String(bytes, StandardCharsets.UTF_8);
        return new StringAttribute(value);
    }

    @Override
    public String toString() {
        return this.value;
    }
}
