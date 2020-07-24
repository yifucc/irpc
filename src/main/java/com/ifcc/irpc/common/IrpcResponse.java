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
public class IrpcResponse implements Serializable {
    private String requestId;
    private int code;
    private String message;
    private Map<String, String> metaData;
    private Object payload;
}
