package com.ifcc.irpc.boot;

import com.google.common.collect.Sets;
import com.ifcc.irpc.annotation.server.IrpcProvider;
import com.ifcc.irpc.annotation.server.IrpcServer;
import com.ifcc.irpc.common.ClassQueryBuilder;
import com.ifcc.irpc.common.Const;
import com.ifcc.irpc.common.URL;
import com.ifcc.irpc.common.config.IrpcConfig;
import com.ifcc.irpc.registry.Registry;
import com.ifcc.irpc.server.Server;
import com.ifcc.irpc.spi.ContainerLoad;
import com.ifcc.irpc.spi.ExtensionLoad;
import com.ifcc.irpc.spi.factory.ExtensionFactory;
import lombok.extern.slf4j.Slf4j;

import java.util.Set;

/**
 * @author chenghaifeng
 * @date 2020-07-06
 * @description
 */
@Slf4j
public final class IrpcApplication {
    public static void run(Class<?> clazz, String[] args) {
        IrpcBanner banner = new IrpcBanner();
        banner.printBanner();
        ExtensionFactory extension = ExtensionLoad.getExtensionLoad(ExtensionFactory.class).getDefaultExtension();
        IrpcServer irpcServer = clazz.getAnnotation(IrpcServer.class);
        String[] basePackages = irpcServer.scanBasePackages();
        Set<String> base = Sets.newHashSet(basePackages);
        IrpcConfig config = extension.getExtension(IrpcConfig.class);
        if (config.getScanBasePackages() != null) {
            base.addAll(config.getScanBasePackages());
        }
        ContainerLoad.addBasePackages(base);
        Set<Class<?>> classes = ClassQueryBuilder.build()
                .andAnnotationClass(IrpcProvider.class)
                .andBasePackages(base)
                .toSet();
        if (classes.size() <= 0) {
            log.warn("There is no available service provider.");
        }
        Registry registry = extension.getExtension(Registry.class);
        // 启动时间
        long startTime = System.currentTimeMillis();
        URL serverUrl = new URL(config.getAddress(), config.getPort());
        for (Class<?> c : classes) {
            URL url = new URL(config.getAddress(), config.getPort(), c.getName());
            url.putParameter(Const.TIMESTAMP , startTime + "");
            url.putParameter(Const.SERIALIZATION, config.getSerialization());
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
