package com.ifcc.irpc.spi;

import com.google.common.collect.Maps;
import com.ifcc.irpc.codec.serialization.Serialization;
import com.ifcc.irpc.common.config.IrpcConfig;
import com.ifcc.irpc.discovery.Discovery;
import com.ifcc.irpc.registry.Registry;
import com.ifcc.irpc.spi.annotation.Inject;
import lombok.Data;

import java.util.Map;

/**
 * @author chenghaifeng
 * @date 2020-06-29
 * @description
 */
@Data
public class SpiContext {
    @Inject
    private IrpcConfig config;

    private Map<String, String> customConfig;

    public void init() {
        try {
            // 初始化spiContext
            customConfig = Maps.newHashMap();
            customConfig.put(Registry.class.getName(), config.getRegistry());
            customConfig.put(Discovery.class.getName(), config.getRegistry());
            customConfig.put(Serialization.class.getName(), config.getSerialization());
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}
