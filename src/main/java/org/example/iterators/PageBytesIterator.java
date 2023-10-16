package org.example.iterators;

import java.io.IOException;
import java.io.RandomAccessFile;
import java.nio.file.Path;
import java.util.Iterator;
import java.util.logging.Level;
import java.util.logging.Logger;

public class PageBytesIterator implements Iterator<byte[]> {
    private static final Logger LOGGER = Logger.getLogger(PageBytesIterator.class.getName());

    private final RandomAccessFile file;
    private final long fileLength;
    private final int pageSize;
    private long currentPageNumber = -1;

    public PageBytesIterator(Path filePath, int pageSize) throws IOException {
        this.file = new RandomAccessFile(filePath.toFile(), "r");
        this.pageSize = pageSize;
        this.fileLength = this.file.length();
    }

    @Override
    public boolean hasNext() {
        long nextPageNumber = this.currentPageNumber + 1;
        // long nextPageOffsetStart = nextPageNumber * this.pageSize;
        long nextPageOffsetEnd = (nextPageNumber + 1) * this.pageSize - 1;
        boolean hasNext = this.fileLength >= nextPageOffsetEnd;

        if(!hasNext) {
            try {
                this.file.close();
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
        this.file.seek(offset);
        this.file.readFully(buffer);
        return buffer;
    }
}
