package com.ifcc.irpc.common;

import lombok.Data;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Configuration;

/**
 * @author chenghaifeng
 * @date 2020-06-05
 * @description 配置类
 */
@Configuration
@Data
public class Config {
    // ============== 服务端配置 ===============
    /**
     * 服务通信端口
     * 默认20080
     */
    @Value("${irpc.server.port:20080}")
    private String port;

    /**
     * 可提供的最大连接数
     * 默认100
     */
    @Value("${irpc.server.accepts:100}")
    private int accepts;

    // ============== 客户端配置 ===============
    /**
     * 远程调用重试次数
     */
    @Value("${irpc.client.retries:2}")
    private int retries;

    /**
     * 远程调用超时时间，单位毫秒
     * 默认2000
     */
    @Value("${irpc.client.timeout:2000}")
    private int timeout;
}
