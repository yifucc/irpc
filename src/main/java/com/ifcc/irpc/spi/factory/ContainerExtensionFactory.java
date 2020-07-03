package com.ifcc.irpc.spi.factory;

import com.ifcc.irpc.common.config.IConfigProvider;
import com.ifcc.irpc.spi.ContainerLoad;
import com.ifcc.irpc.spi.annotation.Cell;
import com.ifcc.irpc.spi.annotation.Config;
import com.ifcc.irpc.spi.annotation.ConfigSource;
import com.ifcc.irpc.spi.annotation.Inject;
import com.ifcc.irpc.spi.annotation.SPI;
import com.ifcc.irpc.utils.ClassUtil;
import com.ifcc.irpc.utils.PlaceholderUtil;
import org.apache.commons.lang3.StringUtils;

import java.lang.reflect.Field;
import java.lang.reflect.Method;
import java.util.List;
import java.util.Map;
import java.util.Properties;
import java.util.concurrent.ConcurrentHashMap;

/**
 * @author chenghaifeng
 * @date 2020-07-01
 * @description
 */
public class ContainerExtensionFactory implements ExtensionFactory {

    private final static String DEFAULT_CONFIG_PATH = "irpc.properties";

    private final Map<String, Object> instances = new ConcurrentHashMap<>();

    private ExtensionFactory factory;

    public ContainerExtensionFactory() {}

    /*public void init() {
        this.factory = ExtensionLoad.getExtensionLoad(ExtensionFactory.class).getDefaultExtension();
    }*/

    @Override
    public <T> T getExtension(Class<T> type, String name) {
        if (type.isInterface() && type.isAnnotationPresent(SPI.class)) {
            return null;
        }
        Object o = ContainerLoad.getContainerLoad(type).getExtension(name);
        /*Object o = instances.get(name);
        if (o == null) {
            o = instances.get(type.getName());
        }
        if (o == null) {
            createExtension(type);
        }*/
        return (T) (o != null? o : ContainerLoad.getContainerLoad(type).getExtension(type.getName()));
    }

    @Override
    public <T> T getExtension(Class<T> type) {
        if (type.isInterface() && type.isAnnotationPresent(SPI.class)) {
            return null;
        }
        /*Object o = instances.get(type.getName());
        if (o == null) {
            createExtension(type);
        }*/
        return ContainerLoad.getContainerLoad(type).getExtension(type.getName());
    }

    private <T> void createExtension(Class<T> type) {
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
                    try {
                        Object o = clazz.newInstance();
                        instances.put(cell.value(), o);
                        injectExtension(o);
                        initExtension(o);
                    } catch (Exception e) {
                        e.printStackTrace();
                    }
                }
            } else {
                Class<?> clazz = classes.get(0);
                Cell cell = clazz.getAnnotation(Cell.class);
                String name = clazz.getName();
                if(cell != null && StringUtils.isNotBlank(cell.value())) {
                    name = cell.value();
                }
                try {
                    Object o = clazz.newInstance();
                    instances.put(name, o);
                    injectExtension(o);
                    initExtension(o);
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
        } else {
            try {
                T instance = type.newInstance();
                String name = type.getName();
                Cell cell = type.getAnnotation(Cell.class);
                if(cell != null && StringUtils.isNotBlank(cell.value())) {
                    name = cell.value();
                }
                instances.put(name, instance);
                injectExtension(instance);
                initExtension(instance);
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
    }

    private <T> T initExtension(T instance) {
        try {
            Method init = instance.getClass().getDeclaredMethod("init", null);
            if (init != null) {
                init.invoke(instance, null);
            }
        } catch (Exception e) {
            //e.printStackTrace();
            return instance;
        }
        return instance;
    }

    private <T> T injectExtension(T instance) {
        if(instance == null) {
            return instance;
        }
        try {
            String configPath = DEFAULT_CONFIG_PATH;
            ConfigSource configSource = instance.getClass().getAnnotation(ConfigSource.class);
            if (configSource != null && StringUtils.isNotBlank(configSource.value())) {
                configPath = configSource.value();
            }
            Field[] fields = instance.getClass().getDeclaredFields();
            for(Field field : fields) {
                Inject inject = field.getAnnotation(Inject.class);
                Config config = field.getAnnotation(Config.class);
                if(inject != null) {
                    Object injectValue = null;
                    if (StringUtils.isNotBlank(inject.value())) {
                        injectValue = factory.getExtension(field.getType(), inject.value());
                    } else {
                        injectValue = factory.getExtension(field.getType());
                    }
                    field.setAccessible(true);
                    if (injectValue != null) {
                        field.set(instance, injectValue);
                    }
                } else if (config != null) {
                    IConfigProvider<Properties> provider = factory.getExtension(IConfigProvider.class, "properties");
                    Properties props = provider.provide(configPath);
                    String value = PlaceholderUtil.resolveStringValue(props, config.value());
                    if (StringUtils.isBlank(value) && config.required()) {
                        throw new IllegalStateException("The config cannot be empty: " + config.value());
                    }
                    field.setAccessible(true);
                    if (field.getType().isAssignableFrom(int.class) || field.getType().isAssignableFrom(Integer.class)) {
                        field.set(instance, Integer.parseInt(value));
                    } else if (field.getType().isAssignableFrom(long.class) || field.getType().isAssignableFrom(Long.class)) {
                        field.set(instance, Long.parseLong(value));
                    } else if (field.getType().isAssignableFrom(float.class) || field.getType().isAssignableFrom(Float.class)) {
                        field.set(instance, Float.parseFloat(value));
                    } else if (field.getType().isAssignableFrom(double.class) || field.getType().isAssignableFrom(Double.class)) {
                        field.set(instance, Double.parseDouble(value));
                    } else if (field.getType().isAssignableFrom(boolean.class) || field.getType().isAssignableFrom(Boolean.class)) {
                        field.set(instance, Boolean.parseBoolean(value));
                    } else if (field.getType().isAssignableFrom(short.class) || field.getType().isAssignableFrom(Short.class)) {
                        field.set(instance, Short.parseShort(value));
                    } else if (field.getType().isAssignableFrom(byte.class) || field.getType().isAssignableFrom(Byte.class)) {
                        field.set(instance, Byte.parseByte(value));
                    } else {
                        field.set(instance, value);
                    }
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
        }

        return instance;
    }
}
