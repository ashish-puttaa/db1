package org.example.entities.common;

import java.util.Map;
import java.util.Optional;
import java.util.Set;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.TimeUnit;

//TODO: Dirty pages are not written back to disk immediately. They will be buffered. (Will add that into the Page)
public abstract class BufferPool<K, V> {
    private final BufferLRUCache<K, V> buffer;
    private volatile ScheduledExecutorService executorService;

    public BufferPool(int capacity) {
        this.buffer = new BufferLRUCache<>(capacity, this::handleBufferEviction);
    }

    public Optional<V> get(K key) {
        Optional<V> value = this.buffer.get(key);
        if(value.isPresent()) {
            return value;
        }

        Optional<V> newValue = this.read(key);
        if(newValue.isPresent()) {
            this.buffer.put(key, newValue.get());
            return newValue;
        }

        return Optional.empty();
    }

    public void put(K key, V value) {
        this.buffer.put(key, value);
    }

    public void startScheduler() {
        this.executorService = Executors.newSingleThreadScheduledExecutor();

        Runnable runnable = () -> this.handleBufferSchedule(this.buffer.cache.entrySet());

        TimeUnit timeUnit = TimeUnit.MILLISECONDS;
        long intervalDelay = timeUnit.toMillis(100);
        this.executorService.scheduleWithFixedDelay(runnable, intervalDelay, intervalDelay, timeUnit);
    }

    public void stopScheduler() {
        try {
            if(this.executorService != null) {
                this.executorService.shutdown();
                this.executorService.awaitTermination(5, TimeUnit.MINUTES);
            }
        }
        catch (InterruptedException ignored) {}
    }

    protected abstract Optional<V> read(K key);
    protected abstract void handleBufferEviction(K key, V value);
    protected abstract void handleBufferSchedule(Set<Map.Entry<K, V>> entrySet);
}
