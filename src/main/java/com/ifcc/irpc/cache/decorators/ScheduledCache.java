package com.ifcc.irpc.cache.decorators;

import com.ifcc.irpc.cache.Cache;

import java.util.Queue;
import java.util.concurrent.ConcurrentLinkedQueue;
import java.util.concurrent.TimeUnit;

/**
 * @author chenghaifeng
 * @date 2020-07-21
 * @description 定时缓存
 */
public class ScheduledCache<K, V> implements Cache<K, V> {

    private Cache<K, V> cache;

    private Queue<ScheduledEntity<K>> timeRecords;

    private long clearInterval;

    public ScheduledCache(Cache cache) {
        this.cache = cache;
        this.clearInterval = TimeUnit.MINUTES.toMillis(5);
        this.timeRecords = new ConcurrentLinkedQueue<ScheduledEntity<K>>();
    }

    public ScheduledCache(Cache cache, long clearInterval) {
        this.cache = cache;
        this.clearInterval = clearInterval;
        this.timeRecords = new ConcurrentLinkedQueue<ScheduledEntity<K>>();
    }

    public void setClearInterval(long clearInterval) {
        this.clearInterval = clearInterval;
    }

    @Override
    public V get(K key) {
        clearExpiredKeyValue();
        return cache.get(key);
    }

    @Override
    public void put(K key, V value) {
        clearExpiredKeyValue();
        cache.put(key, value);
        timeRecords.offer(new ScheduledEntity<>(key, System.currentTimeMillis()));
    }

    @Override
    public V remove(K key) {
        clearExpiredKeyValue();
        return cache.remove(key);
    }

    @Override
    public void clear() {
        timeRecords.clear();
        cache.clear();
    }

    @Override
    public int size() {
        clearExpiredKeyValue();
        return cache.size();
    }

    private void clearExpiredKeyValue() {
        ScheduledEntity<K> entity;
        long currentTime = System.currentTimeMillis();
        while ((entity = timeRecords.peek()) != null) {
            if (currentTime - entity.timestamp > clearInterval) {
                timeRecords.poll();
                cache.remove(entity.key);
            } else {
                break;
            }
        }
    }

    private static class ScheduledEntity<K> {
        K key;
        long timestamp;
        ScheduledEntity(K key, long timestamp) {
            this.key = key;
            this.timestamp = timestamp;
        }
    }
}
