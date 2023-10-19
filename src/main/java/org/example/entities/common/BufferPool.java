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

    public Optional<V> getPage(K pageIdentifier) {
        Optional<V> page = this.buffer.get(pageIdentifier);
        if(page.isPresent()) {
            return page;
        }

        Optional<V> newPage = this.readPage(pageIdentifier);
        if(newPage.isPresent()) {
            this.buffer.put(pageIdentifier, newPage.get());
            return newPage;
        }

        return Optional.empty();
    }

    public void insertPage(K pageIdentifier, V page) {
        this.buffer.put(pageIdentifier, page);
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

    protected abstract Optional<V> readPage(K pageIdentifier);
    protected abstract void handleBufferEviction(K key, V value);
    protected abstract void handleBufferSchedule(Set<Map.Entry<K, V>> entrySet);
}
