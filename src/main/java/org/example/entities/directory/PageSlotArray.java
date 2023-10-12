package org.example.entities.directory;

import org.example.util.ByteUtil;

import java.nio.ByteBuffer;
import java.util.*;

public class PageSlotArray {
    List<PageSlotArrayEntry> slotArray;

    //METADATA
   PageHoleMap pageHolesMap;

    public PageSlotArray(PageSlotArrayEntry[] slotArray) {
        this.slotArray = List.of(slotArray);
        this.pageHolesMap = new PageHoleMap(slotArray);
    }

    public byte[] serialize() {
        ByteBuffer byteBuffer = ByteBuffer.allocate(this.slotArray.size() * PageSlotArrayEntry.getSerializedLength());

        for(PageSlotArrayEntry slot: this.slotArray) {
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

    public int insertSlot(short pageOffset, short tupleLength) {
        for(int i=0; i<this.slotArray.size(); i++) {
            PageSlotArrayEntry entry = this.slotArray.get(i);

            if(entry.isEmpty()) {
                entry.setValue(pageOffset, tupleLength);
                return i;
            }
        }

        PageSlotArrayEntry entry = new PageSlotArrayEntry(pageOffset, tupleLength);
        this.slotArray.add(entry);
        return this.slotArray.size() - 1;
    }

    public void emptySlot(int index) {
        PageSlotArrayEntry entryToRemove = this.slotArray.get(index);

        if(!entryToRemove.isEmpty()) {
            entryToRemove.setToEmpty();

            short holeStartOffset = entryToRemove.pageOffset;
            short holeLength = entryToRemove.tupleLength;

            this.pageHolesMap.holes.computeIfAbsent(holeLength, k -> new ArrayList<>()).add(holeStartOffset);
        }
    }
}
