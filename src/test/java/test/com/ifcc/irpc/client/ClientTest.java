package test.com.ifcc.irpc.client;

import com.ifcc.irpc.client.Client;
import com.ifcc.irpc.common.IrpcRequest;
import com.ifcc.irpc.common.URL;
import com.ifcc.irpc.spi.ExtensionLoad;
import com.ifcc.irpc.spi.factory.ExtensionFactory;
import org.junit.jupiter.api.Test;

/**
 * @author chenghaifeng
 * @date 2020-07-24
 * @description
 */
public class ClientTest {

    @Test
    void test1() throws InterruptedException {
        ExtensionFactory factory = ExtensionLoad.getExtensionLoad(ExtensionFactory.class).getDefaultExtension();
        Client client = factory.getExtension(Client.class);
        URL url = new URL("127.0.0.1", 20080, "/test");
        client.connect(url);
//        Thread.sleep(5000);
        IrpcRequest request = new IrpcRequest();
        request.setService("/test");
        request.setMethod("getUsers");
        client.send(request);
        Thread.sleep(1000000);
    }
}
