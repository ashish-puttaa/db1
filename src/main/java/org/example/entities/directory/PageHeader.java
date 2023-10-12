package org.example.entities.directory;

import java.nio.ByteBuffer;

public class PageHeader {
    public byte columnCount;
    public int pageIdentifier;
    public short tupleCount;

    public PageHeader(byte columnCount, int pageIdentifier, short tupleCount) {
        this.columnCount = columnCount;
        this.pageIdentifier = pageIdentifier;
        this.tupleCount = tupleCount;
    }

    public byte[] serialize() {
        ByteBuffer byteBuffer = ByteBuffer.allocate(getSerializedLength());

        byteBuffer.put(this.columnCount);
        byteBuffer.putInt(this.pageIdentifier);
        byteBuffer.putShort(this.tupleCount);

        return byteBuffer.array();
    }

    public static PageHeader deserialize(byte[] bytes) {
        ByteBuffer byteBuffer = ByteBuffer.wrap(bytes);

        byte columnCount = byteBuffer.get();
        int pageIdentifier = byteBuffer.getInt();
        short tupleCount = byteBuffer.getShort();

        return new PageHeader(columnCount, pageIdentifier, tupleCount);
    }

    public static int getSerializedLength() {
        int columnCountLength = Byte.BYTES;
        int pageIdentifierLength = Integer.BYTES;
        int tupleCountLength = Short.BYTES;

        return columnCountLength + pageIdentifierLength + tupleCountLength;
    }
}
