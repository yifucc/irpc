package com.ifcc.irpc.utils;

import com.google.common.collect.Lists;
import com.google.common.collect.Sets;
import com.sun.istack.internal.NotNull;

import java.io.File;
import java.lang.reflect.Modifier;
import java.net.URL;
import java.util.Arrays;
import java.util.List;
import java.util.Set;

/**
 * @author chenghaifeng
 * @date 2020-07-01
 * @description
 */
public class ClassUtil {
    /**
     * 获取一个接口的所有实现类(直接或者间接)
     *
     * @param target
     * @return
     */
    public static List<Class<?>> getInterfaceImpls(Class<?> target) {
        List<Class<?>> subclasses = Lists.newArrayList();
        try {
            // 判断class对象是否是一个接口
            if (target.isInterface()) {
                @NotNull
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
                        subclasses.add(Class.forName(classObject.getName()));
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
                listPackages(basePackage + "." + file.getName(), classes);
            } else {
                // 如果不是一个目录,判断是不是以.class结尾的文件,如果不是则不作处理
                String classpath = file.getName();
                if (".class".equals(classpath.substring(classpath.length() - ".class".length()))) {
                    classes.add(basePackage + "." + classpath.replaceAll(".class", ""));
                }
            }
        }
    }
}
