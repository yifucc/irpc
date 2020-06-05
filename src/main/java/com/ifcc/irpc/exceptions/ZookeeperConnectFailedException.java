package com.ifcc.irpc.exceptions;

/**
 * @author chenghaifeng
 * @date 2020-06-04
 * @description zk连接失败异常
 */
public class ZookeeperConnectFailedException extends Exception {
    public ZookeeperConnectFailedException() {
        super();
    }
    public ZookeeperConnectFailedException(String message) {
        super(message);
    }

    public ZookeeperConnectFailedException(String message, Throwable cause) {
        super(message, cause);
    }
}
