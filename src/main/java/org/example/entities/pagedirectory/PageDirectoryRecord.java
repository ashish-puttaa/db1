package org.example.entities.pagedirectory;

import java.nio.ByteBuffer;

public record PageDirectoryRecord(int databaseFileId, int pageNumber) {
    public byte[] serialize() {
        ByteBuffer byteBuffer = ByteBuffer.allocate(getSerializedLength());
        byteBuffer.putInt(this.databaseFileId);
        byteBuffer.putInt(this.pageNumber);
        return byteBuffer.array();
    }

    public static PageDirectoryRecord deserialize(byte[] bytes) {
        ByteBuffer byteBuffer = ByteBuffer.wrap(bytes);
        int databaseFileId = byteBuffer.getInt();
        int pageNumber = byteBuffer.getInt();
        return new PageDirectoryRecord(databaseFileId, pageNumber);
    }

    public static int getSerializedLength() {
        return Integer.BYTES + Integer.BYTES;
    }
}
