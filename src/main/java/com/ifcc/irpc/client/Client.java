package com.ifcc.irpc.client;

import com.ifcc.irpc.common.Invocation;
import com.ifcc.irpc.common.Result;
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
    Result send(Invocation invocation);
}
