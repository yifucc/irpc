package com.ifcc.irpc.discovery;

import lombok.Data;

/**
 * @author chenghaifeng
 * @date 2020-06-05
 * @description
 */
@Data
public class DiscoveryContext {
    private String service;
    private String ip;

    public DiscoveryContext(String service, String ip) {
        this.service = service;
        this.ip = ip;
    }
}
