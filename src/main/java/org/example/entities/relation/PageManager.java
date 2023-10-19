package org.example.entities.relation;

import org.example.Constants;

import java.util.Optional;

public class PageManager {
    private static final class InstanceHolder { public static final PageManager instance = new PageManager(); }
    public static PageManager getInstance() { return PageManager.InstanceHolder.instance; }

    private final PageBufferPool buffer;

    public PageManager() {
        int bufferCapacity = Constants.PAGE_BUFFER_POOL_SIZE / Constants.PAGE_SIZE;
        this.buffer = new PageBufferPool(bufferCapacity);
    }

    public Optional<Page> getPage(int pageIdentifier) {
        return this.buffer.get(pageIdentifier);
    }
}
