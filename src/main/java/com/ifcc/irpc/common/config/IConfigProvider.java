package com.ifcc.irpc.common.config;

/**
 * @author chenghaifeng
 * @date 2020-06-30
 * @description 配置加载方式提供者接口
 */
public interface IConfigProvider<T> {

    T provide(String filePath);
}
