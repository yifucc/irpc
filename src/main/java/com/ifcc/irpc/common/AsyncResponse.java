package com.ifcc.irpc.common;

import lombok.extern.slf4j.Slf4j;

import java.util.Map;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.TimeoutException;
import java.util.concurrent.atomic.AtomicBoolean;
import java.util.function.Function;

/**
 * @author chenghaifeng
 * @date 2020-08-03
 * @description
 */
@Slf4j
public class AsyncResponse implements Result {

    private CompletableFuture<IrpcResponse> responseFuture;

    private AtomicBoolean futureFlag = new AtomicBoolean(false);

    public AsyncResponse(CompletableFuture<IrpcResponse> responseFuture) {
        this.responseFuture = responseFuture;
    }

    public Result getIrpcResponse() {
        try {
            if (responseFuture.isDone()) {
                return responseFuture.get();
            }
        } catch (Exception e) {
            log.error("Got exception when trying to fetch the underlying result from AsyncResponse.");
            throw new IllegalStateException(e);
        }
        return createDefaultValue();
    }

    public AtomicBoolean getFutureFlag() {
        return futureFlag;
    }

    public static AsyncResponse newDefaultAsyncResponse() {
        CompletableFuture<IrpcResponse> future = new CompletableFuture<>();
        return new AsyncResponse(future);
    }

    public static AsyncResponse newDefaultAsyncResponse(IrpcResponse response) {
        return new AsyncResponse(CompletableFuture.completedFuture(response));
    }

    public static AsyncResponse newDefaultAsyncResponse(Object value) {
        return newDefaultAsyncResponse(value, null);
    }

    public static AsyncResponse newDefaultAsyncResponse(Throwable t) {
        return newDefaultAsyncResponse(null, t);
    }

    public static AsyncResponse newDefaultAsyncResponse(Object value, Throwable t) {
        CompletableFuture<IrpcResponse> future = new CompletableFuture<>();
        IrpcResponse response = new IrpcResponse();
        if (t != null) {
            response.setException(t);
        } else {
            response.setValue(value);
        }
        future.complete(response);
        return new AsyncResponse(future);
    }

    private static Result createDefaultValue() {
        return new IrpcResponse();
    }

    public CompletableFuture<IrpcResponse> getResponseFuture() {
        return responseFuture;
    }

    public void setResponseFuture(CompletableFuture<IrpcResponse> responseFuture) {
        this.responseFuture = responseFuture;
    }

    @Override
    public String getRequestId() {
        return getIrpcResponse().getRequestId();
    }

    @Override
    public void setRequestId(String requestId) {
        try {
            if (responseFuture.isDone()) {
                responseFuture.get().setRequestId(requestId);
            } else {
                IrpcResponse response = new IrpcResponse();
                response.setRequestId(requestId);
                responseFuture.complete(response);
            }
        } catch (Exception e) {
            log.error("Got exception when trying to fetch the underlying result from AsyncResponse.");
            throw new IllegalStateException(e);
        }
    }

    @Override
    public Object getValue() {
        return getIrpcResponse().getValue();
    }

    @Override
    public void setValue(Object value) {
        try {
            if (responseFuture.isDone()) {
                responseFuture.get().setValue(value);
            } else {
                IrpcResponse response = new IrpcResponse();
                response.setValue(value);
                responseFuture.complete(response);
            }
        } catch (Exception e) {
            log.error("Got exception when trying to fetch the underlying result from AsyncResponse.");
            throw new IllegalStateException(e);
        }
    }

    @Override
    public Throwable getException() {
        return getIrpcResponse().getException();
    }

    @Override
    public void setException(Throwable t) {
        try {
            if (responseFuture.isDone()) {
                responseFuture.get().setException(t);
            } else {
                IrpcResponse response = new IrpcResponse();
                response.setException(t);
                responseFuture.complete(response);
            }
        } catch (Exception e) {
            log.error("Got exception when trying to fetch the underlying result from AsyncResponse.");
            throw new IllegalStateException(e);
        }
    }

    @Override
    public boolean hasException() {
        return getIrpcResponse().hasException();
    }

    @Override
    public Map<String, String> getAttachments() {
        return getIrpcResponse().getAttachments();
    }

    @Override
    public void setAttachments(Map<String, String> map) {
        try {
            if (responseFuture.isDone()) {
                responseFuture.get().setAttachments(map);
            } else {
                IrpcResponse response = new IrpcResponse();
                response.setAttachments(map);
                responseFuture.complete(response);
            }
        } catch (Exception e) {
            log.error("Got exception when trying to fetch the underlying result from AsyncResponse.");
            throw new IllegalStateException(e);
        }
    }

    @Override
    public String getAttachment(String key) {
        return getIrpcResponse().getAttachment(key);
    }

    @Override
    public String getAttachment(String key, String defaultValue) {
        return getIrpcResponse().getAttachment(key, defaultValue);
    }

    @Override
    public void setAttachment(String key, String value) {
        try {
            if (responseFuture.isDone()) {
                responseFuture.get().setAttachment(key, value);
            } else {
                IrpcResponse response = new IrpcResponse();
                response.setAttachment(key, value);
                responseFuture.complete(response);
            }
        } catch (Exception e) {
            log.error("Got exception when trying to fetch the underlying result from AsyncResponse.");
            throw new IllegalStateException(e);
        }
    }

    @Override
    public Result get() throws ExecutionException, InterruptedException {
        return responseFuture.get();
    }

    @Override
    public Result get(long timeout, TimeUnit unit) throws InterruptedException, ExecutionException, TimeoutException {
        return responseFuture.get(timeout, unit);
    }

    @Override
    public <T> CompletableFuture<T> thenApply(Function<Result, ? extends T> function) {
        return responseFuture.thenApply(function);
    }
}
