package com.ifcc.irpc.common;

/**
 * @author chenghaifeng
 * @date 2020-07-27
 * @description
 */
public interface Invoker {
    Result invoke(Invocation invocation);
}
