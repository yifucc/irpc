package com.ifcc.irpc.common;

import java.util.Map;

/**
 * @author chenghaifeng
 * @date 2020-07-28
 * @description
 */
public interface Invocation {

    String getRequestId();

    void setRequestId(String requestId);

    String getTargetServiceName();

    String getServiceName();

    String getMethodName();

    Class<?>[] getParameterTypes();

    String[] getParameterSignatures();

    Object[] getArguments();

    String getAttachment(String key);

    String getAttachment(String key, String defaultValue);

    Map<String, String> getAttachments();

    void setAttachment(String key, String value);

    void setAttachmentIfAbsent(String key, String value);
}
