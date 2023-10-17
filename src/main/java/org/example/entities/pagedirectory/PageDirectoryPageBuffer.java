package org.example.entities.pagedirectory;

import org.example.entities.memory.BufferPool;

import java.io.IOException;
import java.util.Map;
import java.util.Optional;
import java.util.Set;

public class PageDirectoryPageBuffer extends BufferPool<Integer, PageDirectoryPage> {
    private final PageDirectoryManager pageDirectoryManager;

    public PageDirectoryPageBuffer(int capacity, PageDirectoryManager pageDirectoryManager) {
        super(capacity);
        this.pageDirectoryManager = pageDirectoryManager;
        this.startScheduler();
    }

    @Override
    protected Optional<PageDirectoryPage> readPage(Integer pageIdentifier) {
        try {
            return Optional.of(this.pageDirectoryManager.readNthPage(pageIdentifier));
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
}
