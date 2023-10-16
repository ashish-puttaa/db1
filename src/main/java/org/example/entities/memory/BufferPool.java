package org.example.entities.memory;

import java.util.Optional;

//TODO: Dirty pages are not written back to disk immediately. They will be buffered. (Will add that into the Page)
public class BufferPool<K, V> {
    private final BufferLRUCache<K, V> buffer;
    private final PageSupplier<K, V> pageSupplier;

    public BufferPool(int capacity, BufferLRUCache.EvictionHandler<K, V> evictionHandler, PageSupplier<K, V> pageSupplier) {
        this.buffer = new BufferLRUCache<>(capacity, evictionHandler);
        this.pageSupplier = pageSupplier;
    }

    public Optional<V> getPage(K pageIdentifier) {
        Optional<V> page = this.buffer.get(pageIdentifier);
        if(page.isPresent()) {
            return page;
        }

        Optional<V> newPage = this.pageSupplier.get(pageIdentifier);
        if(newPage.isPresent()) {
            this.buffer.put(pageIdentifier, newPage.get());
            return newPage;
        }

        return Optional.empty();
    }

    public void insertPage(K pageIdentifier, V page) {
        this.buffer.put(pageIdentifier, page);
    }

    public interface PageSupplier<K, V> {
        Optional<V> get(K pageIdentifier);
    }
}
