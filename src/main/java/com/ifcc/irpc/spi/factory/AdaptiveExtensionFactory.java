package com.ifcc.irpc.spi.factory;

import com.google.common.collect.Lists;
import com.ifcc.irpc.spi.ExtensionLoad;

import java.util.ArrayList;
import java.util.List;

/**
 * @author chenghaifeng
 * @date 2020-07-01
 * @description
 */
public class AdaptiveExtensionFactory implements ExtensionFactory {

    private final List<ExtensionFactory> factories;

    public AdaptiveExtensionFactory() {
        ExtensionLoad<ExtensionFactory> load = ExtensionLoad.getExtensionLoad(ExtensionFactory.class);
        factories = Lists.newArrayList();
        for (String name : load.getSupportedExtensions()) {
            if ("adaptive".equals(name)) {
                continue;
            }
            factories.add(load.getExtension(name));
        }
    }

    @Override
    public <T> T getExtension(Class<T> type, String name) {
        for (ExtensionFactory factory : factories) {
            T extension = factory.getExtension(type, name);
            if (extension != null) {
                return extension;
            }
        }
        return null;
    }

    @Override
    public <T> T getExtension(Class<T> type) {
        for (ExtensionFactory factory : factories) {
            T extension = factory.getExtension(type);
            if (extension != null) {
                return extension;
            }
        }
        return null;
    }

    @SuppressWarnings("unchecked")
    @Override
    public <T> List<T> getAllExtension(Class<T> type) {
        List result = new ArrayList();
        for (ExtensionFactory factory : factories) {
            result.addAll(factory.getAllExtension(type));
        }
        return result;
    }
}
