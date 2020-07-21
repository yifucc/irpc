package test.com.ifcc.irpc.Extention;

import com.ifcc.irpc.common.Holder;
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
        Holder config1 = factory.getExtension(Holder.class);
        Holder config2 = factory.getExtension(Holder.class);
        System.out.println(config1.equals(config2));
    }
}
