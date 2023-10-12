package org.example.entities.directory;

import org.example.util.ByteUtil;

import java.nio.ByteBuffer;
import java.util.Arrays;
import java.util.List;
import java.util.stream.IntStream;

public class PageColumnMetadataArray {
    public PageColumnMetadata[] metadataArray;

    public PageColumnMetadataArray(PageColumnMetadata[] metadataArray) {
        this.metadataArray = metadataArray;
    }

    public int getTupleLength() {
        return Arrays.stream(this.metadataArray).mapToInt(columnMetaData -> columnMetaData.attributeType.size).sum();
    }

    public byte[] serialize() {
        ByteBuffer byteBuffer = ByteBuffer.allocate(this.metadataArray.length * PageColumnMetadata.getSerializedLength());

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

    public static int getSerializedLength(int numColumns) {
        return numColumns * PageColumnMetadata.getSerializedLength();
    }

    public static PageColumnMetadataArray fromAttributes(List<Attribute.TYPES> attributeTypes) {
        PageColumnMetadata[] columnMetadataArray = new PageColumnMetadata[attributeTypes.size()];

        for (int i = 0; i < attributeTypes.size(); i++) {
            Attribute.TYPES type = attributeTypes.get(i);
            byte columnNumber = (byte) (i + 1);
            columnMetadataArray[i] = new PageColumnMetadata(columnNumber, type);
        }

        return new PageColumnMetadataArray(columnMetadataArray);
    }
}
