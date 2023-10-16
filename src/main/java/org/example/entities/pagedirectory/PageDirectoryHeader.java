package org.example.entities.pagedirectory;

import java.nio.ByteBuffer;

public class PageDirectoryHeader {
    private int pageCount;

    private PageDirectoryHeader(int pageCount) {
        this.pageCount = pageCount;
    }

    public byte[] serialize() {
        ByteBuffer byteBuffer = ByteBuffer.allocate(getSerializedLength());
        byteBuffer.putInt(this.pageCount);
        return byteBuffer.array();
    }

    public static PageDirectoryHeader deserialize(byte[] bytes) {
        ByteBuffer byteBuffer = ByteBuffer.wrap(bytes);
        int pageCount = byteBuffer.getInt();
        return new PageDirectoryHeader(pageCount);
    }

    public static int getSerializedLength() {
        return Integer.BYTES;
    }

    public int getPageCount() {
        return this.pageCount;
    }

    public void incrementPageCount() {
        this.pageCount++;
    }
}
