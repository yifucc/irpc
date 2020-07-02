package com.ifcc.irpc.common.config;

import java.util.Properties;

/**
 * @author chenghaifeng
 * @date 2020-06-30
 * @description
 */
public class ConfigLoader extends AbstractConfigLoader<IrpcConfig, Properties> {

    public ConfigLoader(IConfigProvider<Properties> provider) {
        super(provider);
    }

    @Override
    protected IrpcConfig load(Properties props) {
        IrpcConfig config = new IrpcConfig();
        config.setPort(Integer.parseInt(props.getProperty("irpc.server.port", "20080")));
        config.setAccepts(Integer.parseInt(props.getProperty("irpc.server.accepts", "100")));
        config.setRetries(Integer.parseInt(props.getProperty("irpc.client.retries", "2")));
        config.setTimeout(Integer.parseInt(props.getProperty("irpc.client.timeout", "2000")));
        config.setRegistry(props.getProperty("irpc.registry", "zookeeper"));
        config.setRegistryAddress(props.getProperty("irpc.registryAddress", ""));
        config.setSerialization(props.getProperty("irpc.client.serialization", "protocol"));
        return config;
    }
}
