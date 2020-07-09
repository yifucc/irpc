package com.ifcc.irpc.utils;

import com.google.common.collect.Lists;
import com.google.common.collect.Maps;
import com.google.common.collect.Sets;
import com.ifcc.irpc.common.Holder;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;

import java.io.File;
import java.lang.annotation.Annotation;
import java.lang.reflect.Modifier;
import java.net.URL;
import java.util.Arrays;
import java.util.Enumeration;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.jar.JarEntry;
import java.util.jar.JarFile;

/**
 * @author chenghaifeng
 * @date 2020-07-01
 * @description
 */
@Slf4j
public class ClassUtil {

    private static final Map<String, Holder<Class<?>>> CLASS_CACHE = Maps.newConcurrentMap();

    private static final Map<String, Holder<Set<Class<?>>>> PACKAGE_CACHE = Maps.newConcurrentMap();

    /**
     * 获取一个接口的所有实现类(直接或者间接)
     *
     * Class.getResource(""):获取当前类所在的绝对路径
     * ClassLoader.getResource(""):获取根目录 等价于 Class.getResource("/")
     *
     * @param target
     * @return
     */
    public static List<Class<?>> getInterfaceImpls(Class<?> target) {
        List<Class<?>> subclasses = Lists.newArrayList();
        try {
            // 判断class对象是否是一个接口
            if (target.isInterface()) {
                String basePackage = target.getClassLoader().getResource("").getPath();
                File[] files = new File(basePackage).listFiles();
                // 存放class路径的list
                List<String> classpaths = Lists.newArrayList();
                for (File file : files) {
                    // 扫描项目编译后的所有类
                    if (file.isDirectory()) {
                        // listPackages(file.getName(), classpaths);
                    }
                }
                // 获取所有类,然后判断是否是 target 接口的实现类
                for (String classpath : classpaths) {
                    Class<?> classObject = Class.forName(classpath);
                    if (Modifier.isAbstract(classObject.getModifiers()) || Modifier.isInterface(classObject.getModifiers())) {
                        continue;
                    }
                    Set<Class<?>> interfaces = Sets.newHashSet();
                    getInterfacesByCycle(classObject, interfaces);
                    if (interfaces.contains(target)) {
                        subclasses.add(classObject);
                    }
                }
            } else {
                throw new IllegalArgumentException("Class对象不是一个interface");
            }
        } catch (Throwable e) {
            e.printStackTrace();
        }
        return subclasses;
    }

    private static Set<Class<?>> getClassByFile(String path, String basePackage, Set<Class<?>> subclasses) {
        try {
            List<String> classpaths = Lists.newArrayList();
            listPackages(path, basePackage, classpaths);
            // 获取所有类,然后判断是否是 target 接口的实现类
            for (String classpath : classpaths) {
                // Class<?> classObject = Class.forName(classpath);
                Class<?> classObject = getClassByName(classpath);
                /*if(!target.isAssignableFrom(classObject)) {
                    continue;
                }
                if (Modifier.isAbstract(classObject.getModifiers()) || Modifier.isInterface(classObject.getModifiers())) {
                    continue;
                }*/
                if (classObject != null) {
                    subclasses.add(classObject);
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return subclasses;
    }

    private static void getInterfacesByCycle(Class<?> clazz, Set<Class<?>> interfaces) {
        interfaces.addAll(Arrays.asList(clazz.getInterfaces()));
        if (!clazz.getSuperclass().equals(Object.class)) {
            getInterfacesByCycle(clazz.getSuperclass(), interfaces);
        }
    }

    /**
     * 获取项目编译后的所有的.class的字节码文件
     * 这么做的目的是为了让 Class.forName() 可以加载类
     *
     * @param basePackage 默认包名
     * @param classes     存放字节码文件路径的集合
     * @return
     */
    private static void listPackages(String path, String basePackage, List<String> classes) {
        /*URL url = ClassUtil.class.getClassLoader()
                .getResource("./" + basePackage.replaceAll("\\.", "/"));*/
        File directory = new File(path);
        for (File file : directory.listFiles()) {
            // 如果是一个目录就继续往下读取(递归调用)
            if (file.isDirectory()) {
                listPackages(path + "/" + file.getName(),basePackage + (StringUtils.isNotBlank(basePackage) ? "." : "") + file.getName(), classes);
            } else {
                // 如果不是一个目录,判断是不是以.class结尾的文件,如果不是则不作处理
                String classpath = file.getName();
                if (".class".equals(classpath.substring(classpath.length() - ".class".length()))) {
                    classes.add(basePackage + "." + classpath.replaceAll(".class", ""));
                }
            }
        }
    }

    public static Set<Class<?>> getAllSubClass(Class<?> target, String basePackage) {
        if(!target.isInterface()) {
            throw new IllegalArgumentException("Class is not a interface: " + target.getName());
        }
        if (StringUtils.isBlank(basePackage)) {
            basePackage = "com.ifcc.irpc";
        }
        Set<Class<?>> classes = Sets.newHashSet();
        Set<Class<?>> packagesClasses = getAllClassByPackages(Lists.newArrayList(basePackage));
        for (Class<?> clazz : packagesClasses) {
            if(clazz == null) {
                continue;
            }
            if(!target.isAssignableFrom(clazz)) {
                continue;
            }
            if (Modifier.isAbstract(clazz.getModifiers()) || Modifier.isInterface(clazz.getModifiers())) {
                continue;
            }
            classes.add(clazz);
        }
        return classes;
    }

    public static Set<Class<?>> getAllClassByAnnotation(Class<? extends Annotation> annotationClass, List<String> basePackages) {
        Set<Class<?>> classes = Sets.newHashSet();
        Set<Class<?>> packagesClasses = getAllClassByPackages(basePackages);
        for (Class<?> clazz : packagesClasses) {
            if (clazz.getAnnotation(annotationClass) != null ) {
                classes.add(clazz);
            }
        }
        return classes;
    }

    public static Set<Class<?>> getAllClassByPackages(List<String> basePackages) {
        Set<Class<?>> classes = Sets.newHashSet();
        for (String basePackage : basePackages) {
            classes.addAll(getAllClassByPackage(basePackage));
        }
        return classes;
    }

    private static Set<Class<?>> getAllClassByPackage(String basePackage) {
        Holder<Set<Class<?>>> holder = PACKAGE_CACHE.computeIfAbsent(basePackage, pack -> new Holder<>());
        Set<Class<?>> classes = holder.get();
        if (classes != null) {
            return classes;
        }
        synchronized (holder) {
            classes = holder.get();
            if (classes != null) {
                return classes;
            }
            classes = Sets.newHashSet();
            try {
                ClassLoader loader = Thread.currentThread().getContextClassLoader();
                String packagePath = basePackage.replace(".", "/");
                Enumeration<URL> urls = loader.getResources(packagePath);
                URL url;
                while ( urls.hasMoreElements() ) {
                    url = urls.nextElement();
                    String type = url.getProtocol();
                    if ("file".equals(type)) {
                        getClassByFile(url.getPath(), basePackage, classes);
                    } else if ("jar".equals(type)) {
                        getClassByJar(url.getPath(), true, classes);
                    }
                }
            } catch (Exception e) {
                e.printStackTrace();
            }
            holder.set(classes);
            return classes;
        }
    }


    /**
     * 从项目文件获取某包下所有类
     * @param filePath 文件路径
     * @param childPackage 是否遍历子包
     * @return 类的完整名称
     */
    public static Set<String> getClassNameByFile(String filePath, boolean childPackage) {
        Set<String> myClassName = Sets.newHashSet();
        File file = new File(filePath);
        File[] childFiles = file.listFiles();
        for (File childFile : childFiles) {
            if (childFile.isDirectory()) {
                if (childPackage) {
                    myClassName.addAll(getClassNameByFile(childFile.getPath(), childPackage));
                }
            } else {
                String childFilePath = childFile.getPath();
                if (childFilePath.endsWith(".class")) {
                    childFilePath = childFilePath.substring(childFilePath.indexOf("/classes") + 9, childFilePath.lastIndexOf("."));
                    childFilePath = childFilePath.replace("/", ".");
                    myClassName.add(childFilePath);
                }
            }
        }

        return myClassName;
    }


    /**
     * 从所有jar中搜索该包，并获取该包下所有类
     * @param urls URL集合
     * @param packagePath 包路径
     * @param childPackage 是否遍历子包
     * @return 类的完整名称
     */
    private static Set<Class<?>> getClassByJars(URL[] urls, String packagePath, boolean childPackage) {
        Set<Class<?>> classes = Sets.newHashSet();
        if (urls != null) {
            for (int i = 0; i < urls.length; i++) {
                URL url = urls[i];
                String urlPath = url.getPath();
                // 不必搜索classes文件夹
                if (urlPath.endsWith("classes/")) {
                    continue;
                }
                String jarPath = urlPath + "!/" + packagePath;
                getClassByJar(jarPath, childPackage, classes);
            }
        }
        return classes;
    }


    private static Class<?> getClassByName(String classname) {
        Holder<Class<?>> holder = CLASS_CACHE.computeIfAbsent(classname, n -> new Holder<>());
        Class<?> clazz = holder.get();
        if(clazz == null) {
            synchronized (holder) {
                clazz = holder.get();
                if (clazz == null) {
                    try {
                        clazz = Class.forName(classname);
                        holder.set(clazz);
                    } catch (Throwable e) {
                        // cannot find the class
                    }
                }
            }
        }
        return clazz;
    }

    /**
     * 从jar获取某包下所有类
     * @param jarPath jar文件路径
     * @param childPackage 是否遍历子包
     * @return 类的完整名称
     */
    private static Set<Class<?>> getClassByJar(String jarPath, boolean childPackage, Set<Class<?>> myClass) {
        String[] jarInfo = jarPath.split("!");
        String jarFilePath = jarInfo[0].substring(jarInfo[0].indexOf("/"));
        String packagePath = jarInfo[1].substring(1);
        try {
            JarFile jarFile = new JarFile(jarFilePath);
            Enumeration<JarEntry> entrys = jarFile.entries();
            while (entrys.hasMoreElements()) {
                try {
                    JarEntry jarEntry = entrys.nextElement();
                    String entryName = jarEntry.getName();
                    if (entryName.endsWith(".class")) {
                        if (childPackage) {
                            if (entryName.startsWith(packagePath)) {
                                entryName = entryName.replace("/", ".").substring(0, entryName.lastIndexOf("."));
                                // Class<?> clazz = Class.forName(entryName);
                                Class<?> clazz = getClassByName(entryName);
                                /*if(!target.isAssignableFrom(clazz)) {
                                    continue;
                                }
                                if (Modifier.isAbstract(clazz.getModifiers()) || Modifier.isInterface(clazz.getModifiers())) {
                                    continue;
                                }*/
                                if(clazz != null) {
                                    myClass.add(clazz);
                                }
                            }
                        } else {
                            int index = entryName.lastIndexOf("/");
                            String myPackagePath;
                            if (index != -1) {
                                myPackagePath = entryName.substring(0, index);
                            } else {
                                myPackagePath = entryName;
                            }
                            if (myPackagePath.equals(packagePath)) {
                                entryName = entryName.replace("/", ".").substring(0, entryName.lastIndexOf("."));
                                // Class<?> clazz = Class.forName(entryName);
                                Class<?> clazz = getClassByName(entryName);
                                /*if(!target.isAssignableFrom(clazz)) {
                                    continue;
                                }
                                if (Modifier.isAbstract(clazz.getModifiers()) || Modifier.isInterface(clazz.getModifiers())) {
                                    continue;
                                }*/
                                if(clazz != null) {
                                    myClass.add(clazz);
                                }
                            }
                        }
                    }
                } catch (Throwable e) {
                    // e.printStackTrace();
                }
            }
        } catch (Exception e) {
            // e.printStackTrace();
        }
        return myClass;
    }

}
