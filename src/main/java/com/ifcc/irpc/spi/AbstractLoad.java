package com.ifcc.irpc.spi;

import com.ifcc.irpc.common.config.IConfigProvider;
import com.ifcc.irpc.spi.annotation.Config;
import com.ifcc.irpc.spi.annotation.ConfigSource;
import com.ifcc.irpc.spi.annotation.Inject;
import com.ifcc.irpc.spi.factory.ExtensionFactory;
import com.ifcc.irpc.utils.PlaceholderUtil;
import org.apache.commons.lang3.StringUtils;

import java.lang.reflect.Field;
import java.lang.reflect.Method;
import java.util.Map;
import java.util.Properties;
import java.util.Set;
import java.util.TreeSet;
import java.util.concurrent.ConcurrentHashMap;

/**
 * @author chenghaifeng
 * @date 2020-07-02
 * @description
 */
public abstract class AbstractLoad<T> {

    private final static String DEFAULT_CONFIG_PATH = "irpc.properties";

    private ExtensionFactory factory;

    /**
     * key 实现类别名
     * value 实现类class
     */
    private Map<String, Class<?>> extensionClasses;

    private Map<String, Object> instances = new ConcurrentHashMap<>();

    protected AbstractLoad() {}

    protected AbstractLoad(ExtensionFactory factory) {
        this.factory = factory;
    }

    protected void setFactory(ExtensionFactory factory) {
        this.factory = factory;
    }

    public T getExtension(String name) {
        if (StringUtils.isBlank(name)) {
            throw new IllegalArgumentException("Extension name cannot be null or empty.");
        }
        T instance = (T)instances.get(name);
        if (instance != null) {
            return instance;
        }
        synchronized (this) {
            instance = (T)instances.get(name);
            if (instance != null) {
                return instance;
            }
            return createExtension(name);
        }
    }

    public Set<String> getSupportedExtensions() {
        return new TreeSet<>(getExtensionClasses().keySet());
    }

    protected T createExtension(String name) {
        Class<?> clazz = this.getExtensionClasses().get(name);
        if (clazz == null) {
            return null;
        }
        try {
            T instance = (T)clazz.newInstance();
            instances.put(name, instance);
            // 依赖注入
            this.injectExtension(instance);
            this.initExtension(instance);
            return instance;
        } catch (Exception e) {
            throw new IllegalStateException(e);
        }
    }

    protected abstract Map<String, Class<?>> loadExtensionClass();

    protected Map<String, Class<?>> getExtensionClasses() {
        if (extensionClasses == null) {
            synchronized (this) {
                extensionClasses = this.loadExtensionClass();
            }
        }
        return extensionClasses;
    }

    private T initExtension(T instance) {
        try {
            Method init = instance.getClass().getDeclaredMethod("init", null);
            if (init != null) {
                init.invoke(instance, null);
            }
        } catch (Exception e) {
            return instance;
        }
        return instance;
    }

    private T injectExtension(T instance) {
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
