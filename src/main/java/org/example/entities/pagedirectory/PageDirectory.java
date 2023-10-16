package org.example.entities.pagedirectory;

import org.example.Constants;
import org.example.util.ByteUtil;

import java.io.IOException;
import java.io.RandomAccessFile;
import java.nio.file.Path;
import java.util.Optional;

//TODO: Keeps track of all page ids and what database file they are in and their offset/page_numbers
// Must be persisted
public class PageDirectory {
    private static final class InstanceHolder { public static final PageDirectory instance = new PageDirectory(); }

    private final Path filePath = Constants.PAGE_DIRECTORY_FILE_PATH;
    private final int pageSize = Constants.PAGE_SIZE;
    private final PageDirectoryPageBuffer buffer;

    private final PageDirectoryHeader header;

    private PageDirectory() {
        int bufferCapacity = Constants.PAGE_DIRECTORY_BUFFER_POOL_SIZE / this.pageSize;
        this.buffer = new PageDirectoryPageBuffer(bufferCapacity, this);

        try {
            byte[] headerBytes = ByteUtil.readNBytes(this.filePath, PageDirectoryHeader.getSerializedLength(), 0);
            this.header = PageDirectoryHeader.deserialize(headerBytes);
        }
        catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

    public static PageDirectory getInstance() {
        return InstanceHolder.instance;
    }

    public void addMapping(int pageId, PageDirectoryRecord pageDirectoryRecord) {
        for(int i=0; i<this.header.getPageCount(); i++) {
            Optional<PageDirectoryPage> optionalPage = this.buffer.getPage(i);

            if(optionalPage.isPresent()) {
                PageDirectoryPage page = optionalPage.get();
                int pageLength = page.getSerializedLength();

                if(pageLength + PageDirectoryRecord.getSerializedLength() < this.pageSize) {
                    page.addMapping(pageId, pageDirectoryRecord);
                    return;
                }
            }
        }

        this.header.incrementPageCount();

        PageDirectoryPage newPage = new PageDirectoryPage();
        newPage.addMapping(pageId, pageDirectoryRecord);

        int pageIndex = this.header.getPageCount() - 1;
        this.buffer.insertPage(pageIndex, newPage);
    }

    public Optional<PageDirectoryRecord> getMapping(int pageId) throws IOException {
        for(int i=0; i<this.header.getPageCount(); i++) {
            Optional<PageDirectoryPage> optionalPage = this.buffer.getPage(i);

            if(optionalPage.isPresent()) {
                PageDirectoryPage page = optionalPage.get();
                Optional<PageDirectoryRecord> optionalRecord = page.getMapping(pageId);

                if(optionalRecord.isPresent()) {
                    return optionalRecord;
                }
            }
        }

        return Optional.empty();
    }

    public void handleBufferEviction(int pageNumber, PageDirectoryPage page)
    {
        try {
            if(page.isDirty()) {
                this.writeNthPage(pageNumber, page);
            }
        }
        catch (IOException exception) {
            exception.printStackTrace();
        }
    }

    public PageDirectoryPage readNthPage(int n) throws IOException {
        int pageOffsetInRelation = this.pageSize * n;
        byte[] pageBytes = ByteUtil.readNBytes(this.filePath, this.pageSize, pageOffsetInRelation);
        return PageDirectoryPage.deserialize(pageBytes);
    }

    public void writeNthPage(int n, PageDirectoryPage page) throws IOException {
        try (RandomAccessFile randomAccessFile = new RandomAccessFile(this.filePath.toFile(), "rw")) {
            byte[] pageBytes = page.serialize();

            int pageOffsetInRelation = this.pageSize * n;
            randomAccessFile.seek(pageOffsetInRelation);
            randomAccessFile.write(pageBytes);
        }
    }
}
