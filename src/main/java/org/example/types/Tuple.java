package org.example.types;

import org.example.types.attributes.Attribute;

import java.nio.ByteBuffer;
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
}
