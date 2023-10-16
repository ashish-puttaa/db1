package org.example.entities.directory;

import org.example.Constants;
import org.example.util.ByteUtil;

import java.nio.ByteBuffer;

// TODO: Add the compressed and inplace (or very large values (and blob types?)) flags
public class PageSlot {
    public final short slotIndex;
    private boolean hasData;
    public short pageOffset;
    public short tupleLength;

    public PageSlot(short slotIndex, short pageOffset, short tupleLength) {
        this(slotIndex, true, pageOffset, tupleLength);
    }

    private PageSlot(short slotIndex, boolean hasData, short pageOffset, short tupleLength) {
        this.slotIndex = slotIndex;
        this.hasData = hasData;
        this.pageOffset = pageOffset;
        this.tupleLength = tupleLength;
    }

    private PageSlot(short slotIndex) {
        this.slotIndex = slotIndex;
        this.setToEmpty();
    }

    public byte[] serialize() {
        ByteBuffer byteBuffer = ByteBuffer.allocate(getSerializedLength());
        byteBuffer.put(ByteUtil.convertBooleanToByte(this.hasData));
        byteBuffer.putShort(this.pageOffset);
        byteBuffer.putShort(this.tupleLength);
        return byteBuffer.array();
    }

    public static PageSlot deserialize(byte[] bytes, short slotIndex) {
        ByteBuffer byteBuffer = ByteBuffer.wrap(bytes);

        boolean hasData = ByteUtil.convertByteToBoolean(byteBuffer.get());
        if(!hasData) {
            return new PageSlot(slotIndex);
        }

        short pageOffset = byteBuffer.getShort();
        short tupleLength = byteBuffer.getShort();
        return new PageSlot(slotIndex, true, pageOffset, tupleLength);
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
