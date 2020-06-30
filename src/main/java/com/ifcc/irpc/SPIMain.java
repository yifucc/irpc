package com.ifcc.irpc;

import com.ifcc.irpc.registry.Registry;
import com.ifcc.irpc.registry.RegistryContext;
import com.ifcc.irpc.spi.ExtensionFactory;

/**
 * @author chenghaifeng
 * @date 2020-06-12
 * @description
 */
public class SPIMain {

    public static void main(String[] args) {
        Registry registry = ExtensionFactory.getExtension(Registry.class);
        System.out.println(registry);
        try {
            RegistryContext ctx = new RegistryContext("ifcc.service.test", "10.101.23.3:9090");
            RegistryContext ctx2 = new RegistryContext("ifcc.service.test", "10.101.23.4:9090");
            registry.register(ctx);
            registry.register(ctx2);
            Thread.sleep(200000);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}
