package com.ifcc.irpc.discovery;

import com.ifcc.irpc.exceptions.DiscoveryServiceFailedException;
import com.ifcc.irpc.spi.annotation.SPI;

/**
 * @author chenghaifeng
 * @date 2020-06-04
 * @description 服务发现接口
 */
@SPI("zookeeper")
public interface Discovery {
    void discover(DiscoveryContext ctx) throws DiscoveryServiceFailedException;
}
