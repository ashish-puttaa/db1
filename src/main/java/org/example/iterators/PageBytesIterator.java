package org.example.iterators;

import java.io.IOException;
import java.io.RandomAccessFile;
import java.nio.file.Path;
import java.util.Iterator;
import java.util.logging.Level;
import java.util.logging.Logger;

class PageBytesIterator implements Iterator<byte[]> {
    private static final Logger LOGGER = Logger.getLogger(PageBytesIterator.class.getName());

    private final RandomAccessFile relationFile;
    private final long relationFileLength;
    private final int pageSize;
    private long currentPageNumber = -1;

    public PageBytesIterator(Path relationPath, int pageSize) throws IOException {
        this.relationFile = new RandomAccessFile(relationPath.toFile(), "r");
        this.pageSize = pageSize;
        this.relationFileLength = this.relationFile.length();
    }

    @Override
    public boolean hasNext() {
        long nextPageNumber = this.currentPageNumber + 1;
        // long nextPageOffsetStart = nextPageNumber * this.pageSize;
        long nextPageOffsetEnd = (nextPageNumber + 1) * this.pageSize - 1;
        boolean hasNext = this.relationFileLength >= nextPageOffsetEnd;

        if(!hasNext) {
            try {
                this.relationFile.close();
            }
            catch (IOException exception) {
                LOGGER.log(Level.SEVERE, "PageIterator :: hasNext :: ", exception);
            }
        }

        return hasNext;
    }

    @Override
    public byte[] next() {
        long nextPageNumber = this.currentPageNumber + 1;
        byte[] nextPage = null;

        try {
            nextPage = this.readPage(nextPageNumber);
            this.currentPageNumber++;
        }
        catch (IOException exception) {
            LOGGER.log(Level.SEVERE, "PageIterator :: next :: ", exception);
        }

        return nextPage;
    }

    private byte[] readPage(long zeroIndexedPageNumber) throws IOException {
        long offset = zeroIndexedPageNumber * this.pageSize;
        byte[] buffer = new byte[this.pageSize];
        this.relationFile.seek(offset);
        this.relationFile.readFully(buffer);
        return buffer;
    }
}
