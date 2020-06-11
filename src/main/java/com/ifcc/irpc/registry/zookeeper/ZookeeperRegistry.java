package com.ifcc.irpc.registry.zookeeper;

import com.ifcc.irpc.common.Const;
import com.ifcc.irpc.exceptions.RegistryServiceFailedException;
import com.ifcc.irpc.registry.Registry;
import com.ifcc.irpc.registry.RegistryContext;
import lombok.extern.slf4j.Slf4j;
import org.apache.zookeeper.AsyncCallback;
import org.apache.zookeeper.CreateMode;
import org.apache.zookeeper.KeeperException;
import org.apache.zookeeper.WatchedEvent;
import org.apache.zookeeper.ZooDefs;
import org.apache.zookeeper.ZooKeeper;
import org.apache.zookeeper.data.Stat;

/**
 * @author chenghaifeng
 * @date 2020-06-04
 * @description zk实现的注册器
 */
@Slf4j
public class ZookeeperRegistry implements Registry, AsyncCallback.StringCallback {

    private ZookeeperBuilder zookeeperBuilder;

    public ZookeeperRegistry(ZookeeperBuilder zookeeperBuilder) {
        this.zookeeperBuilder = zookeeperBuilder;
    }

    @Override
    public void register(RegistryContext ctx) throws RegistryServiceFailedException {
        try {
            ZooKeeper zk = zookeeperBuilder.zkCli();
            if (zk.exists(Const.ZK_REGISTRY_PATH, null) == null) {
                zk.create(Const.ZK_REGISTRY_PATH, null, ZooDefs.Ids.OPEN_ACL_UNSAFE, CreateMode.PERSISTENT, this, ctx);
            }
            if (zk.exists(Const.ZK_REGISTRY_PATH + Const.DIAGONAL + ctx.getService(), null) == null) {
                zk.create(Const.ZK_REGISTRY_PATH + Const.DIAGONAL + ctx.getService(), null, ZooDefs.Ids.OPEN_ACL_UNSAFE, CreateMode.PERSISTENT, this, ctx);
            }
            if (zk.exists(Const.ZK_REGISTRY_PATH + Const.DIAGONAL + ctx.getService() + Const.ZK_PROVIDERS_PATH, null) == null) {
                zk.create(Const.ZK_REGISTRY_PATH + Const.DIAGONAL + ctx.getService() + Const.ZK_PROVIDERS_PATH, null, ZooDefs.Ids.OPEN_ACL_UNSAFE, CreateMode.PERSISTENT, this, ctx);
            }
            zk.create(Const.ZK_REGISTRY_PATH + Const.DIAGONAL + ctx.getService() + Const.ZK_PROVIDERS_PATH + Const.DIAGONAL + ctx.getUrl(), null, ZooDefs.Ids.OPEN_ACL_UNSAFE, CreateMode.EPHEMERAL, this, ctx);
            if (!ctx.getHasWatched()) {
                watchNode(ctx);
            }
        } catch (Exception e) {
            throw new RegistryServiceFailedException("[ZookeeperRegistry] Zookeeper register service failed.", e);
        }
    }

    private void watchNode(RegistryContext ctx) throws RegistryServiceFailedException {
        try {
            ZooKeeper zk = zookeeperBuilder.zkCli();
            zk.exists(Const.ZK_REGISTRY_PATH + Const.DIAGONAL + ctx.getService() + Const.ZK_PROVIDERS_PATH + Const.DIAGONAL + ctx.getUrl(),
                    (WatchedEvent event) -> {
                        try {
                            watchNode(ctx);
                        } catch (Exception e) {
                            e.printStackTrace();
                        }
                    }, (int rc, String path, Object o, Stat stat) -> {
                        switch (KeeperException.Code.get(rc)) {
                            case OK:
                                if (stat == null) {
                                    try {
                                        register(ctx);
                                    } catch (Exception e) {
                                        e.printStackTrace();
                                    }
                                }
                                break;
                            default:
                                try {
                                    register(ctx);
                                } catch (Exception e) {
                                    e.printStackTrace();
                                }
                        }
                    }, ctx);
            ctx.setHasWatched(true);
        } catch (Exception e) {
            throw new RegistryServiceFailedException("[ZookeeperRegistry] Zookeeper register service failed.", e);
        }
    }

    @Override
    public void processResult(int rc, String path, Object ctx, String name) {
        RegistryContext context = (RegistryContext) ctx;
        switch (KeeperException.Code.get(rc)) {
            case OK:
                log.info("[ZookeeperRegistry] service: {} registered to zookeeper successfully.", context.getService());
                break;
            case NODEEXISTS:
                break;
            case OPERATIONTIMEOUT:
            case CONNECTIONLOSS:
            default:
                log.error("[ZookeeperRegistry] Zookeeper register service failed, error code: {}, msg: {}", rc, KeeperException.Code.get(rc).toString());
                try {
                    Thread.sleep(10000);
                    this.register(context);
                } catch (Exception e) {
                    log.error("[ZookeeperRegistry] Zookeeper register service failed.", e);
                }
                break;
            /*default:
                log.error("[ZookeeperRegistry] Zookeeper register service failed, error code: {}, msg: {}", rc, KeeperException.Code.get(rc).toString());
                break;*/
        }
    }
}
