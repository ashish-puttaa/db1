package org.example.iterators;

import org.example.entities.relation.PageSlot;

import java.util.Iterator;
import java.util.List;

public class OccupiedPageSlotsIterator implements Iterator<PageSlot> {
    private final List<PageSlot> slots;
    private int currentIndex = -1;

    public OccupiedPageSlotsIterator(List<PageSlot> slots) {
        this.slots = slots;
    }

    @Override
    public boolean hasNext() {
        List<PageSlot> slots = this.slots;

        for (int nextIndex = this.currentIndex + 1; nextIndex < slots.size(); nextIndex++) {
            if (!slots.get(nextIndex).isEmpty()) {
                this.currentIndex = nextIndex - 1;
                return true;
            }
        }

        return false;
    }

    @Override
    public PageSlot next() {
        if (!this.hasNext()) {
            throw new IndexOutOfBoundsException("No more entries with data");
        }

        PageSlot nextSlot = this.slots.get(this.currentIndex + 1);
        this.currentIndex++;
        return nextSlot;
    }

    @Override
    public void remove() {
        throw new UnsupportedOperationException("remove operation is not supported");
    }
}
