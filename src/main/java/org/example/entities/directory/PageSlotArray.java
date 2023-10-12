package org.example.entities.directory;

import org.example.util.ByteUtil;

import java.nio.ByteBuffer;
import java.util.ArrayList;
import java.util.List;

public class PageSlotArray {
    PageSlotArrayEntry[] slotArray;

    public PageSlotArray(PageSlotArrayEntry[] slotArray) {
        this.slotArray = slotArray;
    }

    public byte[] serialize() {
        ByteBuffer byteBuffer = ByteBuffer.allocate(slotArray.length * PageSlotArrayEntry.getSerializedLength());

        for(PageSlotArrayEntry slot: slotArray) {
            byteBuffer.put(slot.serialize());
        }

        return byteBuffer.array();
    }

    public static PageSlotArray deserialize(byte[] bytes, int numSlots) {
        ByteBuffer byteBuffer = ByteBuffer.wrap(bytes);
        PageSlotArrayEntry[] slotArray = new PageSlotArrayEntry[numSlots];

        for (int i = 0; i < numSlots; i++) {
            byte[] slotEntryBytes = ByteUtil.readNBytes(byteBuffer, PageSlotArrayEntry.getSerializedLength());
            slotArray[i] = PageSlotArrayEntry.deserialize(slotEntryBytes);
        }

        return new PageSlotArray(slotArray);
    }

    public static int getSerializedLength(int numSlots) {
        return numSlots * PageSlotArrayEntry.getSerializedLength();
    }

    public static PageSlotArray fromTupleList(List<Tuple> tupleList) {
        List<PageSlotArrayEntry> entryList = new ArrayList<>();
        short currentOffset = 0;

        for(Tuple tuple: tupleList) {
            short pageOffset = currentOffset;
            short tupleLength = (short) tuple.attributeList.stream()
                    .mapToInt(attribute -> attribute.getType().size)
                    .sum();

            PageSlotArrayEntry slotArrayEntry = new PageSlotArrayEntry(pageOffset, tupleLength);
            entryList.add(slotArrayEntry);

            currentOffset += tupleLength;
        }

        return new PageSlotArray(entryList.toArray(PageSlotArrayEntry[]::new));
    }
}
