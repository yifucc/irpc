package com.ifcc.irpc.server;

import com.ifcc.irpc.common.URL;
import com.ifcc.irpc.spi.annotation.SPI;

/**
 * @author chenghaifeng
 * @date 2020-07-09
 * @description
 */
@SPI("netty")
public interface Server {
    void open(URL url);
}
