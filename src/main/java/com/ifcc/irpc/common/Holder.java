package com.ifcc.irpc.common;

import java.lang.ref.SoftReference;

/**
 * @author chenghaifeng
 * @date 2020-07-06
 * @description
 */
public class Holder<T> {
    private SoftReference<T> ref;

    public Holder() {}

    public void set(T value) {
        this.ref = new SoftReference<>(value);
    }

    public T get() {
        return this.ref != null? this.ref.get() : null;
    }
}
