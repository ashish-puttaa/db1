package org.example.entities.relation;

import java.nio.ByteBuffer;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

public class PageTuple {
    public record RecordIdentifier(int pageId, short slotIndex) {}

    public final RecordIdentifier recordId;
    public final List<Attribute<?>> attributeList;

    public PageTuple(List<Attribute<?>> attributeList) {
        this.recordId = null;
        this.attributeList = attributeList;
    }

    private PageTuple(RecordIdentifier recordId, List<Attribute<?>> attributeList) {
        this.recordId = recordId;
        this.attributeList = attributeList;
    }

    public byte[] serialize() {
        List<byte[]> bytesList = this.attributeList.stream().map(Attribute::serialize).toList();
        int totalSize = bytesList.stream().mapToInt(bytes -> bytes.length).sum();

        ByteBuffer byteBuffer = ByteBuffer.allocate(totalSize);
        bytesList.forEach(byteBuffer::put);

        return byteBuffer.array();
    }

    public static PageTuple deserialize(byte[] bytes, PageColumnMetadataArray columnMetadataArray, RecordIdentifier recordId) {
        List<Attribute<?>> attributeList = new ArrayList<>(columnMetadataArray.metadataArray.length);

        int currentIndex = 0;

        for (PageColumnMetadata column : columnMetadataArray.metadataArray) {
            AttributeType attributeType = column.attributeType;
            int toIndex = currentIndex;

            if(attributeType.equals(AttributeType.VARCHAR)) {
                byte[] sizeBytes = Arrays.copyOfRange(bytes, currentIndex, currentIndex + VarcharAttribute.getSerializedSizeLength());
                short size = VarcharAttribute.getSize(sizeBytes);
                toIndex += VarcharAttribute.getSerializedSizeLength() + size;
            }
            else {
                toIndex += attributeType.size;
            }

            byte[] chunk = Arrays.copyOfRange(bytes, currentIndex, toIndex);
            Attribute<?> attribute = AttributeFactory.createFromBytes(chunk, attributeType);
            attributeList.add(attribute);

            currentIndex = toIndex;
        }

        return new PageTuple(recordId, attributeList);
    }

    public int getSerializedLength() {
        return this.attributeList.stream().mapToInt(Attribute::getSerializedLength).sum();
    }
}
