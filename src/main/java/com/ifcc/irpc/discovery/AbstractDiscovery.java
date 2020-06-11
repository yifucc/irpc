package com.ifcc.irpc.discovery;

import java.util.List;
import java.util.concurrent.CopyOnWriteArrayList;

/**
 * @author chenghaifeng
 * @date 2020-06-05
 * @description 服务发现器抽象类
 */
public abstract class AbstractDiscovery implements Discovery{

    private volatile List<String> serverAddressList;

    public AbstractDiscovery(List<String> serverAddressList) {
        this.serverAddressList = serverAddressList;
    }

    public AbstractDiscovery() {
        this.serverAddressList = new CopyOnWriteArrayList<>();
    }

    @Override
    public List<String> serverAddress() {
        return this.serverAddressList;
    }
}
