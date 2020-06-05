package com.ifcc.irpc.registry.zookeeper;

import com.ifcc.irpc.common.Const;
import com.ifcc.irpc.exceptions.RegistryServiceFailedException;
import com.ifcc.irpc.registry.Registry;
import com.ifcc.irpc.registry.RegistryContext;
import lombok.extern.slf4j.Slf4j;
import org.apache.zookeeper.AsyncCallback;
import org.apache.zookeeper.CreateMode;
import org.apache.zookeeper.KeeperException;
import org.apache.zookeeper.ZooDefs;
import org.apache.zookeeper.ZooKeeper;

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
        } catch (Exception e) {
            throw new RegistryServiceFailedException("[ZookeeperRegistry] Zookeeper register service failed.", e);
        }
    }

    @Override
    public void processResult(int rc, String path, Object ctx, String name) {
        switch (KeeperException.Code.get(rc)) {
            case OK:
                return;
            case OPERATIONTIMEOUT:
            case CONNECTIONLOSS:
                log.error("[ZookeeperRegistry] Zookeeper register service failed, error code: {}, msg: {}", rc, KeeperException.Code.get(rc).toString());
                RegistryContext context = (RegistryContext) ctx;
                try {
                    this.register(context);
                    Thread.sleep(100000);
                } catch (Exception e) {
                    log.error("[ZookeeperRegistry] Zookeeper register service failed.", e);
                }
                break;
            case NODEEXISTS:
            default:
                break;
        }
    }
}
