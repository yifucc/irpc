package com.ifcc.irpc.utils;

import com.google.common.collect.Lists;

import java.lang.annotation.Annotation;
import java.lang.annotation.Documented;
import java.lang.annotation.Inherited;
import java.lang.annotation.Retention;
import java.lang.annotation.Target;
import java.util.List;

/**
 * @author chenghaifeng
 * @date 2020-07-20
 * @description
 */
public class AnnotationUtil {
    private static final List<Class<? extends Annotation>> META_ANNOTATIONS = Lists.newArrayList(Target.class, Retention.class, Documented.class, Inherited.class);

    public static <T extends Annotation> T getAnnotation(Class<?> clazz, Class<T> annotationClass) {
        T annotation = null;
        while (annotation == null) {
            annotation = clazz.getAnnotation(annotationClass);
            clazz = clazz.getSuperclass();
            if (clazz == null) {
                break;
            }
        }
        return annotation;
    }

    public static <T extends Annotation> T findAnnotation(Class<?> clazz, Class<T> annotationClass) {
        T annotation = getAnnotation(clazz, annotationClass);
        if (annotation != null) {
            return annotation;
        }
        annotation = findAnnotationInner(clazz, annotationClass);
        return annotation;
    }

    @SuppressWarnings("unchecked")
    private static <T extends Annotation> T findAnnotationInner(Class<?> clazz, Class<T> annotationClass) {
        T annotation = null;
        if (clazz == null) {
            return annotation;
        }
        Annotation[] annotations = clazz.getAnnotations();
        for (Annotation a : annotations) {
            if (META_ANNOTATIONS.contains(a.annotationType())) {
                continue;
            }
            if (a.annotationType().equals(annotationClass)) {
                annotation = (T) a;
                break;
            }
            annotation = findAnnotationInner(a.annotationType(), annotationClass);
            if (annotation != null) {
                break;
            }
        }
        return annotation;
    }
}
