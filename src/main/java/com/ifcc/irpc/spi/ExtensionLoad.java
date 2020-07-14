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

    private final static Map<Class<?>, ExtensionLoad<?>> EXTENSION_LOAD_MAP = new ConcurrentHashMap<>();

    /**
     * key 实现类别名
     * value 实现类class
     */

    private Class<T> interfaceClass;

    private String defaultName;

    private ExtensionLoad(Class<T> interfaceClass) {
        super();
        this.interfaceClass = interfaceClass;
        ExtensionFactory factory = interfaceClass == ExtensionFactory.class? null : ExtensionLoad.getExtensionLoad(ExtensionFactory.class).getDefaultExtension();
        super.setFactory(factory);
    }

    @SuppressWarnings("unchecked")
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

    public T getDefaultExtension() {
        if (StringUtils.isBlank(defaultName)) {
            getExtensionClasses();
        }
        if (StringUtils.isBlank(defaultName)) {
            return null;
        }
        return this.getExtension(defaultName);
    }

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
