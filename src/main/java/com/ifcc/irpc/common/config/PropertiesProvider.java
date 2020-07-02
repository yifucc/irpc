package com.ifcc.irpc.common.config;

import com.ifcc.irpc.spi.annotation.Cell;

import java.util.Map;
import java.util.Properties;
import java.util.concurrent.ConcurrentHashMap;

/**
 * @author chenghaifeng
 * @date 2020-06-30
 * @description
 */
@Cell("properties")
public class PropertiesProvider implements IConfigProvider<Properties> {

    private final Map<String, Properties> propertiesMap = new ConcurrentHashMap<>();

    public PropertiesProvider() {}

    @Override
    public Properties provide(String filePath) {
        return propertiesMap.computeIfAbsent(filePath, path -> {
            Properties props = new Properties();
            try {
                props.load(ClassLoader.getSystemResourceAsStream(filePath));
            } catch (Exception e) {
                e.printStackTrace();
            }
            return props;
        });
    }
}
