package org.example.entities.directory;

import org.example.Constants;
import org.example.util.ByteUtil;

import java.nio.ByteBuffer;
import java.util.*;
import java.util.stream.Stream;

public class PageSlotArray {
    List<PageSlotArrayEntry> slots;

    //METADATA
    private final short slotArrayOffsetStart;
    private final PageHolesMap pageHolesMap;

    public PageSlotArray(PageSlotArrayEntry[] slotArray, short slotArrayOffsetStart) {
        this.slots = List.of(slotArray);
        this.slotArrayOffsetStart = slotArrayOffsetStart;

        PageSlotArrayEntry[] updatedSlotArray = addStartAndEndHolesToSlotsArray(slotArray, slotArrayOffsetStart);
        this.pageHolesMap = new PageHolesMap(updatedSlotArray, slotArrayOffsetStart);
    }

    public byte[] serialize() {
        ByteBuffer byteBuffer = ByteBuffer.allocate(this.slots.size() * PageSlotArrayEntry.getSerializedLength());

        for(PageSlotArrayEntry slot: this.slots) {
            byteBuffer.put(slot.serialize());
        }

        return byteBuffer.array();
    }

    public static PageSlotArray deserialize(byte[] bytes, int numSlots, short slotsArrayOffsetStart) {
        ByteBuffer byteBuffer = ByteBuffer.wrap(bytes);
        PageSlotArrayEntry[] slotArray = new PageSlotArrayEntry[numSlots];

        for (int i = 0; i < numSlots; i++) {
            byte[] slotEntryBytes = ByteUtil.readNBytes(byteBuffer, PageSlotArrayEntry.getSerializedLength());
            slotArray[i] = PageSlotArrayEntry.deserialize(slotEntryBytes);
        }

        return new PageSlotArray(slotArray, slotsArrayOffsetStart);
    }

    public short getTupleOffsetStart() {
        return getTupleOffsetStart(this.slots.size(), this.slotArrayOffsetStart);
    }

    public static short getTupleOffsetStart(int slotArrayOffsetStart, int numSlots) {
        return (short) (slotArrayOffsetStart + getSerializedLength(numSlots));
    }

    public static int getSerializedLength(int numSlots) {
        return numSlots * PageSlotArrayEntry.getSerializedLength();
    }

    public static PageSlotArray fromTupleList(List<Tuple> tupleList, short slotArrayOffsetStart) {
        List<PageSlotArrayEntry> entryList = new ArrayList<>();
        short currentOffset = 0;

        for(Tuple tuple: tupleList) {
            short tupleOffsetStart = getTupleOffsetStart(slotArrayOffsetStart, tupleList.size());
            short pageOffset = (short) (tupleOffsetStart + currentOffset);
            short tupleLength = (short) tuple.attributeList.stream()
                    .mapToInt(attribute -> attribute.getType().size)
                    .sum();

            PageSlotArrayEntry slotArrayEntry = new PageSlotArrayEntry(pageOffset, tupleLength);
            entryList.add(slotArrayEntry);

            currentOffset += tupleLength;
        }

        return new PageSlotArray(entryList.toArray(PageSlotArrayEntry[]::new), slotArrayOffsetStart);
    }

    private static PageSlotArrayEntry[] addStartAndEndHolesToSlotsArray(PageSlotArrayEntry[] slotArray, short slotArrayOffsetStart) {
        boolean hasTupleStartOffset = Arrays.stream(slotArray).anyMatch(entry -> entry.pageOffset == getTupleOffsetStart(slotArrayOffsetStart, slotArray.length));
        boolean hasPageEndOffset = Arrays.stream(slotArray).anyMatch(entry -> entry.pageOffset + entry.tupleLength == Constants.PAGE_SIZE);

        if(!hasTupleStartOffset) {
            short tupleOffsetStart = getTupleOffsetStart(slotArrayOffsetStart, slotArray.length);
            PageSlotArrayEntry tupleStartSlot = new PageSlotArrayEntry(tupleOffsetStart, (short) 0);
            return Stream.concat(Arrays.stream(slotArray), Stream.of(tupleStartSlot)).toArray(PageSlotArrayEntry[]::new);
        }

        if(!hasPageEndOffset) {
            short pageEndOffset = (short) (Constants.PAGE_SIZE - getSerializedLength(slotArray.length));
            PageSlotArrayEntry pageEndSlot = new PageSlotArrayEntry(pageEndOffset, (short) 0);
            return Stream.concat(Arrays.stream(slotArray), Stream.of(pageEndSlot)).toArray(PageSlotArrayEntry[]::new);
        }

        return slotArray;
    }

    private int appendToSlotArray(PageSlotArrayEntry slotEntry) {
        this.slots.add(slotEntry);
        this.pageHolesMap.shiftTupleStartOffsetOnePositionRight();
        return this.slots.size() - 1;
    }

    public Optional<Short> getHole(short desiredLength) {
        return this.pageHolesMap.getHole(desiredLength);
    }

    public int insertSlot(short pageOffset, short tupleLength) {
        for(int i = 0; i<this.slots.size(); i++) {
            PageSlotArrayEntry entry = this.slots.get(i);

            if(entry.isEmpty()) {
                entry.setValue(pageOffset, tupleLength);
                return i;
            }
        }

        PageSlotArrayEntry entry = new PageSlotArrayEntry(pageOffset, tupleLength);
        return this.appendToSlotArray(entry);
    }

    public void emptySlot(int index) {
        PageSlotArrayEntry entryToRemove = this.slots.get(index);

        if(!entryToRemove.isEmpty()) {
            entryToRemove.setToEmpty();

            short holeStartOffset = entryToRemove.pageOffset;
            short holeLength = entryToRemove.tupleLength;

            this.pageHolesMap.addHole(holeLength, holeStartOffset);
        }
    }
}
