package com.ifcc.irpc;

import com.ifcc.irpc.registry.RegistryContext;
import com.ifcc.irpc.registry.etcd.EtcdBuilder;
import com.ifcc.irpc.registry.etcd.EtcdRegistry;

/**
 * @author chenghaifeng
 * @date 2020-06-10
 * @description
 */
public class EtcdMain {

    public static void main(String[] args) {
        EtcdBuilder builder = new EtcdBuilder("http://106.13.230.240:2379");
        EtcdRegistry registry = new EtcdRegistry(builder);
        try {
            registry.register(new RegistryContext("com.ifcc.test", "10.23.4.23:2323"));
            registry.register(new RegistryContext("com.ifcc.test", "10.23.4.24:2323"));
            Thread.sleep(100000);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}
