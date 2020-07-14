package test.com.ifcc.irpc.registry;

import com.ifcc.irpc.registry.Registry;
import com.ifcc.irpc.registry.RegistryContext;
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
            RegistryContext ctx = new RegistryContext("ifcc.service.test", "10.101.23.3:9090");
            RegistryContext ctx2 = new RegistryContext("ifcc.service.test", "10.101.23.4:9090");
            registry.register(ctx);
            registry.register(ctx2);
            latch.await();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}
