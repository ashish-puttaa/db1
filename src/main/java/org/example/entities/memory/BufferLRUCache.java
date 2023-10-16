package org.example.entities.memory;

import java.util.HashMap;
import java.util.LinkedList;
import java.util.Optional;

// TODO: Make this thread safe
public class BufferLRUCache<K, V> {
    private final LinkedList<K> order;
    private final HashMap<K, V> cache;
    private final int capacity;

    public BufferLRUCache(int capacity) {
        this.order = new LinkedList<>();
        this.cache = new HashMap<>();
        this.capacity = capacity;
    }

    public Optional<V> get(K key) {
        if(this.cache.containsKey(key)) {
            this.order.remove(key);
            this.order.addFirst(key);
            return Optional.of(this.cache.get(key));
        }

        return Optional.empty();
    }

    public void put(K key, V value) {
        if(this.cache.containsKey(key)) {
            this.order.remove(key);
        }
        else if(this.order.size() == this.capacity) {
            K removedKey = this.order.removeLast();
            this.cache.remove(removedKey);
        }

        this.order.addFirst(key);
        this.cache.put(key, value);
    }
}
