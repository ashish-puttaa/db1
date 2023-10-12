package org.example.entities.directory;

import java.util.*;

public class PageHolesMap {
    SortedMap<Short, List<Short>> holes;

    public PageHolesMap(PageSlotArrayEntry[] slotArray) {
        this.holes = this.constructPageHolesMap(slotArray);
    }

    public Optional<Short> getHole(short desiredLength) {
        Iterator<Map.Entry<Short, List<Short>>> iterator = this.holes.entrySet().iterator();

        while(iterator.hasNext()) {
            Map.Entry<Short, List<Short>> entry = iterator.next();
            Short length = entry.getKey();

            if (length >= desiredLength) {
                List<Short> offsetList = entry.getValue();

                if(!offsetList.isEmpty()) {
                    short offset = offsetList.remove(0);

                    if(offsetList.isEmpty()) {
                        iterator.remove();
                    }

                    return Optional.of(offset);
                }
            }
        }

        return Optional.empty();
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
