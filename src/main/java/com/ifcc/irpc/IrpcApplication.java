package com.ifcc.irpc;

import com.ifcc.irpc.annotation.server.IrpcProvider;
import com.ifcc.irpc.annotation.server.IrpcServer;
import com.ifcc.irpc.common.ClassQueryBuilder;
import com.ifcc.irpc.common.URL;
import com.ifcc.irpc.common.config.IrpcConfig;
import com.ifcc.irpc.registry.Registry;
import com.ifcc.irpc.spi.ExtensionLoad;
import com.ifcc.irpc.spi.factory.ExtensionFactory;
import com.ifcc.irpc.utils.LocalIpUtil;

import java.util.Arrays;
import java.util.Set;

/**
 * @author chenghaifeng
 * @date 2020-07-06
 * @description
 */
public final class IrpcApplication {
    public static void run(Class<?> clazz, String[] args) {
        IrpcServer irpcServer = clazz.getAnnotation(IrpcServer.class);
        String[] basePackages = irpcServer.scanBasePackages();
        Set<Class<?>> classes = ClassQueryBuilder.build()
                .andAnnotationClass(IrpcProvider.class)
                .andBasePackages(Arrays.asList(basePackages))
                .toSet();
        ExtensionFactory extension = ExtensionLoad.getExtensionLoad(ExtensionFactory.class).getDefaultExtension();
        Registry registry = extension.getExtension(Registry.class);
        IrpcConfig config = extension.getExtension(IrpcConfig.class);
        for (Class<?> c : classes) {
            URL url = new URL(LocalIpUtil.localRealIp(), config.getPort(), c.getName());
            try {
                registry.register(url);
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
    }
}
