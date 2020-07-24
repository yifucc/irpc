package com.ifcc.irpc.common;

import lombok.Data;

import java.io.Serializable;
import java.util.Map;

/**
 * @author chenghaifeng
 * @date 2020-07-22
 * @description
 */
@Data
public class IrpcRequest implements Serializable {
    private String requestId;
    private String service;
    private String method;
    private Map<String, String> metaData;
    private Object payload;
}
