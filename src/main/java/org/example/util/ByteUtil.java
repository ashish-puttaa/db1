package org.example.util;

import java.nio.ByteBuffer;
import java.nio.charset.StandardCharsets;
import java.util.Arrays;

public class ByteUtil {
    public static int getInteger(byte[] bytes, int fromOffset, int toOffset) {
        byte[] intBytes = Arrays.copyOfRange(bytes, fromOffset, toOffset);
        return ByteBuffer.wrap(intBytes).getInt();
    }

    public static byte[] readNBytes(ByteBuffer byteBuffer, int n) {
        byte[] bytes = new byte[n];
        byteBuffer.get(bytes);
        return bytes;
    }

    public static byte[] convertToByteArray(String input) {
        return input.getBytes(StandardCharsets.UTF_8);
    }

    public static byte[] convertToPaddedByteArray(String input, int desiredLength) {
        byte[] byteArray = convertToByteArray(input);

        if(byteArray.length >= desiredLength) {
            return byteArray;
        }

        return Arrays.copyOf(byteArray, desiredLength);
    }

    public static byte convertBooleanToByte(boolean flag) {
        return (byte) (flag ? 1 : 0);
    }

    public static boolean convertByteToBoolean(byte flag) {
        return flag != 0;
    }
}
