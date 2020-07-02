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
     */
    private byte magic;
    /**
     * 版本号
     */
    private byte version;
    /**
     * 消息类型
     */
    private byte msgType;
    /**
     * 请求类型
     */
    private byte reqType;
    /**
     * 是否压缩
     */
    private byte compressType;
    /**
     * 流id
     */
    private short streamId;
    /**
     * 长度
     */
    private int length;
    /**
     * 保留字段
     */
    private int reserved;
}
