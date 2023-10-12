package org.example.entities.directory;

import java.nio.ByteBuffer;

public class PageSlotArrayEntry {
    short pageOffset;
    short tupleLength;

    public PageSlotArrayEntry(short pageOffset, short tupleLength) {
        this.pageOffset = pageOffset;
        this.tupleLength = tupleLength;
    }

    public byte[] serialize() {
        ByteBuffer byteBuffer = ByteBuffer.allocate(getSerializedLength());
        byteBuffer.putShort(this.pageOffset);
        byteBuffer.putShort(this.tupleLength);
        return byteBuffer.array();
    }

    public static PageSlotArrayEntry deserialize(byte[] bytes) {
        ByteBuffer byteBuffer = ByteBuffer.wrap(bytes);
        short pageOffset = byteBuffer.getShort();
        short tupleLength = byteBuffer.getShort();
        return new PageSlotArrayEntry(pageOffset, tupleLength);
    }

    public static int getSerializedLength() {
        return Short.BYTES + Short.BYTES;
    }
}
