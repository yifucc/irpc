package com.ifcc.irpc.discovery.zookeeper;

import com.ifcc.irpc.common.Const;
import com.ifcc.irpc.discovery.Discovery;
import com.ifcc.irpc.discovery.DiscoveryContext;
import com.ifcc.irpc.exceptions.DiscoveryServiceFailedException;
import com.ifcc.irpc.registry.zookeeper.ZookeeperBuilder;
import com.ifcc.irpc.spi.annotation.Inject;
import lombok.extern.slf4j.Slf4j;
import org.apache.zookeeper.AsyncCallback;
import org.apache.zookeeper.CreateMode;
import org.apache.zookeeper.KeeperException;
import org.apache.zookeeper.WatchedEvent;
import org.apache.zookeeper.ZooDefs;
import org.apache.zookeeper.ZooKeeper;

import java.util.List;

/**
 * @author chenghaifeng
 * @date 2020-06-05
 * @description zk实现的服务发现器
 */
@Slf4j
public class ZookeeperDiscovery implements Discovery, AsyncCallback.ChildrenCallback {

    @Inject
    private ZookeeperBuilder zookeeperBuilder;

    public ZookeeperDiscovery(ZookeeperBuilder zookeeperBuilder) {
        this.zookeeperBuilder = zookeeperBuilder;
    }

    public ZookeeperDiscovery() {}

    @Override
    public void discover(DiscoveryContext ctx) throws DiscoveryServiceFailedException {
        try {
            ZooKeeper zk = zookeeperBuilder.zkCli();
            // 获取对应provider下所有的子节点
            zk.getChildren(Const.ZK_REGISTRY_PATH + Const.DIAGONAL + ctx.getService() + Const.ZK_PROVIDERS_PATH,
                    (WatchedEvent event) -> {
                        try {
                            discover(ctx);
                        } catch (Exception e) {
                            e.printStackTrace();
                        }
                    }, this, ctx);
        } catch (Exception e) {
            throw new DiscoveryServiceFailedException("[ZookeeperDiscovery] Zookeeper discovery service failed.", e);
        }

    }

    @Override
    public void processResult(int rc, String path, Object ctx, List<String> children) {
        DiscoveryContext context = (DiscoveryContext) ctx;
        switch (KeeperException.Code.get(rc)) {
            case OK:
                context.getServerAddressList().clear();
                if (children != null) {
                    context.getServerAddressList().addAll(children);
                }
                if (children == null || children.isEmpty()) {
                    log.error("[ZookeeperDiscovery] There is no available service provider.");
                }
                try {
                    ZooKeeper zk = zookeeperBuilder.zkCli();
                    if (zk.exists(Const.ZK_REGISTRY_PATH + Const.DIAGONAL + context.getService() + Const.ZK_CONSUMERS_PATH, null) == null) {
                        zk.create(Const.ZK_REGISTRY_PATH + Const.DIAGONAL + context.getService() + Const.ZK_CONSUMERS_PATH, null, ZooDefs.Ids.OPEN_ACL_UNSAFE, CreateMode.PERSISTENT);
                    }
                    if (zk.exists(Const.ZK_REGISTRY_PATH + Const.DIAGONAL + context.getService() + Const.ZK_CONSUMERS_PATH + Const.DIAGONAL + context.getIp(), null) == null) {
                        zk.create(Const.ZK_REGISTRY_PATH + Const.DIAGONAL + context.getService() + Const.ZK_CONSUMERS_PATH + Const.DIAGONAL + context.getIp(), null, ZooDefs.Ids.OPEN_ACL_UNSAFE, CreateMode.EPHEMERAL);
                    }
                } catch (Exception e) {
                    log.error("[ZookeeperDiscovery] Create zookeeper discovery node failed.", e);
                }
                return;
            case OPERATIONTIMEOUT:
            case CONNECTIONLOSS:
            case NONODE:
            default:
                log.error("[ZookeeperDiscovery] Zookeeper discovery service failed, error code: {}, msg: {}, path: {}", rc, KeeperException.Code.get(rc).toString(), path);
                try {
                    Thread.sleep(10000);
                    discover(context);
                } catch (Exception e) {
                    log.error("[ZookeeperDiscovery] Zookeeper discovery service failed.", e);
                }
        }
    }
}
