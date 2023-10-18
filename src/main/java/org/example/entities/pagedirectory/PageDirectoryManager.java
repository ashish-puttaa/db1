package org.example.entities.pagedirectory;

import org.example.Constants;

import java.io.IOException;
import java.util.Optional;

//TODO: Keeps track of all page ids and what database file they are in and their offset/page_numbers
// Must be persisted
public class PageDirectoryManager {
    private static final class InstanceHolder { public static final PageDirectoryManager instance = new PageDirectoryManager(); }

    private final PageDirectoryPageBuffer buffer;
    private final PageDirectory pageDirectory;


    private PageDirectoryManager() {
        this.pageDirectory = new PageDirectory(Constants.PAGE_DIRECTORY_FILE_PATH, Constants.PAGE_SIZE);

        int bufferCapacity = Constants.PAGE_DIRECTORY_BUFFER_POOL_SIZE / Constants.PAGE_SIZE;
        this.buffer = new PageDirectoryPageBuffer(bufferCapacity, this, this.pageDirectory::readNthPage);
    }

    public static PageDirectoryManager getInstance() {
        return InstanceHolder.instance;
    }

    private boolean canAddRecordToPage(PageDirectoryPage page, PageDirectoryRecord record) {
        int pageLength = page.getSerializedLength();
        int recordLength = PageDirectoryRecord.getSerializedLength();

        return pageLength + recordLength < Constants.PAGE_SIZE;
    }

    public void addRecord(int pageId, PageDirectoryRecord record) {
        for(int i=0; i<this.pageDirectory.getPageCount(); i++) {
            Optional<PageDirectoryPage> optionalPage = this.buffer.getPage(i);

            if(optionalPage.isPresent() && this.canAddRecordToPage(optionalPage.get(), record)) {
                optionalPage.get().addMapping(pageId, record);
                return;
            }
        }

        try {
            PageDirectoryPage newPage = this.pageDirectory.addNewPage();
            newPage.addMapping(pageId, record);

            int pageIndex = this.pageDirectory.getPageCount() - 1;
            this.buffer.insertPage(pageIndex, newPage);
        }
        catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

    public Optional<PageDirectoryRecord> getRecord(int pageId) {
        for(int i=0; i<this.pageDirectory.getPageCount(); i++) {
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
                this.pageDirectory.writeNthPage(pageNumber, page);
            }
        }
        catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

    public void stopScheduler() {
        this.buffer.stopScheduler();
    }
}
