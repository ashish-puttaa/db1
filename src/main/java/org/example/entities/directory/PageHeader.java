package org.example.entities.directory;

import java.nio.ByteBuffer;

public class PageHeader {
    public byte columnCount;
    public int pageIdentifier;
    public short slotCount;

    public PageHeader(byte columnCount, int pageIdentifier, short slotCount) {
        this.columnCount = columnCount;
        this.pageIdentifier = pageIdentifier;
        this.slotCount = slotCount;
    }

    public PageHeader(byte columnCount, int pageIdentifier) {
        this(columnCount, pageIdentifier, (short) 0);
    }

    public byte[] serialize() {
        ByteBuffer byteBuffer = ByteBuffer.allocate(getSerializedLength());

        byteBuffer.put(this.columnCount);
        byteBuffer.putInt(this.pageIdentifier);
        byteBuffer.putShort(this.slotCount);

        return byteBuffer.array();
    }

    public static PageHeader deserialize(byte[] bytes) {
        ByteBuffer byteBuffer = ByteBuffer.wrap(bytes);

        byte columnCount = byteBuffer.get();
        int pageIdentifier = byteBuffer.getInt();
        short slotCount = byteBuffer.getShort();

        return new PageHeader(columnCount, pageIdentifier, slotCount);
    }

    public static int getSerializedLength() {
        int columnCountLength = Byte.BYTES;
        int pageIdentifierLength = Integer.BYTES;
        int slotCountLength = Short.BYTES;

        return columnCountLength + pageIdentifierLength + slotCountLength;
    }

    public void incrementSlotCount() {
        this.slotCount++;
    }
}
