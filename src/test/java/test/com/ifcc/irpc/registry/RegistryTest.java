package test.com.ifcc.irpc.registry;

import com.ifcc.irpc.common.URL;
import com.ifcc.irpc.common.config.IrpcConfig;
import com.ifcc.irpc.registry.Registry;
import com.ifcc.irpc.spi.ExtensionLoad;
import com.ifcc.irpc.spi.factory.ExtensionFactory;
import org.junit.jupiter.api.Test;

import java.util.concurrent.CountDownLatch;

/**
 * @author chenghaifeng
 * @date 2020-07-13
 * @description
 */
public class RegistryTest {

    @Test
    void registryTest() {
        CountDownLatch latch = new CountDownLatch(1);
        Registry registry = ExtensionLoad.getExtensionLoad(ExtensionFactory.class).getDefaultExtension().getExtension(Registry.class);
        try {
            URL ctx = new URL("10.101.23.3", 9090, "ifcc.service.test");
            URL ctx2 = new URL("10.101.23.4",9090,"ifcc.irpc.test");
            ctx.putParameter("timestamp", System.currentTimeMillis() + "");
            ctx2.putParameter("timestamp", System.currentTimeMillis() + "");
            registry.register(ctx);
            registry.register(ctx2);
            latch.await();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    @Test
    void test1() {
        ExtensionFactory factory = ExtensionLoad.getExtensionLoad(ExtensionFactory.class).getDefaultExtension();
        IrpcConfig config = factory.getExtension(IrpcConfig.class);
        System.out.println(config);
    }

    @Test
    void test2() throws NoSuchMethodException {
        System.out.println(int.class.getConstructor(String.class));
    }
}
