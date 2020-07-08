package com.ifcc.irpc.common;

import com.google.common.collect.Lists;
import com.ifcc.irpc.utils.ClassUtil;

import java.lang.annotation.Annotation;
import java.lang.reflect.Modifier;
import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;
import java.util.stream.Stream;

/**
 * @author chenghaifeng
 * @date 2020-07-08
 * @description
 */
public class ClassQueryBuilder {
    private List<String> basePackages;
    private Class<?> interfaceClass;
    private Class<? extends Annotation> annotationClass;

    public static ClassQueryBuilder build() {
        return new ClassQueryBuilder();
    }

    public ClassQueryBuilder andBasePackages(List<String> basePackages) {
        this.basePackages = basePackages;
        return this;
    }

    public ClassQueryBuilder andInterfaceClass(Class<?> interfaceClass) {
        this.interfaceClass = interfaceClass;
        return this;
    }

    public ClassQueryBuilder andAnnotationClass(Class<? extends Annotation> annotationClass) {
        this.annotationClass = annotationClass;
        return this;
    }

    public Set<Class<?>> toSet() {
        if (basePackages == null) {
            basePackages = Lists.newArrayList("com.ifcc.irpc");
        }
        Set<Class<?>> set = ClassUtil.getAllClassByPackages(basePackages);
        Stream<Class<?>> stream = set.stream();
        if (interfaceClass != null) {
            stream = stream.filter( clazz -> {
                if(clazz == null) {
                    return false;
                } else if(!interfaceClass.isAssignableFrom(clazz)) {
                    return false;
                } else if(Modifier.isAbstract(clazz.getModifiers()) || Modifier.isInterface(clazz.getModifiers())) {
                    return false;
                }
                return true;
            });
        }

        if (annotationClass != null) {
            stream = stream.filter(clazz -> clazz.getAnnotation(annotationClass) != null);
        }

        return stream.collect(Collectors.toSet());
    }
}
