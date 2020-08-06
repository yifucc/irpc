package com.ifcc.irpc.exceptions;

/**
 * @author chenghaifeng
 * @date 2020-08-06
 * @description
 */
public class IrpcServiceNotFoundException extends Exception {
    public IrpcServiceNotFoundException() {
        super();
    }
    public IrpcServiceNotFoundException(String message) {
        super(message);
    }

    public IrpcServiceNotFoundException(String message, Throwable cause) {
        super(message, cause);
    }
}
