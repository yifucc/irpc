package com.ifcc.irpc.exceptions;

/**
 * @author chenghaifeng
 * @date 2020-06-05
 * @description
 */
public class DiscoveryServiceFailedException extends Exception {
    public DiscoveryServiceFailedException() {
        super();
    }
    public DiscoveryServiceFailedException(String message) {
        super(message);
    }

    public DiscoveryServiceFailedException(String message, Throwable cause) {
        super(message, cause);
    }
}
