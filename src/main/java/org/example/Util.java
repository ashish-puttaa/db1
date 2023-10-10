package org.example;

import org.example.types.Attribute;
import org.example.types.Page;
import org.example.types.Relation;
import org.example.types.Tuple;
import org.example.types.attributes.StringAttribute;

import java.io.IOException;
import java.io.RandomAccessFile;
import java.nio.charset.StandardCharsets;
import java.nio.file.Path;
import java.util.*;

public class Util {
    public static String generateUTF8String(int length, String prefix, String suffix) {
        StringBuilder stringBuilder = new StringBuilder();
        Random random = new Random();

        int desiredLength = length - prefix.getBytes().length - suffix.getBytes().length;

        while (stringBuilder.toString().getBytes().length < desiredLength) {
            char randomChar = (char) (random.nextInt(26) + 'a');  // Generates a random lowercase letter
            stringBuilder.append(randomChar);
        }

        return prefix + stringBuilder + suffix;
    }

    public static String generateUTF8String(int length) {
        StringBuilder stringBuilder = new StringBuilder();
        Random random = new Random();

        while (stringBuilder.toString().getBytes().length < length) {
            char randomChar = (char) (random.nextInt(26) + 'a');  // Generates a random lowercase letter
            stringBuilder.append(randomChar);
        }

        return stringBuilder.toString();
    }

    public static Page generateSamplePage(int id, int pageSize) {
        String content = Util.generateUTF8String(pageSize - Constants.PAGE_HEADER.length() - Constants.PAGE_FOOTER.length());
        StringAttribute stringAttribute = new StringAttribute(content);
        Tuple tuple = new Tuple(Collections.singletonList(stringAttribute));

        return new Page(Collections.singletonList(tuple), id);
    }

    public static Page readPageFromFile(Path path, int offset, int pageSize, List<Attribute.TYPES> attributeTypesList) throws IOException {
        try(RandomAccessFile randomAccessFile = new RandomAccessFile(path.toFile(), "r")) {
            byte[] pageBytes = new byte[pageSize];

            randomAccessFile.seek(offset);
            randomAccessFile.readFully(pageBytes);

            return Page.fromBytes(pageBytes, attributeTypesList);
        }
    }

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

    // private static byte[][] splitByteArray(byte[] bytes, int[] chunkSizes) {
    //     int length = bytes.length;
    //     int numOfChunks = chunkSizes.length;
    //
    //     byte[][] parts = new byte[numOfChunks][];
    //
    //     int currentIndex = 0;
    //     for (int i = 0; i < numOfChunks; i++) {
    //         int currentChunkSize = chunkSizes[i];
    //         int toIndex = Math.min(currentIndex + currentChunkSize, length);
    //
    //         parts[i] = Arrays.copyOfRange(bytes, currentIndex, toIndex);
    //         currentIndex = toIndex;
    //     }
    //
    //     return parts;
    // }
}
