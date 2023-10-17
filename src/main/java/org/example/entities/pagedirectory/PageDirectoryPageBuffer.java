package org.example.entities.pagedirectory;

import org.example.entities.memory.BufferPool;

import java.io.IOException;
import java.util.Optional;

public class PageDirectoryPageBuffer extends BufferPool<Integer, PageDirectoryPage> {
    public PageDirectoryPageBuffer(int capacity, PageDirectoryManager pageDirectoryManager) {
        super(
            capacity,
            pageDirectoryManager::handleBufferEviction,
            pageNumber -> {
                try {
                    return Optional.of(pageDirectoryManager.readNthPage(pageNumber));
                }
                catch (IOException ignored) {}
                return Optional.empty();
            }
        );

        ScheduleHandler<Integer, PageDirectoryPage> scheduleHandler = (entrySet) -> {
            entrySet.forEach(entry -> {
                PageDirectoryPage page = entry.getValue();
                pageDirectoryManager.handleBufferEviction(entry.getKey(), page);
                page.markAsClean();
            });
        };

        this.startScheduler(scheduleHandler);
    }
}
