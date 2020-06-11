package com.ifcc.irpc.discovery;

import com.ifcc.irpc.exceptions.DiscoveryServiceFailedException;

import java.util.List;

/**
 * @author chenghaifeng
 * @date 2020-06-04
 * @description 服务发现接口
 */
public interface Discovery {
    void discover(DiscoveryContext ctx) throws DiscoveryServiceFailedException;

    List<String> serverAddress();
}
