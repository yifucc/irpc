package com.ifcc.irpc.spi;

import lombok.Data;

import java.util.Map;

/**
 * @author chenghaifeng
 * @date 2020-06-29
 * @description
 */
@Data
public class SpiContext {
    private Map<String, String> customConfig;
}
