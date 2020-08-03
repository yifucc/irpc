package com.ifcc.irpc.protocol;

import lombok.Data;

/**
 * @author chenghaifeng
 * @date 2020-07-01
 * @description 协议头
 */
@Data
public class FrameHeader {
    /**
     * 魔数
     * 611
     */
    private byte magic;
    /**
     * 版本号
     */
    private byte version;
    /**
     * 消息类型
     * 0 一般请求
     * 1 心跳
     */
    private byte msgType;
    /**
     * 请求类型
     * 0 发送并接受响应
     * 1 发送但不接受响应
     */
    private byte reqType;
    /**
     * 是否压缩
     * 0 未压缩
     * 1 压缩
     */
    private byte compressType;
    /**
     * 长度
     */
    private int length;
    /**
     * 保留字段
     */
    private int reserved;

}
