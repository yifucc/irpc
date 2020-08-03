package com.ifcc.irpc.common;

import lombok.Data;

import java.io.Serializable;
import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.TimeUnit;
import java.util.function.Function;

/**
 * @author chenghaifeng
 * @date 2020-07-22
 * @description
 */
@Data
public class IrpcResponse implements Result, Serializable {

    private String requestId;

    private Object result;

    private Throwable exception;

    private Map<String, String> attachments = new HashMap<>();

    public IrpcResponse() {
    }

    public IrpcResponse(Object result) {
        this.result = result;
    }

    public IrpcResponse(Throwable exception) {
        this.exception = exception;
    }

    @Override
    public void setRequestId(String requestId) {
        this.requestId = requestId;
    }

    @Override
    public String getRequestId() {
        return requestId;
    }

    @Override
    public Object getValue() {
        return result;
    }

    @Override
    public void setValue(Object value) {
        this.result = value;
    }

    @Override
    public Throwable getException() {
        return exception;
    }

    @Override
    public void setException(Throwable t) {
        this.exception = t;
    }

    @Override
    public boolean hasException() {
        return this.exception != null;
    }

    @Override
    public Map<String, String> getAttachments() {
        return attachments;
    }

    @Override
    public void setAttachments(Map<String, String> map) {
        this.attachments = map;
    }

    @Override
    public String getAttachment(String key) {
        return attachments.get(key);
    }

    @Override
    public String getAttachment(String key, String defaultValue) {
        return attachments.getOrDefault(key, defaultValue);
    }

    @Override
    public void setAttachment(String key, String value) {
        this.attachments.put(key, value);
    }

    @Override
    public Result get() {
        throw new UnsupportedOperationException("IrpcResponse represents an concrete business response.");
    }

    @Override
    public Result get(long timeout, TimeUnit unit) {
        throw new UnsupportedOperationException("IrpcResponse represents an concrete business response.");
    }

    @Override
    public <T> CompletableFuture<T> thenApply(Function<Result, ? extends T> function) {
        throw new UnsupportedOperationException("IrpcResponse represents an concrete business response.");
    }
}
