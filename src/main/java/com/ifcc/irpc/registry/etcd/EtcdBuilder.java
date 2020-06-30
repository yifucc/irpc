package com.ifcc.irpc.registry.etcd;

import com.ifcc.irpc.spi.annotation.SPI;
import io.etcd.jetcd.Client;

/**
 * @author chenghaifeng
 * @date 2020-06-30
 * @description
 */
@SPI("etcdBuilder")
public interface EtcdBuilder {
    Client etcdCli();
    long leaseId();
}
