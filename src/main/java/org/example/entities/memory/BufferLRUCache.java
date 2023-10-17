package org.example.entities.memory;

import java.util.LinkedHashMap;
import java.util.Map;
import java.util.Optional;
import java.util.concurrent.locks.ReentrantReadWriteLock;
import java.util.function.BiConsumer;

class BufferLRUCache<K, V> {
    public final LinkedHashMap<K, V> cache;
    private final int capacity;
    private final BiConsumer<K, V> evictionHandler;
    private final ReentrantReadWriteLock lock;

    public BufferLRUCache(int capacity, BiConsumer<K, V> evictionHandler) {
        this.cache = new LinkedHashMap<>(capacity, 1, true);
        this.capacity = capacity;
        this.evictionHandler = evictionHandler;
        this.lock = new ReentrantReadWriteLock(true);
    }

    public Optional<V> get(K key) {
        this.lock.readLock().lock();
        try {
            return Optional.ofNullable(this.cache.get(key));
        }
        finally {
            this.lock.readLock().unlock();
        }
    }

    public void put(K key, V value) {
        this.lock.writeLock().lock();

        try {
            if(this.cache.size() >= this.capacity) {
                this.evict();
            }
            this.cache.put(key, value);
        }
        finally {
            this.lock.writeLock().unlock();
        }
    }

    private void evict() {
        Map.Entry<K, V> eldest = this.cache.entrySet().iterator().next();
        this.cache.remove(eldest.getKey());
        this.evictionHandler.accept(eldest.getKey(), eldest.getValue());
    }
}
