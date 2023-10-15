package org.example.util;

import java.io.IOException;
import java.io.RandomAccessFile;
import java.nio.file.Path;
import java.util.*;

public class CommonUtil {
    @Deprecated
    public static List<byte[]> readFileAsChunks(Path path, int chunkSize) throws IOException {
        try (RandomAccessFile randomAccessFile = new RandomAccessFile(path.toFile(), "r")) {
            byte[] buffer = new byte[chunkSize];
            int bytesRead;

            List<byte[]> chunksList = new ArrayList<>();

            while ((bytesRead = randomAccessFile.read(buffer)) != -1) {
                byte[] result = new byte[chunkSize];
                System.arraycopy(buffer, 0, result, 0, bytesRead);
                chunksList.add(result);
            }

            return chunksList;
        }
    }

    public static List<byte[]> splitByteArray(byte[] bytes, int chunkSize) {
        int length = bytes.length;
        int numOfChunks = (int) Math.ceil((double) length / chunkSize);

        List<byte[]> chunksList = new ArrayList<>(numOfChunks);

        for (int i = 0; i < numOfChunks; i++) {
            int fromIndex = i * chunkSize;
            int toIndex = Math.min((i + 1) * chunkSize, length);

            byte[] chunk = Arrays.copyOfRange(bytes, fromIndex, toIndex);
            chunksList.add(chunk);
        }

        return chunksList;
    }

    @Deprecated
    private static byte[][] splitByteArray(byte[] bytes, int[] chunkSizes) {
        int length = bytes.length;
        int numOfChunks = chunkSizes.length;

        byte[][] parts = new byte[numOfChunks][];

        int currentIndex = 0;
        for (int i = 0; i < numOfChunks; i++) {
            int currentChunkSize = chunkSizes[i];
            int toIndex = Math.min(currentIndex + currentChunkSize, length);

            parts[i] = Arrays.copyOfRange(bytes, currentIndex, toIndex);
            currentIndex = toIndex;
        }

        return parts;
    }
}
