package com.ifcc.irpc.utils;

import com.google.common.collect.Lists;
import com.google.common.collect.Sets;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;

import java.io.File;
import java.lang.reflect.Modifier;
import java.net.JarURLConnection;
import java.net.URL;
import java.util.Arrays;
import java.util.Enumeration;
import java.util.List;
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
                        listPackages(file.getName(), classpaths);
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

    private static Set<Class<?>> getClassByFile(Class<?> target, String basePackage) {
        Set<Class<?>> subclasses = Sets.newHashSet();
        try {
            if (target.isInterface()) {
                List<String> classpaths = Lists.newArrayList();
                listPackages(basePackage, classpaths);
                // 获取所有类,然后判断是否是 target 接口的实现类
                for (String classpath : classpaths) {
                    Class<?> classObject = Class.forName(classpath);
                    if(!target.isAssignableFrom(classObject)) {
                        continue;
                    }
                    if (Modifier.isAbstract(classObject.getModifiers()) || Modifier.isInterface(classObject.getModifiers())) {
                        continue;
                    }
                    subclasses.add(classObject);
                }
            } else {
                throw new IllegalArgumentException("Class is not a interface: " + target.getName());
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
    private static void listPackages(String basePackage, List<String> classes) {
        URL url = ClassUtil.class.getClassLoader()
                .getResource("./" + basePackage.replaceAll("\\.", "/"));
        File directory = new File(url.getFile());
        for (File file : directory.listFiles()) {
            // 如果是一个目录就继续往下读取(递归调用)
            if (file.isDirectory()) {
                listPackages(basePackage + (StringUtils.isNotBlank(basePackage) ? "." : "") + file.getName(), classes);
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
        if (StringUtils.isBlank(basePackage)) {
            basePackage = "com.ifcc.irpc";
        }
        Set<Class<?>> classes = Sets.newHashSet();
        try {
            ClassLoader loader = Thread.currentThread().getContextClassLoader();
            String packagePath = basePackage.replace(".", "/");
            Enumeration<URL> urls = loader.getResources(packagePath);
            URL url;
            while ( urls.hasMoreElements() ) {
                url = urls.nextElement();
                String type = url.getProtocol();
//                System.out.println(url);
                if ("file".equals(type)) {
                    classes.addAll(getClassByFile(target, basePackage));
                } else if ("jar".equals(type)) {
                    classes.addAll(getClassByJar(url.getPath(), target, true));
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return classes;
    }


    /**
     * 从项目文件获取某包下所有类
     * @param filePath 文件路径
     * @param childPackage 是否遍历子包
     * @return 类的完整名称
     */
    private static Set<String> getClassNameByFile(String filePath, boolean childPackage) {
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
                    childFilePath = childFilePath.substring(childFilePath.indexOf("\\classes") + 9, childFilePath.lastIndexOf("."));
                    childFilePath = childFilePath.replace("\\", ".");
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
    private static Set<Class<?>> getClassByJars(URL[] urls, String packagePath, Class<?> target, boolean childPackage) {
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
                classes.addAll(getClassByJar(jarPath, target, childPackage));
            }
        }
        return classes;
    }


    /**
     * 从jar获取某包下所有类
     * @param jarPath jar文件路径
     * @param childPackage 是否遍历子包
     * @return 类的完整名称
     */
    private static Set<Class<?>> getClassByJar(String jarPath, Class<?> target, boolean childPackage) {
        Set<Class<?>> myClass = Sets.newHashSet();
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
                                Class<?> clazz = Class.forName(entryName);
                                if(!target.isAssignableFrom(clazz)) {
                                    continue;
                                }
                                if (Modifier.isAbstract(clazz.getModifiers()) || Modifier.isInterface(clazz.getModifiers())) {
                                    continue;
                                }
                                myClass.add(clazz);
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
                                Class<?> clazz = Class.forName(entryName);
                                if(!target.isAssignableFrom(clazz)) {
                                    continue;
                                }
                                if (Modifier.isAbstract(clazz.getModifiers()) || Modifier.isInterface(clazz.getModifiers())) {
                                    continue;
                                }
                                myClass.add(clazz);
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



    public static void findClassJar(final String packName, Class<?> target, List<Class<?>> list){
        String pathName = packName.replace(".", "/");
        JarFile jarFile  = null;
        try {
            URL url = target.getClassLoader().getResource(pathName);
            JarURLConnection jarURLConnection  = (JarURLConnection )url.openConnection();
            jarFile = jarURLConnection.getJarFile();
        } catch (Exception e) {
            e.printStackTrace();
            throw new RuntimeException("未找到策略资源");
        }

        Enumeration<JarEntry> jarEntries = jarFile.entries();
        while (jarEntries.hasMoreElements()) {
            JarEntry jarEntry = jarEntries.nextElement();
            String jarEntryName = jarEntry.getName();

            if(jarEntryName.contains(pathName) && !jarEntryName.equals(pathName+"/")){
                //递归遍历子目录
                if(jarEntry.isDirectory()){
                    String clazzName = jarEntry.getName().replace("/", ".");
                    int endIndex = clazzName.lastIndexOf(".");
                    String prefix = null;
                    if (endIndex > 0) {
                        prefix = clazzName.substring(0, endIndex);
                    }
                    findClassJar(prefix, target, list);
                }
                if(jarEntry.getName().endsWith(".class")){
                    Class<?> clazz = null;
                    try {
                        clazz = target.getClassLoader().loadClass(jarEntry.getName().replace("/", ".").replace(".class", ""));
                    } catch (ClassNotFoundException e) {
                        e.printStackTrace();
                    }
                    if(target.isAssignableFrom(clazz)){
                        list.add(clazz);
                    }
                }
            }

        }

    }

}
