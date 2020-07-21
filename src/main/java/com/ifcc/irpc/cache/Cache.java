package com.ifcc.irpc.cache;

/**
 * @author chenghaifeng
 * @date 2020-07-21
 * @description 缓存接口
 */
public interface Cache<K, V> {
    V get(K key);
    void put(K key, V value);
    V remove(K key);
    void clear();
    int size();
}
