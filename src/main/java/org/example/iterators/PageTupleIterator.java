package org.example.iterators;

import org.example.entities.directory.Page;
import org.example.entities.directory.PageSlotArrayEntry;
import org.example.entities.directory.PageTuple;

import java.util.Iterator;

public class PageTupleIterator implements Iterator<PageTuple> {
    private final Page page;
    private final Iterator<PageSlotArrayEntry> occupiedSlotsIterator;

    public PageTupleIterator(Page page, Iterator<PageSlotArrayEntry> occupiedSlotsIterator) {
        this.page = page;
        this.occupiedSlotsIterator = occupiedSlotsIterator;
    }

    @Override
    public boolean hasNext() {
        return this.occupiedSlotsIterator.hasNext();
    }

    @Override
    public PageTuple next() {
        if (!this.hasNext()) {
            throw new IndexOutOfBoundsException("No more entries with data");
        }

        PageSlotArrayEntry slotEntry = this.occupiedSlotsIterator.next();
        return this.page.readTuple(slotEntry);
    }

    @Override
    public void remove() {
        throw new UnsupportedOperationException("remove operation is not supported");
    }
}
