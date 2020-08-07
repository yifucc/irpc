package com.ifcc.irpc.common.config;

import com.ifcc.irpc.spi.annotation.Cell;
import com.ifcc.irpc.spi.annotation.Config;
import com.ifcc.irpc.utils.LocalIpUtil;
import lombok.Data;
import org.apache.commons.lang3.StringUtils;

import java.util.Set;

/**
 * @author chenghaifeng
 * @date 2020-06-05
 * @description 配置类
 */
@Data
@Cell
public class IrpcConfig {
    // ============== 服务端配置 ===============
    /**
     * 服务通信端口
     * 默认20080
     */
    @Config("${irpc.server.port:20080}")
    private int port;

    /**
     * 可提供的最大连接数
     * 默认100
     */
    @Config("${irpc.server.accepts:100}")
    private int accepts;

    /**
     * 服务端地址
     */
    @Config(value = "${irpc.server.address}", required = false)
    private String address;

    @Config("${irpc.server.serialization:protocol}")
    private String serialization;

    // ============== 客户端配置 ===============
    /**
     * 远程调用重试次数
     */
    @Config("${irpc.client.retries:2}")
    private int retries;

    /**
     * 远程调用超时时间，单位毫秒
     * 默认2000
     */
    @Config("${irpc.client.timeout:2000}")
    private int timeout;

    /**
     * 客户端缓存，单位毫秒
     * 默认3000
     * -1表示不开启
     */
    @Config("${irpc.client.cache:3000}")
    private long cacheTime;

    // ============== 公共配置 ================

    /**
     * 注册中心 zookeeper etcd
     */
    @Config("${irpc.registry:zookeeper}")
    private String registry;

    /**
     * 注册中心地址
     */
    @Config("${irpc.registryAddress}")
    private String registryAddress;

    /**
     * 扫描包
     */
    @Config(value = "${irpc.scanBasePackages}", required = false)
    private Set<String> scanBasePackages;

    public void init() {
        if (StringUtils.isBlank(address)) {
            address = LocalIpUtil.localRealIp();
        }
    }
}
