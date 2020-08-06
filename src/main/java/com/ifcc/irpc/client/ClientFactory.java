package com.ifcc.irpc.client;

import com.ifcc.irpc.common.URL;
import com.ifcc.irpc.spi.ExtensionLoad;
import com.ifcc.irpc.spi.annotation.Cell;
import com.ifcc.irpc.spi.factory.ExtensionFactory;

import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

/**
 * @author chenghaifeng
 * @date 2020-08-06
 * @description
 */
@Cell("clientFactory")
public class ClientFactory {
    private ExtensionFactory factory;

    private Map<String, Client> clientMap;

    public ClientFactory() {
        this.clientMap = new ConcurrentHashMap<>();
        this.factory = ExtensionLoad.getExtensionLoad(ExtensionFactory.class).getDefaultExtension();
    }

    public Client getClient(URL url) {
        return clientMap.computeIfAbsent(url.getHost(), key -> {
            Client client = factory.getExtension(Client.class);
            client.connect(url);
            return client;
        });
    }

    public void removeClient(URL url) {
        clientMap.remove(url.getHost());
    }
}
