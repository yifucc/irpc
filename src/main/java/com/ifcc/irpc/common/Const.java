package com.ifcc.irpc.common;

/**
 * @author chenghaifeng
 * @date 2020-06-04
 * @description 常量
 */
public class Const {
    // ============== zk相关常量 =============
    /**
     * 连接zk超时时间
     */
    public static final int ZK_SESSION_TIMEOUT = 5000;
    /**
     * 注册根节点
     */
    public static final String ZK_REGISTRY_PATH = "/irpc";
    /**
     * 生产者节点
     */
    public static final String ZK_PROVIDERS_PATH = "/providers";
    /**
     * 消费者节点
     */
    public static final String ZK_CONSUMERS_PATH = "/consumers";

    public static final String DIAGONAL="/";
}
