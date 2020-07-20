package com.ifcc.irpc.common.config;

import com.ifcc.irpc.spi.annotation.Cell;

import java.io.InputStream;
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
            InputStream inputStream = null;
            try {
                inputStream = this.getClass().getClassLoader().getResourceAsStream(filePath);
                props.load(inputStream);
            } catch (Exception e) {
                e.printStackTrace();
            } finally {
                if (inputStream != null) {
                    try {
                        inputStream.close();
                    } catch (Exception e) {
                        e.printStackTrace();
                    }
                }
            }
            return props;
        });
    }
}
