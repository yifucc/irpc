package test.com.ifcc.irpc.discovery;

import com.google.common.collect.BiMap;
import com.google.common.collect.Lists;
import com.ifcc.irpc.common.config.IConfigLoader;
import com.ifcc.irpc.common.config.IConfigProvider;
import com.ifcc.irpc.discovery.Discovery;
import com.ifcc.irpc.discovery.DiscoveryContext;
import com.ifcc.irpc.spi.ExtensionLoad;
import com.ifcc.irpc.spi.factory.ExtensionFactory;
import com.ifcc.irpc.utils.ClassUtil;
import com.ifcc.irpc.utils.LocalIpUtil;
import lombok.SneakyThrows;
import org.junit.jupiter.api.Test;

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

    @SneakyThrows
    @Test
    void test2() {
        Set<Class<?>> classes = ClassUtil.getAllSubClass(IConfigLoader.class, "");
        Set<Class<?>> set = ClassUtil.getAllClassByPackages(Lists.newArrayList(""));
//        Set<Class<?>> classes2 = ClassUtil.getAllSubClass(IConfigLoader.class, "com");
//        ArrayList<Class<?>> list = new ArrayList<>();
        //ClassUtil.findClassJar("com.ifcc", IConfigProvider.class, list);
        System.out.println(classes);
        System.out.println(set);

//        System.out.println(list);
//        System.out.println(IConfigLoader.class.isAssignableFrom(AbstractConfigLoader.class));
    }
}
