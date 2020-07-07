package com.ifcc.irpc;

import com.ifcc.irpc.annotation.server.IrpcProvider;
import com.ifcc.irpc.annotation.server.IrpcServer;
import com.ifcc.irpc.common.config.IrpcConfig;
import com.ifcc.irpc.registry.Registry;
import com.ifcc.irpc.registry.RegistryContext;
import com.ifcc.irpc.spi.ExtensionLoad;
import com.ifcc.irpc.spi.factory.ExtensionFactory;
import com.ifcc.irpc.utils.ClassUtil;
import com.ifcc.irpc.utils.LocalIpUtil;

import java.util.Arrays;
import java.util.List;
import java.util.Set;

/**
 * @author chenghaifeng
 * @date 2020-07-06
 * @description
 */
public class IrpcApplication {
    public static void run(Class<?> clazz, String[] args) {
        IrpcServer irpcServer = clazz.getAnnotation(IrpcServer.class);
        String[] basePackages = irpcServer.scanBasePackages();
        List<String> basePackageList = Arrays.asList(basePackages);
        Set<Class<?>> classes = ClassUtil.getAllClassByAnnotation(IrpcProvider.class, basePackageList);
        ExtensionFactory extension = ExtensionLoad.getExtensionLoad(ExtensionFactory.class).getDefaultExtension();
        Registry registry = extension.getExtension(Registry.class);
        IrpcConfig config = extension.getExtension(IrpcConfig.class);
        for (Class<?> c : classes) {
            RegistryContext ctx = new RegistryContext(c.getName(), LocalIpUtil.localRealIp() + ":" + config.getPort());
            try {
                registry.register(ctx);
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
    }
}
