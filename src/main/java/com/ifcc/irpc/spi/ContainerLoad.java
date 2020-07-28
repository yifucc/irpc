package com.ifcc.irpc.spi;

import com.google.common.collect.Lists;
import com.google.common.collect.Maps;
import com.ifcc.irpc.annotation.IrpcFactory;
import com.ifcc.irpc.common.ClassQueryBuilder;
import com.ifcc.irpc.spi.annotation.Cell;
import com.ifcc.irpc.spi.factory.ExtensionFactory;
import com.ifcc.irpc.utils.AnnotationUtil;
import org.apache.commons.lang3.StringUtils;

import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.concurrent.ConcurrentHashMap;

/**
 * @author chenghaifeng
 * @date 2020-07-02
 * @description
 */
public class ContainerLoad<T> extends AbstractLoad<T>{

    private static List<String> basePackages = Lists.newArrayList("com.ifcc.irpc");

    private final static Map<Class<?>, ContainerLoad<?>> CONTAINER_LOAD_MAP = new ConcurrentHashMap<>();

    private ContainerLoad(Class<T> type) {
        super(ExtensionLoad.getExtensionLoad(ExtensionFactory.class).getDefaultExtension(), type);
    }

    @SuppressWarnings("unchecked")
    public static <T> ContainerLoad<T> getContainerLoad(Class<T> type) {
        if(type == null) {
            throw new IllegalArgumentException("Class type cannot be null.");
        }
        return (ContainerLoad<T>) CONTAINER_LOAD_MAP.computeIfAbsent(type, ContainerLoad::new);
    }

    public static void addBasePackages(List<String> basePackages) {
        ContainerLoad.basePackages.addAll(basePackages);
    }

    @Override
    protected Map<String, Class<?>> loadExtensionClass() {
        Map<String, Class<?>> classMap = Maps.newHashMap();
        if (this.getType().isInterface()) {
            IrpcFactory factory = AnnotationUtil.findAnnotation(this.getType(), IrpcFactory.class);
            if (factory != null) {
                classMap.put(this.getType().getName(), this.getType());
                return classMap;
            }
            Set<Class<?>> classes = ClassQueryBuilder.build()
                    .andBasePackages(basePackages)
                    .andInterfaceClass(this.getType())
                    .andAnnotationClass(Cell.class)
                    .toSet();
            for (Class<?> clazz : classes) {
                Cell cell = clazz.getAnnotation(Cell.class);
                if(cell != null) {
                    String name = this.getType().getName();
                    if (StringUtils.isNotBlank(cell.value())) {
                        name = cell.value();
                    } else {
                        char[] chars = clazz.getSimpleName().toCharArray();
                        chars[0] += 32;
                        name = String.valueOf(chars);
                    }
                    classMap.put(name, clazz);
                }
            }
        } else {
            String name = this.getType().getName();
            Cell cell = this.getType().getAnnotation(Cell.class);
            if(cell != null) {
                if (StringUtils.isNotBlank(cell.value())) {
                    name = cell.value();
                } else {
                    char[] chars = this.getType().getSimpleName().toCharArray();
                    chars[0] += 32;
                    name = String.valueOf(chars);
                }
                classMap.put(name, this.getType());
            }
        }
        return classMap;
    }

}
