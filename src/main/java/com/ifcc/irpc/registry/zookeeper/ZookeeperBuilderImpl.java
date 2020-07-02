package com.ifcc.irpc.registry.zookeeper;

import com.ifcc.irpc.common.Const;
import com.ifcc.irpc.exceptions.ZookeeperConnectFailedException;
import com.ifcc.irpc.spi.annotation.Config;
import lombok.extern.slf4j.Slf4j;
import org.apache.zookeeper.WatchedEvent;
import org.apache.zookeeper.Watcher;
import org.apache.zookeeper.ZooKeeper;

import java.util.concurrent.CountDownLatch;

/**
 * @author chenghaifeng
 * @date 2020-06-04
 * @description
 */
@Slf4j
public class ZookeeperBuilderImpl implements ZookeeperBuilder{

    @Config("${irpc.registryAddress}")
    private String registryAddress;

    private ZooKeeper zk;

    private CountDownLatch latch = new CountDownLatch(1);

    public ZookeeperBuilderImpl(String registryAddress) {
        this.registryAddress = registryAddress;
    }

    public ZookeeperBuilderImpl() {}

    @Override
    public ZooKeeper zkCli() throws ZookeeperConnectFailedException {
        if (this.zk != null && this.zk.getState().isConnected()) {
            return this.zk;
        }
        return this.connectZooKeeper();
    }

    private ZooKeeper connectZooKeeper() throws ZookeeperConnectFailedException {
        try{
            this.zk = new ZooKeeper(registryAddress, Const.ZK_SESSION_TIMEOUT,

                    (WatchedEvent event) -> {
                        if (event.getState() == Watcher.Event.KeeperState.SyncConnected) {
                            latch.countDown();
                        }
                    }

            );

            latch.await();
        } catch (Exception e) {
            log.error("[ZookeeperRegistry] Connect to zookeeper failed.", e);
            throw new ZookeeperConnectFailedException("[ZookeeperRegistry] Connect to zookeeper failed.", e);
        }
        return this.zk;
    }
}
