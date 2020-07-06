package com.ifcc.irpc.common;

import java.lang.ref.SoftReference;

/**
 * @author chenghaifeng
 * @date 2020-07-06
 * @description
 */
public class Holder<T> extends SoftReference<T> {
    public Holder(T value) {
        super(value);
    }
}
