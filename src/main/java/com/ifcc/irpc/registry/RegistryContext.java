package com.ifcc.irpc.registry;

import lombok.Data;

/**
 * @author chenghaifeng
 * @date 2020-06-04
 * @description
 */
@Data
public class RegistryContext {
    private String service;
    private String url;
    private Boolean hasWatched;
    public RegistryContext(String service, String url) {
        this.service = service;
        this.url = url;
        this.hasWatched = false;
    }
}
