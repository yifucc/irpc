package com.ifcc.irpc;

import com.ifcc.irpc.common.URL;
import com.ifcc.irpc.registry.Registry;
import com.ifcc.irpc.spi.factory.ExtensionFactory;
import com.ifcc.irpc.spi.ExtensionLoad;
import lombok.SneakyThrows;

/**
 * @author chenghaifeng
 * @date 2020-06-12
 * @description
 */
public class SPIMain {

    @SneakyThrows
    public static void main(String[] args) {
        Registry registry = ExtensionLoad.getExtensionLoad(ExtensionFactory.class).getDefaultExtension().getExtension(Registry.class);
        try {
            URL ctx = new URL("10.101.23.3", 9090, "ifcc.service.test");
            URL ctx2 = new URL("10.101.23.4",9090,"ifcc.service.test");
            registry.register(ctx);
            registry.register(ctx2);
            System.out.println("hello, world");
            Thread.sleep(200000);
        } catch (Exception e) {
            e.printStackTrace();
        }
//        System.out.println(ExtensionLoad.getExtensionLoad(ExtensionFactory.class).getDefaultExtension());
//        System.out.println(ClassUtil.getAllSubClass(IConfigLoader.class));
    }
}
