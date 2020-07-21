package com.ifcc.irpc.cache.impl;

import com.ifcc.irpc.cache.Cache;

import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

/**
 * @author chenghaifeng
 * @date 2020-07-21
 * @description 缓存实现类
 */
public class IrpcCache<K, V> implements Cache<K, V> {
    private Map<K, V> cache = new ConcurrentHashMap<>();

    @Override
    public V get(K key) {
        return cache.get(key);
    }

    @Override
    public void put(K key, V value) {
        cache.put(key, value);
    }

    @Override
    public V remove(K key) {
        return cache.remove(key);
    }

    @Override
    public void clear() {
        cache.clear();
    }

    @Override
    public int size() {
        return cache.size();
    }
}
