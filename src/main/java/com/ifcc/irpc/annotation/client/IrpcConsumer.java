package com.ifcc.irpc.annotation.client;

import com.ifcc.irpc.annotation.IrpcFactory;
import com.ifcc.irpc.client.wrapper.ProxyWrapper;

import java.lang.annotation.Documented;
import java.lang.annotation.ElementType;
import java.lang.annotation.Inherited;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

/**
 * @author chenghaifeng
 * @date 2020-07-07
 * @description
 */
@Target({ElementType.TYPE})
@Retention(RetentionPolicy.RUNTIME)
@Inherited
@Documented
@IrpcFactory(factoryClass = ProxyWrapper.class)
public @interface IrpcConsumer {
    String targetName() default "";
}
