package org.example.entities.directory;

import org.example.Constants;
import org.example.iterators.OccupiedPageSlotsIterator;
import org.example.util.ByteUtil;

import java.nio.ByteBuffer;
import java.util.*;

public class PageSlotArray {
    private final List<PageSlotArrayEntry> slots;

    //METADATA
    private final short slotArrayOffsetStart;
    private final PageHolesMap pageHolesMap;

    private PageSlotArray(List<PageSlotArrayEntry> slots, short slotArrayOffsetStart) {
        this.slots = slots;
        this.slotArrayOffsetStart = slotArrayOffsetStart;

        List<PageSlotArrayEntry> updatedSlotsList = addStartAndEndHolesToSlotsArray(slots, slotArrayOffsetStart);
        this.pageHolesMap = new PageHolesMap(updatedSlotsList, slotArrayOffsetStart);
    }

    public PageSlotArray(short slotArrayOffsetStart) {
        this(new ArrayList<>(), slotArrayOffsetStart);
    }

    public byte[] serialize() {
        ByteBuffer byteBuffer = ByteBuffer.allocate(this.getSerializedLength());

        for(PageSlotArrayEntry slot: this.slots) {
            byteBuffer.put(slot.serialize());
        }

        return byteBuffer.array();
    }

    public static PageSlotArray deserialize(byte[] bytes, int numSlots, short slotsArrayOffsetStart) {
        ByteBuffer byteBuffer = ByteBuffer.wrap(bytes);
        List<PageSlotArrayEntry> slotArray = new ArrayList<>(numSlots);

        for (int i = 0; i < numSlots; i++) {
            byte[] slotEntryBytes = ByteUtil.readNBytes(byteBuffer, PageSlotArrayEntry.getSerializedLength());
            PageSlotArrayEntry slot = PageSlotArrayEntry.deserialize(slotEntryBytes);
            slotArray.add(slot);
        }

        return new PageSlotArray(slotArray, slotsArrayOffsetStart);
    }

    public short getTupleOffsetStart() {
        return getTupleOffsetStart(this.slotArrayOffsetStart, this.slots.size());
    }

    public static short getTupleOffsetStart(int slotArrayOffsetStart, int numSlots) {
        return (short) (slotArrayOffsetStart + getSerializedLength(numSlots));
    }

    public int getSerializedLength() {
        return getSerializedLength(this.slots.size());
    }

    public static int getSerializedLength(int numSlots) {
        return numSlots * PageSlotArrayEntry.getSerializedLength();
    }

    public static PageSlotArray fromTupleList(List<Tuple> tupleList, short slotArrayOffsetStart) {
        List<PageSlotArrayEntry> slotsList = new ArrayList<>();
        short currentOffset = 0;

        for(Tuple tuple: tupleList) {
            short tupleOffsetStart = getTupleOffsetStart(slotArrayOffsetStart, tupleList.size());
            short pageOffset = (short) (tupleOffsetStart + currentOffset);
            short tupleLength = (short) tuple.attributeList.stream()
                    .mapToInt(attribute -> attribute.getType().size)
                    .sum();

            PageSlotArrayEntry slotArrayEntry = new PageSlotArrayEntry(pageOffset, tupleLength);
            slotsList.add(slotArrayEntry);

            currentOffset += tupleLength;
        }

        return new PageSlotArray(slotsList, slotArrayOffsetStart);
    }

    private static List<PageSlotArrayEntry> addStartAndEndHolesToSlotsArray(List<PageSlotArrayEntry> slotsList, short slotArrayOffsetStart) {
        boolean hasTupleStartOffset = slotsList.stream().anyMatch(entry -> entry.pageOffset == getTupleOffsetStart(slotArrayOffsetStart, slotsList.size()));
        boolean hasPageEndOffset = slotsList.stream().anyMatch(entry -> entry.pageOffset + entry.tupleLength == Constants.PAGE_SIZE);

        List<PageSlotArrayEntry> updatedSlotsList = new ArrayList<>(slotsList);

        if(!hasTupleStartOffset) {
            short tupleOffsetStart = getTupleOffsetStart(slotArrayOffsetStart, updatedSlotsList.size());
            PageSlotArrayEntry tupleStartSlot = new PageSlotArrayEntry(tupleOffsetStart, (short) 0);
            updatedSlotsList.add(tupleStartSlot);
        }

        if(!hasPageEndOffset) {
            short pageEndOffset = (short) (Constants.PAGE_SIZE - slotArrayOffsetStart);
            PageSlotArrayEntry pageEndSlot = new PageSlotArrayEntry(pageEndOffset, (short) 0);
            updatedSlotsList.add(pageEndSlot);
        }

        return updatedSlotsList;
    }

    private int appendToSlotArray(PageSlotArrayEntry slotEntry) {
        this.slots.add(slotEntry);
        this.pageHolesMap.shiftTupleStartOffsetOnePositionRight();
        return this.slots.size() - 1;
    }

    public Optional<Short> getHole(short desiredLength) {
        return this.pageHolesMap.getHole(desiredLength);
    }

    public Iterator<PageSlotArrayEntry> getIterator() {
        return new OccupiedPageSlotsIterator(this.slots);
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

    public PageSlotArrayEntry getSlot(int slotIndex) {
        if (slotIndex > this.slots.size() - 1) {
            throw new IndexOutOfBoundsException();
        }

        return this.slots.get(slotIndex);
    }
}
