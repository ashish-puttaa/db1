package org.example.entities.directory;

import org.example.util.ByteUtil;

import java.nio.ByteBuffer;
import java.util.Arrays;
import java.util.List;
import java.util.stream.IntStream;

public class PageColumnMetadataArray {
    public PageColumnMetadata[] metadataArray;

    private PageColumnMetadataArray(PageColumnMetadata[] metadataArray) {
        this.metadataArray = metadataArray;
    }

    public byte[] serialize() {
        ByteBuffer byteBuffer = ByteBuffer.allocate(this.getSerializedLength());

        Arrays.stream(this.metadataArray)
                .map(PageColumnMetadata::serialize)
                .forEach(byteBuffer::put);

        return byteBuffer.array();
    }

    public static PageColumnMetadataArray deserialize(byte[] bytes, int numColumns) {
        ByteBuffer byteBuffer = ByteBuffer.wrap(bytes);

        PageColumnMetadata[] metadataArray = IntStream.range(0, numColumns)
                .mapToObj(i -> ByteUtil.readNBytes(byteBuffer, PageColumnMetadata.getSerializedLength()))
                .map(PageColumnMetadata::deserialize)
                .toArray(PageColumnMetadata[]::new);

        return new PageColumnMetadataArray(metadataArray);
    }

    public int getSerializedLength() {
        return getSerializedLength(this.metadataArray.length);
    }

    public static int getSerializedLength(int numColumns) {
        return numColumns * PageColumnMetadata.getSerializedLength();
    }

    public static PageColumnMetadataArray fromAttributes(List<AttributeType> attributeTypes) {
        PageColumnMetadata[] columnMetadataArray = new PageColumnMetadata[attributeTypes.size()];

        for (int i = 0; i < attributeTypes.size(); i++) {
            AttributeType type = attributeTypes.get(i);
            byte columnNumber = (byte) (i + 1);
            columnMetadataArray[i] = new PageColumnMetadata(columnNumber, type);
        }

        return new PageColumnMetadataArray(columnMetadataArray);
    }
}
