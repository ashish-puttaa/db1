package org.example.entities.directory;

import java.nio.ByteBuffer;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

public class Tuple {
    public final List<Attribute> attributeList;

    public Tuple(List<Attribute> attributeList) {
        this.attributeList = attributeList;
    }

    public byte[] serialize() {
        List<byte[]> bytesList = attributeList.stream().map(Attribute::serialize).toList();
        int totalSize = bytesList.stream().mapToInt(bytes -> bytes.length).sum();

        ByteBuffer byteBuffer = ByteBuffer.allocate(totalSize);
        bytesList.forEach(byteBuffer::put);

        return byteBuffer.array();
    }

    public static Tuple deserialize(byte[] bytes, PageColumnMetadataArray columnMetadataArray) {
        List<Attribute> attributeList = new ArrayList<>(columnMetadataArray.metadataArray.length);

        int currentIndex = 0;

        for (PageColumnMetadata column : columnMetadataArray.metadataArray) {
            AttributeType attributeType = column.attributeType;
            int toIndex = Math.min(currentIndex + attributeType.size, bytes.length);
            byte[] chunk = Arrays.copyOfRange(bytes, currentIndex, toIndex);

            Attribute attribute = AttributeFactory.createFromBytes(chunk, attributeType);
            attributeList.add(attribute);

            currentIndex = toIndex;
        }

        return new Tuple(attributeList);
    }
}
