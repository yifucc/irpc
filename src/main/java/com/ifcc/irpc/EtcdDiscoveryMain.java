package com.ifcc.irpc;

import com.ifcc.irpc.discovery.Discovery;
import com.ifcc.irpc.discovery.DiscoveryContext;
import com.ifcc.irpc.discovery.etcd.EtcdDiscovery;
import com.ifcc.irpc.registry.etcd.EtcdBuilderImpl;
import com.ifcc.irpc.utils.LocalIpUtil;
import lombok.SneakyThrows;

/**
 * @author chenghaifeng
 * @date 2020-06-11
 * @description
 */
public class EtcdDiscoveryMain {
    public static void main(String[] args) {
        EtcdBuilderImpl builder = new EtcdBuilderImpl("http://106.13.230.240:2379");
        try {
            Discovery discovery = new EtcdDiscovery(builder);
            DiscoveryContext ctx = new DiscoveryContext("com.ifcc.test", LocalIpUtil.localRealIp());
            DiscoveryContext ctx2 = new DiscoveryContext("ifcc.service.test", LocalIpUtil.localRealIp());
            discovery.discover(ctx);
            discovery.discover(ctx2);
            new Runnable() {

                @SneakyThrows
                @Override
                public void run() {
                    int time = 0;
                    while (true) {
                        if (time > 10000) {
                            break;
                        }
                        System.out.println(ctx.getServerAddressList());
                        System.out.println(ctx2.getServerAddressList());
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
