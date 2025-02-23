package org.example.util;

import java.io.IOException;
import java.io.RandomAccessFile;
import java.nio.ByteBuffer;
import java.nio.charset.StandardCharsets;
import java.nio.file.Path;
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

    public static byte[] shrinkByteArrayFromFront(byte[] bytes, int lengthToShrink) {
        int shrunkLength = bytes.length - lengthToShrink;
        byte[] shrunkTupleBytes = new byte[shrunkLength];

        System.arraycopy(bytes, lengthToShrink, shrunkTupleBytes, 0, shrunkLength);
        return shrunkTupleBytes;
    }

    public static byte[] readNBytes(Path filePath, int n, int startOffset) throws IOException {
        try (RandomAccessFile randomAccessFile = new RandomAccessFile(filePath.toFile(), "r")) {
            byte[] bytes = new byte[n];
            randomAccessFile.seek(startOffset);
            randomAccessFile.readFully(bytes);

            return bytes;
        }
    }

    public static void writeNBytes(Path filePath, int n, int startOffset, byte[] data) throws IOException {
        try (RandomAccessFile randomAccessFile = new RandomAccessFile(filePath.toFile(), "rw")) {
            randomAccessFile.seek(startOffset);
            randomAccessFile.write(data, 0, n);
        }
    }
}
