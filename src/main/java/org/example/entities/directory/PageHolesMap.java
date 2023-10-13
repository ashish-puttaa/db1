package org.example.entities.directory;

import java.util.*;

//TODO:
// - Add the unassigned tuple bytes as a big hole in the hole map
// - Put HoleMap and SlotArray inside PageTuple (contains the tuple byte[])
// - Handle case when the hole is at the end. While constructing the hole map add a page slot entry for the end of the page (offset = page end, length = 0).

public class PageHolesMap {
    private final SortedMap<Short, List<Short>> holes;

    public PageHolesMap(PageSlotArrayEntry[] slotArray) {
        this.holes = this.constructPageHolesMap(slotArray);
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

    private SortedMap<Short, List<Short>> constructPageHolesMap(PageSlotArrayEntry[] slotArray) {
        SortedMap<Short, List<Short>> holesMap = new TreeMap<>();

        PageSlotArrayEntry[] nonEmptySlotsArray = Arrays.stream(slotArray).filter(entry -> !entry.isEmpty()).toArray(PageSlotArrayEntry[]::new);
        PageSlotArrayEntry[] sortedSlotArray = this.sortSlotArrayByOffset(nonEmptySlotsArray);

        for(int i=1; i<sortedSlotArray.length; i++) {
            PageSlotArrayEntry previousEntry = sortedSlotArray[i-1];
            PageSlotArrayEntry currentEntry = sortedSlotArray[i];

            short previousEntryOffsetEnd = (short) (previousEntry.pageOffset + previousEntry.tupleLength - 1);
            boolean hasGap = currentEntry.pageOffset > previousEntryOffsetEnd;

            if(hasGap) {
                short holeStartOffset = (short) (previousEntryOffsetEnd + 1);
                short holeLength = (short) (currentEntry.pageOffset - holeStartOffset);

                holesMap.computeIfAbsent(holeLength, k -> new ArrayList<>()).add(holeStartOffset);
            }
        }

        return holesMap;
    }

    private PageSlotArrayEntry[] sortSlotArrayByOffset(PageSlotArrayEntry[] slotArray) {
        return Arrays.stream(slotArray)
                .sorted(Comparator.comparingInt(entry -> entry.pageOffset))
                .toArray(PageSlotArrayEntry[]::new);
    }
}
