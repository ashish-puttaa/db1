package org.example.iterators;

import org.example.entities.relation.Page;
import org.example.entities.relation.PageSlot;
import org.example.entities.relation.PageTuple;

import java.util.Iterator;

public class PageTupleIterator implements Iterator<PageTuple> {
    private final Page page;
    private final Iterator<PageSlot> occupiedSlotsIterator;

    public PageTupleIterator(Page page, Iterator<PageSlot> occupiedSlotsIterator) {
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

        PageSlot slot = this.occupiedSlotsIterator.next();
        return this.page.readTuple(slot);
    }

    @Override
    public void remove() {
        throw new UnsupportedOperationException("remove operation is not supported");
    }
}
