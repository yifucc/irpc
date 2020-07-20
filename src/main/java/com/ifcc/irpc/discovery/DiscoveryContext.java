package com.ifcc.irpc.discovery;

import lombok.Data;

import java.util.List;
import java.util.concurrent.CopyOnWriteArrayList;

/**
 * @author chenghaifeng
 * @date 2020-06-05
 * @description
 */
@Data
@Deprecated
public class DiscoveryContext {
    private String service;
    private String ip;
    private Boolean hasWatched;
    private volatile List<String> serverAddressList;

    public DiscoveryContext(String service, String ip) {
        this.service = service;
        this.ip = ip;
        this.hasWatched = false;
        this.serverAddressList = new CopyOnWriteArrayList<>();
    }
}
