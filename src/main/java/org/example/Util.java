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

    public static Page generateSamplePage(String id, int pageSize) {
        String content = Util.generateUTF8String(
                pageSize, String.format("start%s__", id) , String.format("__end%s\n", id)
        );

        StringAttribute stringAttribute = new StringAttribute(content);
        Tuple tuple = new Tuple(Collections.singletonList(stringAttribute));

        return new Page(Collections.singletonList(tuple));
    }

    public static Page readPageFromFile(Path path, int offset, int pageSize, List<Attribute.TYPES> attributeTypesList) throws IOException {
        try(RandomAccessFile randomAccessFile = new RandomAccessFile(path.toFile(), "r")) {
            byte[] pageBytes = new byte[pageSize];

            randomAccessFile.seek(offset);
            randomAccessFile.readFully(pageBytes);

            return Page.fromBytes(pageBytes, attributeTypesList);
        }
    }

    public static Relation readRelationFromFile(Path path, int pageSize, List<Attribute.TYPES> attributeTypesList) throws IOException {
        try (RandomAccessFile randomAccessFile = new RandomAccessFile(path.toFile(), "r")) {
            byte[] buffer = new byte[pageSize];
            int bytesRead;

            List<byte[]> pageList = new ArrayList<>();

            while ((bytesRead = randomAccessFile.read(buffer)) != -1) {
                byte[] result = new byte[pageSize];
                System.arraycopy(buffer, 0, result, 0, bytesRead);
                pageList.add(result);
            }

            return Relation.fromBytes(path, pageList, attributeTypesList);
        }
    }

    public static byte[][] splitByteArray(byte[] bytes, int chunkSize) {
        int length = bytes.length;
        int numOfChunks = (int) Math.ceil((double) length / chunkSize);

        byte[][] parts = new byte[numOfChunks][chunkSize];

        for (int i = 0; i < numOfChunks; i++) {
            int fromIndex = i * chunkSize;
            int toIndex = Math.min((i + 1) * chunkSize, length);

            parts[i] = Arrays.copyOfRange(bytes, fromIndex, toIndex);
        }

        return parts;
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
