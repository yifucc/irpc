package com.ifcc.irpc;

import com.ifcc.irpc.annotation.server.IrpcProvider;
import com.ifcc.irpc.annotation.server.IrpcServer;
import com.ifcc.irpc.common.ClassQueryBuilder;
import com.ifcc.irpc.common.URL;
import com.ifcc.irpc.common.config.IrpcConfig;
import com.ifcc.irpc.registry.Registry;
import com.ifcc.irpc.server.Server;
import com.ifcc.irpc.spi.ExtensionLoad;
import com.ifcc.irpc.spi.factory.ExtensionFactory;
import lombok.extern.slf4j.Slf4j;

import java.util.Arrays;
import java.util.Set;

/**
 * @author chenghaifeng
 * @date 2020-07-06
 * @description
 */
@Slf4j
public final class IrpcApplication {
    public static void run(Class<?> clazz, String[] args) {
        IrpcServer irpcServer = clazz.getAnnotation(IrpcServer.class);
        String[] basePackages = irpcServer.scanBasePackages();
        Set<Class<?>> classes = ClassQueryBuilder.build()
                .andAnnotationClass(IrpcProvider.class)
                .andBasePackages(Arrays.asList(basePackages))
                .toSet();
        if (classes.size() <= 0) {
            log.warn("There is no available service provider.");
        }
        ExtensionFactory extension = ExtensionLoad.getExtensionLoad(ExtensionFactory.class).getDefaultExtension();
        Registry registry = extension.getExtension(Registry.class);
        IrpcConfig config = extension.getExtension(IrpcConfig.class);
        // 启动时间
        long startTime = System.currentTimeMillis();
        URL serverUrl = new URL(config.getAddress(), config.getPort());
        for (Class<?> c : classes) {
            URL url = new URL(config.getAddress(), config.getPort(), c.getName());
            url.putParameter("timestamp" , startTime + "");
            try {
                registry.register(url);
                serverUrl.getUrls().put(url.getService(), url);
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
        Server server = extension.getExtension(Server.class);
        server.open(serverUrl);
    }
}
