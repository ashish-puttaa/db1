package org.example.entities.pagedirectory;

import org.example.iterators.PageBytesIterator;

import java.io.IOException;
import java.nio.file.Path;
import java.util.Iterator;

public class PageDirectoryPageIterator implements Iterator<PageDirectoryPage> {
    PageBytesIterator pageBytesIterator;

    public PageDirectoryPageIterator(Path directoryFilePath, int pageSize) throws IOException {
        this.pageBytesIterator = new PageBytesIterator(directoryFilePath, pageSize);
    }

    @Override
    public boolean hasNext() {
        return this.pageBytesIterator.hasNext();
    }

    @Override
    public PageDirectoryPage next() {
        byte[] nextPageBytes = this.pageBytesIterator.next();
        return PageDirectoryPage.deserialize(nextPageBytes);
    }
}
