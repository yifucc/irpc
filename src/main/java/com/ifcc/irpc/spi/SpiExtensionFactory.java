package com.ifcc.irpc.spi;

import com.ifcc.irpc.spi.annotation.SPI;
import org.apache.commons.lang3.StringUtils;

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

}
