package com.ifcc.irpc.common;

import com.google.common.collect.Maps;
import lombok.Data;
import org.apache.commons.lang3.StringUtils;

import java.io.Serializable;
import java.util.Map;
import java.util.concurrent.atomic.AtomicBoolean;

/**
 * @author chenghaifeng
 * @date 2020-07-14
 * @description
 */
@Data
public class URL implements Serializable {

    private String host;

    private int port;

    private String service;

    private Map<String, String> parameters;

    private volatile transient Map<String, URL> urls;

    private volatile transient AtomicBoolean hasWatched;

    public URL(String host, int port, String service, Map<String, String> parameters) {
        this.host = host;
        this.port = port;
        this.service = service;
        this.parameters = parameters;
        this.urls = Maps.newConcurrentMap();
        this.hasWatched = new AtomicBoolean(false);
    }

    public URL(String host, int port, String service) {
        this(host, port, service, Maps.newHashMap());
    }

    public URL(String host, String service) {
        this(host, -1, service, Maps.newHashMap());
    }

    public URL(String host, int port) {
        this(host, port, null, Maps.newHashMap());
    }

    public URL() {}

    public void putParameter(String key, String value) {
        if (parameters == null) {
            parameters = Maps.newHashMap();
        }
        parameters.put(key, value);
    }

    public String getParameter(String key, String defaultValue) {
        if (parameters == null) {
            parameters = Maps.newHashMap();
        }
        return parameters.getOrDefault(key, defaultValue);
    }

    public String getParameter(String key) {
        if (parameters == null) {
            parameters = Maps.newHashMap();
        }
        return parameters.get(key);
    }

    public boolean hasParameter(String key) {
        String value = getParameter(key);
        return StringUtils.isNotBlank(value);
    }
}
