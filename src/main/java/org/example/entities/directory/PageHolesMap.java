package org.example.entities.directory;

import java.util.*;

//TODO:
// - Add the unassigned tuple bytes as a big hole in the hole map
// - Put HoleMap and SlotArray inside PageTuple (contains the tuple byte[])
// - Handle case when the hole is at the end. While constructing the hole map add a page slot entry for the end of the page (offset = page end, length = 0).

public class PageHolesMap {
    private final SortedMap<Short, List<Short>> holes;
    private Short tupleOffsetStart;
    private Optional<Short> optionalTupleStartOffsetHoleLength = Optional.empty();

    public PageHolesMap(List<PageSlotArrayEntry> slotsList, short tupleOffsetStart) {
        this.holes = this.constructPageHolesMap(slotsList, tupleOffsetStart);
        this.tupleOffsetStart = tupleOffsetStart;
    }

    public Optional<Short> getHole(short desiredLength) {
        Iterator<Map.Entry<Short, List<Short>>> iterator = this.holes.entrySet().iterator();

        while(iterator.hasNext()) {
            Map.Entry<Short, List<Short>> entry = iterator.next();
            Short holeLength = entry.getKey();

            if (holeLength >= desiredLength) {
                List<Short> offsetList = entry.getValue();

                if(!offsetList.isEmpty()) {
                    short holeOffset = offsetList.remove(0);
                    short desiredOffset = (short) (holeOffset + holeLength - desiredLength);
                    short newHoleLength = (short) (holeLength - desiredLength);

                    if(offsetList.isEmpty()) {
                        iterator.remove();
                    }

                    if(desiredOffset == this.tupleOffsetStart) {
                        this.optionalTupleStartOffsetHoleLength = Optional.empty();
                    }

                    this.holes.computeIfAbsent(newHoleLength, k -> new ArrayList<>()).add(holeOffset);
                    return Optional.of(desiredOffset);
                }
            }
        }

        return Optional.empty();
    }

    public void addHole(short holeLength, short holeOffset) {
        this.holes.computeIfAbsent(holeLength, k -> new ArrayList<>()).add(holeOffset);
    }

    private SortedMap<Short, List<Short>> constructPageHolesMap(List<PageSlotArrayEntry> slotsList, short tupleOffsetStart) {
        SortedMap<Short, List<Short>> holesMap = new TreeMap<>();

        PageSlotArrayEntry[] nonEmptySlotsArray = slotsList.stream().filter(entry -> !entry.isEmpty()).toArray(PageSlotArrayEntry[]::new);
        PageSlotArrayEntry[] sortedSlotArray = this.sortSlotArrayByOffset(nonEmptySlotsArray);

        for(int i=1; i<sortedSlotArray.length; i++) {
            PageSlotArrayEntry previousEntry = sortedSlotArray[i-1];
            PageSlotArrayEntry currentEntry = sortedSlotArray[i];

            short previousEntryOffsetEnd = (short) (previousEntry.pageOffset + previousEntry.tupleLength - 1);
            boolean hasGap = currentEntry.pageOffset > previousEntryOffsetEnd + 1;

            if(hasGap) {
                short holeStartOffset = (short) (previousEntryOffsetEnd + 1);
                short holeLength = (short) (currentEntry.pageOffset - holeStartOffset);

                holesMap.computeIfAbsent(holeLength, k -> new ArrayList<>()).add(holeStartOffset);

                if(holeStartOffset == tupleOffsetStart) {
                    this.optionalTupleStartOffsetHoleLength = Optional.of(holeLength);
                }
            }
        }

        return holesMap;
    }

    private PageSlotArrayEntry[] sortSlotArrayByOffset(PageSlotArrayEntry[] slotArray) {
        return Arrays.stream(slotArray)
                .sorted(Comparator.comparingInt(entry -> entry.pageOffset))
                .toArray(PageSlotArrayEntry[]::new);
    }

    public void shiftTupleStartOffsetOnePositionRight() {
        int bytesNewlyOccupiedBySlotArray = PageSlotArrayEntry.getSerializedLength();

        if(this.optionalTupleStartOffsetHoleLength.isPresent()) {
            Short tupleStartOffsetHoleLength = this.optionalTupleStartOffsetHoleLength.get();

            if(this.holes.containsKey(tupleStartOffsetHoleLength)) {
                List<Short> holeOffsets = this.holes.get(tupleStartOffsetHoleLength);
                holeOffsets.remove(this.tupleOffsetStart);

                short newOffsetZeroHoleLength = (short) (tupleStartOffsetHoleLength - bytesNewlyOccupiedBySlotArray);
                this.addHole(newOffsetZeroHoleLength, this.tupleOffsetStart);

                this.tupleOffsetStart = (short) (this.tupleOffsetStart + bytesNewlyOccupiedBySlotArray);
                this.optionalTupleStartOffsetHoleLength = Optional.of(newOffsetZeroHoleLength);
            }
        }
    }
}
