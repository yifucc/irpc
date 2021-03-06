package com.ifcc.irpc.spi.factory;

import com.ifcc.irpc.spi.annotation.SPI;

import java.util.List;

/**
 * @author chenghaifeng
 * @date 2020-07-01
 * @description
 */
@SPI("adaptive")
public interface ExtensionFactory {
    <T> T getExtension(Class<T> type, String name);
    <T> T getExtension(Class<T> type);
    <T> List<T> getAllExtension(Class<T> type);
}
