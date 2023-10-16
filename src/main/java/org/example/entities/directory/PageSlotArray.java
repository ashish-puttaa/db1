package org.example.entities.directory;

import org.example.Constants;
import org.example.iterators.OccupiedPageSlotsIterator;
import org.example.util.ByteUtil;

import java.nio.ByteBuffer;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.Optional;

public class PageSlotArray {
    private final List<PageSlot> slots;

    //METADATA
    private final short slotArrayOffsetStart;
    private final PageHolesMap pageHolesMap;

    private PageSlotArray(List<PageSlot> slots, short slotArrayOffsetStart) {
        this.slots = slots;
        this.slotArrayOffsetStart = slotArrayOffsetStart;

        List<PageSlot> updatedSlotsList = addStartAndEndHolesToSlotsArray(slots, slotArrayOffsetStart);
        this.pageHolesMap = new PageHolesMap(updatedSlotsList, slotArrayOffsetStart);
    }

    public PageSlotArray(short slotArrayOffsetStart) {
        this(new ArrayList<>(), slotArrayOffsetStart);
    }

    public byte[] serialize() {
        ByteBuffer byteBuffer = ByteBuffer.allocate(this.getSerializedLength());

        for(PageSlot slot: this.slots) {
            byteBuffer.put(slot.serialize());
        }

        return byteBuffer.array();
    }

    public static PageSlotArray deserialize(byte[] bytes, int numSlots, short slotsArrayOffsetStart) {
        ByteBuffer byteBuffer = ByteBuffer.wrap(bytes);
        List<PageSlot> slotArray = new ArrayList<>(numSlots);

        for (int i = 0; i < numSlots; i++) {
            byte[] slotEntryBytes = ByteUtil.readNBytes(byteBuffer, PageSlot.getSerializedLength());
            PageSlot slot = PageSlot.deserialize(slotEntryBytes, (short) i);
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
        return numSlots * PageSlot.getSerializedLength();
    }

    private static List<PageSlot> addStartAndEndHolesToSlotsArray(List<PageSlot> slotsList, short slotArrayOffsetStart) {
        boolean hasTupleStartOffset = slotsList.stream().anyMatch(slot -> slot.pageOffset == getTupleOffsetStart(slotArrayOffsetStart, slotsList.size()));
        boolean hasPageEndOffset = slotsList.stream().anyMatch(slot -> slot.pageOffset + slot.tupleLength == Constants.PAGE_SIZE);

        List<PageSlot> updatedSlotsList = new ArrayList<>(slotsList);

        if(!hasTupleStartOffset) {
            short tupleOffsetStart = getTupleOffsetStart(slotArrayOffsetStart, updatedSlotsList.size());
            short tupleStartSlotIndex = (short) updatedSlotsList.size();
            PageSlot tupleStartSlot = new PageSlot(tupleStartSlotIndex, tupleOffsetStart, (short) 0);
            updatedSlotsList.add(tupleStartSlot);
        }

        if(!hasPageEndOffset) {
            short pageEndOffset = (short) (Constants.PAGE_SIZE);
            short pageEndSlotIndex = (short) updatedSlotsList.size();
            PageSlot pageEndSlot = new PageSlot(pageEndSlotIndex, pageEndOffset, (short) 0);
            updatedSlotsList.add(pageEndSlot);
        }

        return updatedSlotsList;
    }

    private void appendToSlotArray(PageSlot slotEntry) {
        this.slots.add(slotEntry);
        this.pageHolesMap.shiftTupleStartOffsetOnePositionRight();
    }

    public Optional<Short> getHole(short desiredLength) {
        return this.pageHolesMap.getHole(desiredLength);
    }

    public void returnHole(short offset, short length) {
        this.pageHolesMap.addHole(length, offset);
    }

    public Iterator<PageSlot> getIterator() {
        return new OccupiedPageSlotsIterator(this.slots);
    }

    public Optional<Integer> getEmptySlotIndex() {
        for(int i = 0; i<this.slots.size(); i++) {
            if(this.slots.get(i).isEmpty()) {
                return Optional.of(i);
            }
        }

        return Optional.empty();
    }

    public int insertSlot(short pageOffset, short tupleLength) {
        Optional<Integer> optionalEmptySlotIndex = this.getEmptySlotIndex();

        if(optionalEmptySlotIndex.isPresent()) {
            int emptySlotIndex = optionalEmptySlotIndex.get();
            PageSlot slot = this.slots.get(emptySlotIndex);
            slot.setValue(pageOffset, tupleLength);
            return emptySlotIndex;
        }
        else {
            short slotIndex = (short) this.slots.size();
            PageSlot slot = new PageSlot(slotIndex, pageOffset, tupleLength);
            this.appendToSlotArray(slot);
            return this.slots.size() - 1;
        }
    }

    public void emptySlot(int index) {
        PageSlot slotToRemove = this.slots.get(index);

        if(!slotToRemove.isEmpty()) {
            slotToRemove.setToEmpty();

            short holeStartOffset = slotToRemove.pageOffset;
            short holeLength = slotToRemove.tupleLength;

            this.pageHolesMap.addHole(holeLength, holeStartOffset);
        }
    }

    public PageSlot getSlot(int slotIndex) {
        if (slotIndex > this.slots.size() - 1) {
            throw new IndexOutOfBoundsException();
        }

        return this.slots.get(slotIndex);
    }
}
