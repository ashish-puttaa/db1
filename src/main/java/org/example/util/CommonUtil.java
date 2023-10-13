package org.example.util;

import org.example.Constants;
import org.example.entities.directory.*;

import java.io.IOException;
import java.io.RandomAccessFile;
import java.nio.file.Path;
import java.util.*;

public class CommonUtil {
    public static String generateUTF8String(int length) {
        return generateUTF8String(length, "", "");
    }

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

    public static Page generateSamplePage(int id) {

        AttributeType[] types = { AttributeType.CHAR, AttributeType.INTEGER, AttributeType.CHAR};

        PageColumnMetadataArray columnMetadataArray = PageColumnMetadataArray.fromAttributes(Arrays.asList(types));
        List<Tuple> tupleList = new ArrayList<>();

        Random random = new Random();
        short numTuples = (short) (Constants.PAGE_SIZE / columnMetadataArray.getTupleLength());

        for(int i=0; i<numTuples; i++) {
            List<Attribute<?>> attributeList = new ArrayList<>();

            for(AttributeType type: types) {
                switch (type) {
                    case CHAR -> {
                        double lowerBound = 0.25;
                        double upperBound = 0.75;
                        double randomPercentage = lowerBound + (upperBound - lowerBound) * random.nextDouble();

                        int length = (int) (AttributeType.CHAR.size * randomPercentage);
                        String prefix = String.format("string:%d-%d__", id, i+1);
                        String content = CommonUtil.generateUTF8String(length - prefix.length());
                        attributeList.add(new CharAttribute(prefix + content));
                    }
                    case INTEGER -> {
                        int content = random.nextInt();
                        attributeList.add(new IntegerAttribute(content));
                    }
                }
            }

            tupleList.add(new Tuple(attributeList));
        }

        PageHeader pageHeader = new PageHeader((byte) columnMetadataArray.metadataArray.length, id, numTuples);

        short pageSlotArrayOffsetStart = (short) (PageHeader.getSerializedLength() + PageColumnMetadataArray.getSerializedLength(pageHeader.columnCount) + 1);
        PageSlotArray pageSlotArray = PageSlotArray.fromTupleList(tupleList, pageSlotArrayOffsetStart);

        return new Page(pageHeader, columnMetadataArray, pageSlotArray, tupleList);
    }

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
