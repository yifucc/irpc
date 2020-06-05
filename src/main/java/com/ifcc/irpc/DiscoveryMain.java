package com.ifcc.irpc;

import com.ifcc.irpc.discovery.DiscoveryContext;
import com.ifcc.irpc.discovery.zookeeper.ZookeeperDiscovery;
import com.ifcc.irpc.exceptions.DiscoveryServiceFailedException;
import com.ifcc.irpc.registry.zookeeper.ZookeeperBuilder;
import com.ifcc.irpc.utils.LocalIpUtil;
import lombok.SneakyThrows;

/**
 * @author chenghaifeng
 * @date 2020-06-05
 * @description
 */
public class DiscoveryMain {

    public static void main(String[] args) throws DiscoveryServiceFailedException, InterruptedException {
        ZookeeperBuilder builder = new ZookeeperBuilder("106.13.230.240:2181");
        ZookeeperDiscovery discovery = new ZookeeperDiscovery(builder);
        System.out.println(LocalIpUtil.localRealIp());
        DiscoveryContext ctx = new DiscoveryContext("ifcc.service.test", LocalIpUtil.localRealIp());
//        try {
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
                        System.out.println(discovery.serverAddress());
                        time++;
                        Thread.sleep(5000);
                    }
                }
            }.run();
            System.out.println(discovery.serverAddress());
            Thread.sleep(100000);
//        } catch (Exception e) {
//            e.printStackTrace();
//        }
    }
}
