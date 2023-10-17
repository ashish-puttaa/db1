package org.example.entities.pagedirectory;

import org.example.entities.memory.BufferPool;

import java.io.IOException;
import java.util.Optional;

public class PageDirectoryPageBuffer extends BufferPool<Integer, PageDirectoryPage> {
    public PageDirectoryPageBuffer(int capacity, PageDirectory pageDirectory) {
        super(
            capacity,
            pageDirectory::handleBufferEviction,
            pageNumber -> {
                try {
                    return Optional.of(pageDirectory.readNthPage(pageNumber));
                }
                catch (IOException ignored) {}
                return Optional.empty();
            }
        );

        ScheduleHandler<Integer, PageDirectoryPage> scheduleHandler = (entrySet) -> {
            entrySet.forEach(entry -> {
                PageDirectoryPage page = entry.getValue();
                pageDirectory.handleBufferEviction(entry.getKey(), page);
                page.markAsClean();
            });
        };

        this.startScheduler(scheduleHandler);
    }
}
