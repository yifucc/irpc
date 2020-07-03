package com.ifcc.irpc.spi;

import com.ifcc.irpc.spi.annotation.SPI;
import com.ifcc.irpc.spi.factory.ExtensionFactory;
import org.apache.commons.lang3.StringUtils;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.net.URL;
import java.nio.charset.StandardCharsets;
import java.util.Enumeration;
import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import java.util.regex.Pattern;

/**
 * @author chenghaifeng
 * @date 2020-06-12
 * @description spi加载器
 */
public class ExtensionLoad<T> extends AbstractLoad<T> {

    private static final String IRPC_INTERNAL_DIRECTORY = "META-INF/irpc/internal/";
    private static final String IRPC_DIRECTORY = "META-INF/irpc/";
    private static final Pattern NAME_SEPARATOR = Pattern.compile("\\s*[,]+\\s*");
//    private final static String DEFAULT_CONFIG_PATH = "irpc.properties";

    private final static Map<Class<?>, ExtensionLoad<?>> EXTENSION_LOAD_MAP = new ConcurrentHashMap<>();

    /**
     * key 实现类别名
     * value 实现类class
     */
//    private Map<String, Class<?>> extensionClasses;

    private Class<T> interfaceClass;

    private String defaultName;

//    private Map<String, Object> instances = new ConcurrentHashMap<>();

//    private final ExtensionFactory factory;


    private ExtensionLoad(Class<T> interfaceClass) {
        super();
        this.interfaceClass = interfaceClass;
        ExtensionFactory factory = interfaceClass == ExtensionFactory.class? null : ExtensionLoad.getExtensionLoad(ExtensionFactory.class).getDefaultExtension();
        super.setFactory(factory);
    }

    public static <T> ExtensionLoad<T> getExtensionLoad(Class<T> interfaceClass) {
        if (!interfaceClass.isInterface()) {
            throw new IllegalArgumentException("Class is not a interface: " + interfaceClass.getName());
        }
        if (!interfaceClass.isAnnotationPresent(SPI.class)) {
            throw new IllegalArgumentException("Class is not annotated by SPI: " + interfaceClass.getName());
        }
        ExtensionLoad<T> loader = (ExtensionLoad<T>) EXTENSION_LOAD_MAP.get(interfaceClass);
        if(loader == null) {
            EXTENSION_LOAD_MAP.putIfAbsent(interfaceClass, new ExtensionLoad<T>(interfaceClass));
            loader = (ExtensionLoad<T>) EXTENSION_LOAD_MAP.get(interfaceClass);
        }
        return loader;
    }

   /* @Override
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
    }*/

    public T getDefaultExtension() {
        if (StringUtils.isBlank(defaultName)) {
            getExtensionClasses();
        }
        if (StringUtils.isBlank(defaultName)) {
            return null;
        }
        return this.getExtension(defaultName);
    }

    /*protected T createExtension(String name) {
        Class<?> clazz = this.getExtensionClasses().get(name);
        try {
            T instance = (T)clazz.newInstance();
            instances.put(name, instance);
            // 依赖注入
            instance = injectExtension(instance);
            initExtension(instance);
            return instance;
        } catch (Exception e) {
            throw new IllegalStateException(e);
        }
    }*/

    /*private T initExtension(T instance) {
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
            for (Field field : fields) {
                Inject inject = field.getAnnotation(Inject.class);
                Config config = field.getAnnotation(Config.class);
                if (inject != null) {
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
                }
                if (config != null) {
                    IConfigProvider<Properties> provider = factory.getExtension(IConfigProvider.class, "properties");
                    Properties props = provider.provide(configPath);
                    String value = PlaceholderUtil.resolveStringValue(props, config.value());
                    if (StringUtils.isBlank(value) && config.required()) {
                        throw new IllegalStateException("The config cannot be empty: " + config.value());
                    }
                    field.setAccessible(true);
                    field.set(instance, value);
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return instance;
    }*/

   /* private Map<String, Class<?>> getExtensionClasses() {
        if (extensionClasses == null) {
            synchronized (this) {
                extensionClasses = loadExtensionClass();
            }
        }
        return extensionClasses;
    }

    public Set<String> getSupportedExtensions() {
        return new TreeSet<>(getExtensionClasses().keySet());
    }*/

   @Override
    protected Map<String, Class<?>> loadExtensionClass() {
        SPI spi = interfaceClass.getAnnotation(SPI.class);
        if (spi != null) {
            String name = spi.value();
            if (StringUtils.isNotBlank(name)) {
                String[] names = NAME_SEPARATOR.split(name);
                if (names.length > 1) {
                    throw new IllegalArgumentException("More than 1 default extension name: ");
                }
                if (names.length == 1) {
                    this.defaultName = names[0];
                }
            }
        }
        Map<String, Class<?>> classMap = new HashMap<>();
        loadDirectory(classMap, IRPC_INTERNAL_DIRECTORY);
        loadDirectory(classMap, IRPC_DIRECTORY);
        return classMap;
    }

    private void loadDirectory(Map<String, Class<?>> classMap, String path) {
        String fileName = path + interfaceClass.getName();
        ClassLoader classLoader = this.getClass().getClassLoader();
        try {
            Enumeration<URL> resources = classLoader.getResources(fileName);
            if (resources != null) {
                while (resources.hasMoreElements()) {
                    URL url = resources.nextElement();
                    loadResource(classMap, classLoader, url);
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
        }

    }

    private void loadResource(Map<String, Class<?>> classMap, ClassLoader classLoader, URL url) {
        try {
            BufferedReader reader = new BufferedReader(new InputStreamReader(url.openStream(), StandardCharsets.UTF_8));
            try {
                String line;
                while ((line = reader.readLine()) != null) {
                    int in = line.indexOf("#");
                    if (in >= 0) {
                        line = line.substring(0, in);
                    }
                    line = line.trim();
                    if (line.length() > 0) {
                        String name = null;
                        int i = line.indexOf("=");
                        if (i > 0) {
                            name = line.substring(0, i).trim();
                            line = line.substring(i + 1).trim();
                        }
                        if (line.length() > 0) {
                            classMap.put(name, Class.forName(line, true, classLoader));
                        }
                    }
                }
            } finally {
                reader.close();
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}
