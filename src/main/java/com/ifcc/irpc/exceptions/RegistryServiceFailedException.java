package com.ifcc.irpc.exceptions;

/**
 * @author chenghaifeng
 * @date 2020-06-04
 * @description
 */
public class RegistryServiceFailedException extends Exception {
    public RegistryServiceFailedException() {
        super();
    }
    public RegistryServiceFailedException(String message) {
        super(message);
    }

    public RegistryServiceFailedException(String message, Throwable cause) {
        super(message, cause);
    }
}
