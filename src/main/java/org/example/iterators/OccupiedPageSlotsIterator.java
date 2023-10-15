package org.example.iterators;

import org.example.entities.directory.PageSlotArrayEntry;

import java.util.Iterator;
import java.util.List;

public class OccupiedPageSlotsIterator implements Iterator<PageSlotArrayEntry> {
    private List<PageSlotArrayEntry> slots;
    private int currentIndex = -1;

    public OccupiedPageSlotsIterator(List<PageSlotArrayEntry> slots) {
        this.slots = slots;
    }

    @Override
    public boolean hasNext() {
        List<PageSlotArrayEntry> slots = this.slots;

        for (int nextIndex = this.currentIndex + 1; nextIndex < slots.size(); nextIndex++) {
            if (!slots.get(nextIndex).isEmpty()) {
                this.currentIndex = nextIndex;
                return true;
            }
        }

        return false;
    }

    @Override
    public PageSlotArrayEntry next() {
        if (!this.hasNext()) {
            throw new IndexOutOfBoundsException("No more entries with data");
        }

        PageSlotArrayEntry nextEntry = this.slots.get(this.currentIndex + 1);
        this.currentIndex++;
        return nextEntry;
    }

    @Override
    public void remove() {
        throw new UnsupportedOperationException("remove operation is not supported");
    }
}
