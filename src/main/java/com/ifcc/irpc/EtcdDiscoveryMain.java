package com.ifcc.irpc;

import com.ifcc.irpc.discovery.DiscoveryContext;
import com.ifcc.irpc.discovery.etcd.EtcdDiscovery;
import com.ifcc.irpc.registry.etcd.EtcdBuilder;
import com.ifcc.irpc.utils.LocalIpUtil;
import lombok.SneakyThrows;

/**
 * @author chenghaifeng
 * @date 2020-06-11
 * @description
 */
public class EtcdDiscoveryMain {
    public static void main(String[] args) {
        EtcdBuilder builder = new EtcdBuilder("http://106.13.230.240:2379");
        try {
            EtcdDiscovery discovery = new EtcdDiscovery(builder);
            discovery.discover(new DiscoveryContext("com.ifcc.test", LocalIpUtil.localRealIp()));
            new Runnable() {

                @SneakyThrows
                @Override
                public void run() {
                    int time = 0;
                    while (true) {
                        if (time > 10000) {
                            break;
                        }
                        System.out.println(discovery.serverAddress());
                        time++;
                        Thread.sleep(5000);
                    }
                }
            }.run();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}
