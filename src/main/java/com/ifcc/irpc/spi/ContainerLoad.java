package com.ifcc.irpc.spi;

import com.google.common.collect.Maps;
import com.ifcc.irpc.spi.annotation.Cell;
import com.ifcc.irpc.spi.factory.ExtensionFactory;
import com.ifcc.irpc.utils.ClassUtil;
import org.apache.commons.lang3.StringUtils;

import java.util.List;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

/**
 * @author chenghaifeng
 * @date 2020-07-02
 * @description
 */
public class ContainerLoad<T> extends AbstractLoad<T>{
    private final static Map<Class<?>, ContainerLoad<?>> CONTAINER_LOAD_MAP = new ConcurrentHashMap<>();

    private Class<T> type;

    private ContainerLoad(Class<T> type) {
        super(ExtensionLoad.getExtensionLoad(ExtensionFactory.class).getDefaultExtension());
        this.type = type;
    }

    public static <T> ContainerLoad<T> getContainerLoad(Class<T> type) {
        if(type == null) {
            throw new IllegalArgumentException("Class type cannot be null.");
        }
        return (ContainerLoad<T>) CONTAINER_LOAD_MAP.computeIfAbsent(type, ContainerLoad::new);
    }

    @Override
    protected Map<String, Class<?>> loadExtensionClass() {
        Map<String, Class<?>> classMap = Maps.newHashMap();
        if (type.isInterface()) {
            List<Class<?>> classes = ClassUtil.getInterfaceImpls(type);
            if(classes.isEmpty()) {
                throw new IllegalArgumentException("The interface class has no implement class: " + type.getName());
            }
            if (classes.size() > 1) {
                for(Class<?> clazz : classes) {
                    Cell cell = clazz.getAnnotation(Cell.class);
                    if(cell == null || StringUtils.isBlank(cell.value())) {
                        throw new IllegalArgumentException("Multi implement class has no annotation cell or cell'value is empty: " + clazz.getName());
                    }
                    classMap.put(cell.value(), clazz);
                }
            } else {
                Class<?> clazz = classes.get(0);
                Cell cell = clazz.getAnnotation(Cell.class);
                String name = clazz.getName();
                if(cell != null && StringUtils.isNotBlank(cell.value())) {
                    name = cell.value();
                }
                classMap.put(name, clazz);
            }
        } else {
            String name = type.getName();
            Cell cell = type.getAnnotation(Cell.class);
            if(cell != null && StringUtils.isNotBlank(cell.value())) {
                name = cell.value();
            }
            classMap.put(name, type);
        }
        return classMap;
    }

}
