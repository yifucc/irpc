package com.ifcc.irpc.common;

import java.util.Map;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.TimeoutException;
import java.util.function.Function;

/**
 * @author chenghaifeng
 * @date 2020-07-28
 * @description
 */
public interface Result {

    String getRequestId();

    void setRequestId(String requestId);

    Object getValue();

    void setValue(Object value);

    Throwable getException();

    void setException(Throwable t);

    boolean hasException();

    Map<String, String> getAttachments();

    void setAttachments(Map<String, String> map);

    String getAttachment(String key);

    String getAttachment(String key, String defaultValue);

    void setAttachment(String key, String value);

    Result get() throws ExecutionException, InterruptedException;

    Result get(long timeout, TimeUnit unit) throws ExecutionException, InterruptedException, TimeoutException;

    <T> CompletableFuture<T> thenApply(Function<Result, ? extends T> function);
}
