package org.example.types.attributes;

import org.example.Util;

import java.nio.charset.StandardCharsets;

public class StringAttribute implements Attribute<String> {
    public String value;
    public final TYPES type = TYPES.STRING;

    public StringAttribute(String value) {
        this.value = value;
    }

    public String getValue() {
        return this.value;
    }

    @Override
    public TYPES getType() {
        return this.type;
    }

    @Override
    public byte[] serialize() {
        return convertStringToByteArray(this.value, this.type.size);
    }

    public static StringAttribute deserialize(byte[] bytes) {
        String value = new String(bytes, StandardCharsets.UTF_8).trim();
        return new StringAttribute(value);
    }

    private static byte[] convertStringToByteArray(String input, int desiredLength) {
        byte[] byteArray = input.getBytes(StandardCharsets.UTF_8);

        if (byteArray.length >= desiredLength) {
            return byteArray;
        }

        byte[] paddedByteArray = new byte[desiredLength];
        System.arraycopy(byteArray, 0, paddedByteArray, 0, byteArray.length);

        return paddedByteArray;
    }
}
