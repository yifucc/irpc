package com.ifcc.irpc.common;

/**
 * @author chenghaifeng
 * @date 2020-06-04
 * @description 常量
 */
public interface Const {
    // ================= zk相关常量 ================
    /**
     * 连接zk超时时间
     */
    int ZK_SESSION_TIMEOUT = 5000;
    /**
     * 注册根节点
     */
    String ZK_REGISTRY_PATH = "/irpc";
    /**
     * 生产者节点
     */
    String ZK_PROVIDERS_PATH = "/providers";
    /**
     * 消费者节点
     */
    String ZK_CONSUMERS_PATH = "/consumers";

    // ================== 符号相关 ==================
    /**
     * 斜杆
     */
    String DIAGONAL = "/";
    /**
     * 冒号
     */
    String COLON = ":";

    byte IRPC_VERSION = 1;

    byte IRPC_MAGIC = 11;

    String SERIALIZATION = "serialization";

    String TIMESTAMP = "timestamp";
}
