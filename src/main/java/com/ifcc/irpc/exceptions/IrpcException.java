package com.ifcc.irpc.exceptions;

/**
 * @author chenghaifeng
 * @date 2020-08-06
 * @description
 */
public class IrpcException extends Exception {
    public IrpcException() {
        super();
    }
    public IrpcException(String message) {
        super(message);
    }

    public IrpcException(String message, Throwable cause) {
        super(message, cause);
    }
}
