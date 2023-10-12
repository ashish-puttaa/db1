package org.example.entities.directory;

import org.example.Constants;
import org.example.util.ByteUtil;

import java.nio.ByteBuffer;

// TODO: Add the compressed and inplace (or very large values (and blob types?)) flags
public class PageSlotArrayEntry {
    private boolean hasData;
    short pageOffset;
    short tupleLength;

    public PageSlotArrayEntry(short pageOffset, short tupleLength) {
        this.setValue(pageOffset, tupleLength);
    }

    private PageSlotArrayEntry(boolean hasData, short pageOffset, short tupleLength) {
        this.hasData = hasData;
        this.pageOffset = pageOffset;
        this.tupleLength = tupleLength;
    }

    public byte[] serialize() {
        ByteBuffer byteBuffer = ByteBuffer.allocate(getSerializedLength());
        byteBuffer.put(ByteUtil.convertBooleanToByte(this.hasData));
        byteBuffer.putShort(this.pageOffset);
        byteBuffer.putShort(this.tupleLength);
        return byteBuffer.array();
    }

    public static PageSlotArrayEntry deserialize(byte[] bytes) {
        ByteBuffer byteBuffer = ByteBuffer.wrap(bytes);
        boolean hasData = ByteUtil.convertByteToBoolean(byteBuffer.get());
        short pageOffset = byteBuffer.getShort();
        short tupleLength = byteBuffer.getShort();
        return new PageSlotArrayEntry(hasData, pageOffset, tupleLength);
    }

    public static int getSerializedLength() {
        return Byte.BYTES + Short.BYTES + Short.BYTES;
    }

    public boolean isEmpty() {
        return !this.hasData;
    }

    public void setToEmpty() {
        this.hasData = false;
        this.pageOffset = Constants.NULL;
        this.tupleLength = Constants.NULL;
    }

    public void setValue(short pageOffset, short tupleLength) {
        this.hasData = true;
        this.pageOffset = pageOffset;
        this.tupleLength = tupleLength;
    }
}
