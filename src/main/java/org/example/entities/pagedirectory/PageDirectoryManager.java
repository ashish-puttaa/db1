package org.example.entities.pagedirectory;

import org.example.Constants;
import org.example.entities.memory.BufferPool;

import java.io.IOException;
import java.util.Map;
import java.util.Optional;
import java.util.Set;

//TODO: Keeps track of all page ids and what database file they are in and their offset/page_numbers
// Must be persisted
public class PageDirectoryManager {
    private static final class InstanceHolder { public static final PageDirectoryManager instance = new PageDirectoryManager(); }

    private final BufferPool<Integer, PageDirectoryPage> buffer;
    private final PageDirectory pageDirectory;


    private PageDirectoryManager() {
        this.pageDirectory = new PageDirectory(Constants.PAGE_DIRECTORY_FILE_PATH, Constants.PAGE_SIZE);

        int bufferCapacity = Constants.PAGE_DIRECTORY_BUFFER_POOL_SIZE / Constants.PAGE_SIZE;
        this.buffer = this.createBufferPool(bufferCapacity);
        this.buffer.startScheduler();
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

    private BufferPool<Integer, PageDirectoryPage> createBufferPool(int capacity) {
        return new BufferPool<>(capacity) {
            @Override
            protected Optional<PageDirectoryPage> readPage(Integer pageIdentifier) {
                try {
                    return Optional.of(PageDirectoryManager.this.pageDirectory.readNthPage(pageIdentifier));
                }
                catch (IOException ignored) {}
                return Optional.empty();
            }

            @Override
            protected void handleBufferEviction(Integer key, PageDirectoryPage value) {
                PageDirectoryManager.this.handleBufferEviction(key, value);
            }

            @Override
            protected void handleBufferSchedule(Set<Map.Entry<Integer, PageDirectoryPage>> entrySet) {
                entrySet.forEach(entry -> {
                    int pageNumber = entry.getKey();
                    PageDirectoryPage page = entry.getValue();

                    PageDirectoryManager.this.handleBufferEviction(pageNumber, page);
                    page.markAsClean();
                });
            }
        };
    }
}
