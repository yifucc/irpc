package test.com.ifcc.irpc.discovery;

import com.google.common.collect.BiMap;
import com.google.common.collect.Lists;
import com.ifcc.irpc.common.URL;
import com.ifcc.irpc.common.config.IConfigLoader;
import com.ifcc.irpc.common.config.IConfigProvider;
import com.ifcc.irpc.discovery.Discovery;
import com.ifcc.irpc.spi.ExtensionLoad;
import com.ifcc.irpc.spi.factory.ExtensionFactory;
import com.ifcc.irpc.utils.ClassUtil;
import com.ifcc.irpc.utils.LocalIpUtil;
import lombok.SneakyThrows;
import org.junit.jupiter.api.Test;

import java.io.File;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Set;

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
        URL ctx = new URL(LocalIpUtil.localRealIp(), "ifcc.service.test");
        URL ctx2 = new URL(LocalIpUtil.localRealIp(), "ifcc.irpc.test");
        try {
            discovery.discover(ctx);
            //discovery.discover(ctx2);
            new Runnable() {

                @SneakyThrows
                @Override
                public void run() {
                    int time = 0;
                    while (true) {
                        if (time > 10000) {
                            break;
                        }
                        System.out.println(ctx.getUrls());
                        System.out.println(ctx2.getUrls());
                        time++;
                        Thread.sleep(5000);
                    }
                }
            }.run();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    @SneakyThrows
    @Test
    void test2() {
//        Set<Class<?>> classes = ClassUtil.getAllSubClass(IConfigLoader.class, "");
        Set<Class<?>> set = ClassUtil.getAllClassByPackages(Lists.newArrayList("com"));
//        Set<Class<?>> classes2 = ClassUtil.getAllSubClass(IConfigLoader.class, "com");
//        ArrayList<Class<?>> list = new ArrayList<>();
        //ClassUtil.findClassJar("com.ifcc", IConfigProvider.class, list);
//        System.out.println(classes);
        System.out.println(set);

//        System.out.println(list);
//        System.out.println(IConfigLoader.class.isAssignableFrom(AbstractConfigLoader.class));
    }

    @Test
    void test3() {
        ExtensionFactory factory = ExtensionLoad.getExtensionLoad(ExtensionFactory.class).getDefaultExtension();
        IComsumer consumer = factory.getExtension(IComsumer.class);
        System.out.println(consumer);
        System.out.println(consumer.getClass());
    }

    @Test
    void test4() {
        File file = new File(new File(System.getProperty("user.home")), "irpc.properties");
        System.out.println(file.exists());
        System.out.println(System.getProperty("user.home"));
        String path = this.getClass().getClassLoader().getResource("irpc.properties").getPath();
        System.out.println(path);
    }
}
