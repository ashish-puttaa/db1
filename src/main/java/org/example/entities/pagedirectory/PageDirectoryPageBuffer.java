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
    }
}
