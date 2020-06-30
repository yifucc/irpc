package com.ifcc.irpc.common.config;

/**
 * @author chenghaifeng
 * @date 2020-06-30
 * @description 配置加载器接口
 */
public interface IConfigLoader<T> {

    T load();
}
