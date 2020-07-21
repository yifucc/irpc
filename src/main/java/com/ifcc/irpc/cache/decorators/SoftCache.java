package com.ifcc.irpc.cache.decorators;

import com.ifcc.irpc.cache.Cache;

import java.lang.ref.ReferenceQueue;
import java.lang.ref.SoftReference;

/**
 * @author chenghaifeng
 * @date 2020-07-21
 * @description 软引用缓存
 */
public class SoftCache<K, V> implements Cache<K, V> {
    private Cache<K, SoftReference<V>> cache;
    private ReferenceQueue<? super V> referenceQueue;

    public SoftCache(Cache cache) {
        this.cache = cache;
        referenceQueue = new ReferenceQueue<>();
    }

    @Override
    public V get(K key) {
        V value = null;
        SoftReference<V> softReference = cache.get(key);
        if (softReference != null) {
            value = softReference.get();
            if (value == null) {
                cache.remove(key);
            }
        }
        return value;
    }

    @Override
    public void put(K key, V value) {
        clearReferenceQueue();
        cache.put(key, new SoftEntity<K, V>(key, value, referenceQueue));
    }

    @Override
    public V remove(K key) {
        clearReferenceQueue();
        SoftReference<V> removeValue = cache.remove(key);
        return removeValue != null? removeValue.get() : null;
    }

    @Override
    public void clear() {
        clearReferenceQueue();
        cache.clear();
    }

    @Override
    public int size() {
        clearReferenceQueue();
        return cache.size();
    }

    @SuppressWarnings("unchecked")
    private void clearReferenceQueue() {
        SoftEntity<K, V> entity;
        while ((entity = (SoftEntity<K, V>) referenceQueue.poll()) != null) {
            cache.remove(entity.key);
        }
    }

    private static class SoftEntity<K, V> extends SoftReference<V> {
        private final K key;
        SoftEntity(K key, V value, ReferenceQueue<? super V> referenceQueue) {
            super(value, referenceQueue);
            this.key = key;
        }
    }
}
