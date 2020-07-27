package test.com.ifcc.irpc.Extention;

import com.ifcc.irpc.common.Holder;
import com.ifcc.irpc.common.config.IConfigProvider;
import com.ifcc.irpc.common.config.IrpcConfig;
import com.ifcc.irpc.spi.ExtensionLoad;
import com.ifcc.irpc.spi.factory.ExtensionFactory;
import org.junit.jupiter.api.Test;

/**
 * @author chenghaifeng
 * @date 2020-07-21
 * @description
 */
public class ExtentionTest {

    @Test
    void test1() {
        ExtensionFactory factory = ExtensionLoad.getExtensionLoad(ExtensionFactory.class).getDefaultExtension();
        IConfigProvider config1 = factory.getExtension(IConfigProvider.class);
        IConfigProvider config2 = factory.getExtension(IConfigProvider.class, "properties");
        System.out.println(config1.equals(config2));
    }

    @Test
    void test2() {
        ExtensionFactory factory = ExtensionLoad.getExtensionLoad(ExtensionFactory.class).getDefaultExtension();
        Test2 test2 = factory.getExtension(Test2.class);
        System.out.println(test2.list);
        System.out.println(test2.test3);
    }
}
