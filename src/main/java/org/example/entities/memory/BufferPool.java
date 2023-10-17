package org.example.entities.memory;

import java.util.Map;
import java.util.Optional;
import java.util.Set;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.TimeUnit;
import java.util.function.BiConsumer;

//TODO: Dirty pages are not written back to disk immediately. They will be buffered. (Will add that into the Page)
public class BufferPool<K, V> {
    private final BufferLRUCache<K, V> buffer;

    private final PageSupplier<K, V> pageSupplier;
    private volatile ScheduledExecutorService executorService;
    private ScheduleHandler<K, V> scheduleHandler;

    public BufferPool(int capacity, BiConsumer<K, V> evictionHandler, PageSupplier<K, V> pageSupplier) {
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

    public void startScheduler(ScheduleHandler<K, V> scheduleHandler) {
        this.scheduleHandler = scheduleHandler;
        this.executorService = Executors.newSingleThreadScheduledExecutor();

        Runnable runnable = () -> this.scheduleHandler.processBuffer(this.buffer.cache.entrySet());

        TimeUnit timeUnit = TimeUnit.MILLISECONDS;
        long intervalDelay = timeUnit.toMillis(100);
        this.executorService.scheduleWithFixedDelay(runnable, intervalDelay, intervalDelay, timeUnit);
    }

    public void stopScheduler() {
        this.executorService.shutdown();

        // TODO: Remove this later
        while(!this.executorService.isTerminated()) {
            // wait
        }
    }

    public interface PageSupplier<K, V> {
        Optional<V> get(K pageIdentifier);
    }

    public interface ScheduleHandler<K, V> {
        void processBuffer(Set<Map.Entry<K, V>> entrySet);
    }
}
