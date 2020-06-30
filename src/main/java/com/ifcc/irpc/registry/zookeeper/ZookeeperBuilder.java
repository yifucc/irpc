package com.ifcc.irpc.registry.zookeeper;

import com.ifcc.irpc.exceptions.ZookeeperConnectFailedException;
import com.ifcc.irpc.spi.annotation.SPI;
import org.apache.zookeeper.ZooKeeper;

/**
 * @author chenghaifeng
 * @date 2020-06-30
 * @description
 */
@SPI("zkBuilder")
public interface ZookeeperBuilder {
    ZooKeeper zkCli() throws ZookeeperConnectFailedException;
}
