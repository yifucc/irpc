package com.ifcc.irpc.discovery;

import com.ifcc.irpc.common.config.AbstractConfigLoader;
import com.ifcc.irpc.common.config.IConfigLoader;
import com.ifcc.irpc.common.config.IConfigProvider;
import com.ifcc.irpc.spi.ExtensionLoad;
import com.ifcc.irpc.spi.factory.ExtensionFactory;
import com.ifcc.irpc.utils.ClassUtil;
import com.ifcc.irpc.utils.LocalIpUtil;
import lombok.SneakyThrows;
import org.junit.jupiter.api.Test;

import java.util.ArrayList;
import java.util.List;

/**
 * @author chenghaifeng
 * @date 2020-07-03
 * @description
 */
public class DiscoveryTest {

    @Test
    void test() {
        Discovery discovery = ExtensionLoad.getExtensionLoad(ExtensionFactory.class).getDefaultExtension().getExtension(Discovery.class);
        System.out.println(LocalIpUtil.localRealIp());
        DiscoveryContext ctx = new DiscoveryContext("ifcc.service.test", LocalIpUtil.localRealIp());
        DiscoveryContext ctx2 = new DiscoveryContext("ifcc.irpc.test", LocalIpUtil.localRealIp());
        try {
            discovery.discover(ctx);
            new Runnable() {

                @SneakyThrows
                @Override
                public void run() {
                    int time = 0;
                    while (true) {
                        if (time > 10000) {
                            break;
                        }
                        System.out.println(ctx.getServerAddressList());
                        System.out.println(ctx2.getServerAddressList());
                        time++;
                        Thread.sleep(5000);
                    }
                }
            }.run();
            System.out.println(ctx.getServerAddressList());
            Thread.sleep(100000);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    @Test
    void test2() {
        List<Class<?>> classes = ClassUtil.getInterfaceImpls(IConfigProvider.class);
        ArrayList<Class<?>> list = new ArrayList<>();
        //ClassUtil.findClassJar("com.ifcc", IConfigProvider.class, list);
        System.out.println(classes);
        System.out.println(list);
        System.out.println(IConfigLoader.class.isAssignableFrom(AbstractConfigLoader.class));
    }
}
