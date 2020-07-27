package com.ifcc.irpc.spi.factory;

import com.ifcc.irpc.spi.ExtensionLoad;
import com.ifcc.irpc.spi.SpiContext;
import com.ifcc.irpc.spi.annotation.SPI;
import org.apache.commons.lang3.StringUtils;

import java.util.ArrayList;
import java.util.List;
import java.util.Set;

/**
 * @author chenghaifeng
 * @date 2020-06-29
 * @description
 */
public class SpiExtensionFactory implements ExtensionFactory {

    @Override
    public <T> T getExtension(Class<T> type, String name) {
        if (!type.isInterface() || !type.isAnnotationPresent(SPI.class)) {
            return null;
        }
        return ExtensionLoad.getExtensionLoad(type).getExtension(name);
    }

    @Override
    public <T> T getExtension(Class<T> type) {
        if (!type.isInterface() || !type.isAnnotationPresent(SPI.class)) {
            return null;
        }
        String name = ExtensionLoad.getExtensionLoad(ExtensionFactory.class).getDefaultExtension().getExtension(SpiContext.class).getCustomConfig().get(type.getName());
        if (StringUtils.isBlank(name)) {
            return ExtensionLoad.getExtensionLoad(type).getDefaultExtension();
        }
        return ExtensionLoad.getExtensionLoad(type).getExtension(name);
    }

    @Override
    public <T> List<T> getAllExtension(Class<T> type) {
        List<T> list = new ArrayList<>();
        if (!type.isInterface() || !type.isAnnotationPresent(SPI.class)) {
            return list;
        }
        ExtensionLoad<T> load = ExtensionLoad.getExtensionLoad(type);
        Set<String> supportedExtensions = load.getSupportedExtensions();
        for (String name : supportedExtensions) {
            list.add(load.getExtension(name));
        }
        return list;
    }

}
