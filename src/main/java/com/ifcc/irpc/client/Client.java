package com.ifcc.irpc.client;

import com.ifcc.irpc.common.AsyncResponse;
import com.ifcc.irpc.common.Invocation;
import com.ifcc.irpc.common.URL;
import com.ifcc.irpc.spi.annotation.SPI;

/**
 * @author chenghaifeng
 * @date 2020-07-09
 * @description
 */
@SPI(value = "netty", singleton = false)
public interface Client {
    void connect(URL url);
    AsyncResponse send(Invocation invocation);
}
