package com.ifcc.irpc.spi.factory;

import com.ifcc.irpc.spi.ContainerLoad;
import com.ifcc.irpc.spi.annotation.SPI;

/**
 * @author chenghaifeng
 * @date 2020-07-01
 * @description
 */
public class ContainerExtensionFactory implements ExtensionFactory {

    public ContainerExtensionFactory() {}

    @SuppressWarnings("unchecked")
    @Override
    public <T> T getExtension(Class<T> type, String name) {
        if (type.isInterface() && type.isAnnotationPresent(SPI.class)) {
            return null;
        }
        Object o = ContainerLoad.getContainerLoad(type).getExtension(name);
        return (T) (o != null? o : ContainerLoad.getContainerLoad(type).getExtension(type.getName()));
    }

    @Override
    public <T> T getExtension(Class<T> type) {
        if (type.isInterface() && type.isAnnotationPresent(SPI.class)) {
            return null;
        }
        return ContainerLoad.getContainerLoad(type).getExtension(type.getName());
    }

}
