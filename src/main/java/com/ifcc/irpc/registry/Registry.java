package com.ifcc.irpc.registry;

import com.ifcc.irpc.exceptions.RegistryServiceFailedException;
import com.ifcc.irpc.spi.annotation.SPI;

/**
 * @author chenghaifeng
 * @date 2020-06-04
 * @description 注册接口
 * 所以注册实现类必须实现的接口
 * 可用于扩展各类注册中心
 */
@SPI("zookeeper")
public interface Registry {

    void register(RegistryContext ctx) throws RegistryServiceFailedException;
}
