package org.example.iterators;

import org.example.entities.directory.Page;

import java.io.IOException;
import java.nio.file.Path;
import java.util.Iterator;

public class PageIterator implements Iterator<Page> {
    PageBytesIterator pageBytesIterator;

    public PageIterator(Path relationPath, int pageSize) throws IOException {
        this.pageBytesIterator = new PageBytesIterator(relationPath, pageSize);
    }

    @Override
    public boolean hasNext() {
        return pageBytesIterator.hasNext();
    }

    @Override
    public Page next() {
        byte[] nextPageBytes = this.pageBytesIterator.next();
        return Page.deserialize(nextPageBytes);
    }
}
