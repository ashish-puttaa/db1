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

    public static byte[] convertStringToByteArray(String input, int desiredLength) {
        byte[] byteArray = input.getBytes(StandardCharsets.UTF_8);

        if (byteArray.length >= desiredLength) {
            return byteArray;
        }
        else {
            byte[] paddedByteArray = Arrays.copyOf(byteArray, desiredLength);
            return paddedByteArray;
        }
    }
}
