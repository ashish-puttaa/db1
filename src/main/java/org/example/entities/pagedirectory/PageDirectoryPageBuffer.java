package org.example.entities.pagedirectory;

import org.example.entities.memory.BufferPool;

import java.io.IOException;
import java.util.Map;
import java.util.Optional;
import java.util.Set;

public class PageDirectoryPageBuffer extends BufferPool<Integer, PageDirectoryPage> {
    private final PageDirectoryManager pageDirectoryManager;
    private final ReadPageHandler readPageHandler;

    public PageDirectoryPageBuffer(int capacity, PageDirectoryManager pageDirectoryManager, ReadPageHandler readPageHandler) {
        super(capacity);
        this.pageDirectoryManager = pageDirectoryManager;
        this.readPageHandler = readPageHandler;
        this.startScheduler();
    }

    @Override
    protected Optional<PageDirectoryPage> readPage(Integer pageIdentifier) {
        try {
            return Optional.of(this.readPageHandler.read(pageIdentifier));
        }
        catch (IOException ignored) {}
        return Optional.empty();
    }

    @Override
    protected void handleBufferEviction(Integer key, PageDirectoryPage value) {
        this.pageDirectoryManager.handleBufferEviction(key, value);
    }

    @Override
    protected void handleBufferSchedule(Set<Map.Entry<Integer, PageDirectoryPage>> entrySet) {
        entrySet.forEach(entry -> {
            int pageNumber = entry.getKey();
            PageDirectoryPage page = entry.getValue();

            this.pageDirectoryManager.handleBufferEviction(pageNumber, page);
            page.markAsClean();
        });
    }

    public interface ReadPageHandler {
        PageDirectoryPage read(int pageNumber) throws IOException;
    }
}
