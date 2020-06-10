package com.ifcc.irpc.annotation.retry;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;
import java.util.concurrent.TimeUnit;

/**
 * @author chenghaifeng
 * @date 2020-06-08
 * @description 重试注解
 */
@Target({ElementType.METHOD})
@Retention(RetentionPolicy.RUNTIME)
public @interface Retry {
    /**
     * 重试次数
     * 默认2次
     * @return
     */
    int retryTimes() default 2;

    /**
     * 重试延迟时间
     * 默认不延迟
     * @return
     */
    int delayTime() default 0;

    /**
     * 延迟时间单位
     * 默认分钟
     * @return
     */
    TimeUnit delayTimeUnit() default TimeUnit.MINUTES;
}
