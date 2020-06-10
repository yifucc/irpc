package com.ifcc.irpc;

import com.ifcc.irpc.registry.RegistryContext;
import com.ifcc.irpc.registry.zookeeper.ZookeeperBuilder;
import com.ifcc.irpc.registry.zookeeper.ZookeeperRegistry;

/**
 * @author chenghaifeng
 * @date 2020-06-04
 * @description
 */
public class TestMain {
    public static void main(String[] ages) {
        ZookeeperBuilder builder = new ZookeeperBuilder("106.13.230.240:2181");
        ZookeeperRegistry registry = new ZookeeperRegistry(builder);
        try {
            RegistryContext ctx = new RegistryContext("ifcc.service.test", "10.101.23.3:9090");
            RegistryContext ctx2 = new RegistryContext("ifcc.service.test", "10.101.23.4:9090");
            registry.register(ctx);
            registry.register(ctx2);
            Thread.sleep(200000);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}
