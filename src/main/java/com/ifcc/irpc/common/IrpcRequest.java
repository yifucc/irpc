package com.ifcc.irpc.common;

import java.io.Serializable;
import java.util.HashMap;
import java.util.Map;
import java.util.stream.Stream;

/**
 * @author chenghaifeng
 * @date 2020-07-22
 * @description
 */
public class IrpcRequest implements Invocation, Serializable {

    private String requestId;
    private String targetServiceName;
    private String serviceName;
    private String methodName;

    private transient Class<?>[] parameterTypes;
    private String[] parameterSignatures;
    private Object[] arguments;
    private Map<String, String> attachments;

    private transient Class<?> returnType;

    public IrpcRequest(String requestId, String serviceName, String methodName, Class<?>[] parameterTypes, Object[] arguments, Map<String, String> attachments) {
        this.requestId = requestId;
        this.serviceName = serviceName;
        this.methodName = methodName;
        this.parameterTypes = parameterTypes == null? new Class[0] : parameterTypes;
        this.arguments = arguments == null? new Object[0] : arguments;
        this.attachments = attachments == null? new HashMap<>() : attachments;
        initParameterSignatures();
    }

    public IrpcRequest(String serviceName, String methodName, Class<?>[] parameterTypes, Object[] arguments) {
        this(null, serviceName, methodName, parameterTypes, arguments);
    }

    public IrpcRequest() {}

    public IrpcRequest(String requestId, String serviceName, String methodName, Class<?>[] parameterTypes, Object[] arguments) {
        this(requestId, serviceName, methodName, parameterTypes, arguments, null);
    }

    private void initParameterSignatures() {
        this.parameterSignatures = Stream.of(parameterTypes).map(Class::getName).toArray(String[]::new);
//        this.returnType = this.
    }

    public void setTargetServiceName(String targetServiceName) {
        this.targetServiceName = targetServiceName;
    }

    public void setServiceName(String serviceName) {
        this.serviceName = serviceName;
    }

    public void setMethodName(String methodName) {
        this.methodName = methodName;
    }

    public void setParameterTypes(Class<?>[] parameterTypes) {
        this.parameterTypes = parameterTypes == null? new Class[0] : parameterTypes;
    }

    public void setParameterSignatures(String[] parameterSignatures) {
        this.parameterSignatures = parameterSignatures;
    }

    public void setArguments(Object[] arguments) {
        this.arguments = arguments;
    }

    public void setAttachments(Map<String, String> attachments) {
        this.attachments = attachments == null? new HashMap<>() : attachments;
    }

    public void setReturnType(Class<?> returnType) {
        this.returnType = returnType;
    }

    public void setRequestId(String requestId) {
        this.requestId = requestId;
    }

    public Class<?> getReturnType() {
        return returnType;
    }

    @Override
    public String getRequestId() {
        return requestId;
    }

    @Override
    public String getTargetServiceName() {
        return targetServiceName;
    }

    @Override
    public String getServiceName() {
        return serviceName;
    }

    @Override
    public String getMethodName() {
        return methodName;
    }

    @Override
    public Class<?>[] getParameterTypes() {
        return parameterTypes;
    }

    @Override
    public String[] getParameterSignatures() {
        return parameterSignatures;
    }

    @Override
    public Object[] getArguments() {
        return arguments;
    }

    @Override
    public String getAttachment(String key) {
        if(attachments == null) {
            return null;
        }
        return attachments.get(key);
    }

    @Override
    public String getAttachment(String key, String defaultValue) {
        if (attachments == null) {
            return defaultValue;
        }
        return attachments.getOrDefault(key, defaultValue);
    }

    @Override
    public Map<String, String> getAttachments() {
        return attachments;
    }

    @Override
    public void setAttachment(String key, String value) {
        if (attachments == null) {
            attachments = new HashMap<>();
        }
        attachments.put(key, value);
    }

    @Override
    public void setAttachmentIfAbsent(String key, String value) {
        if (attachments == null) {
            attachments = new HashMap<>();
        }
        if (!attachments.containsKey(key)) {
            attachments.put(key, value);
        }
    }
}
