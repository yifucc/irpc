package com.ifcc.irpc.common.config;

import java.util.Properties;

/**
 * @author chenghaifeng
 * @date 2020-06-30
 * @description
 */
public class PropertiesProvider implements IConfigProvider<Properties> {

    private String filePath;

    public PropertiesProvider(String filePath) {
        this.filePath = filePath;
    }

    @Override
    public Properties provide() {
        Properties props = new Properties();
        try {
            props.load(ClassLoader.getSystemResourceAsStream(filePath));
        } catch (Exception e) {
            e.printStackTrace();
        }
        return props;
    }
}
