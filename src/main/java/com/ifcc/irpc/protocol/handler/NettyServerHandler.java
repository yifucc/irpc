package com.ifcc.irpc.protocol.handler;

import com.ifcc.irpc.common.IrpcRequest;
import com.ifcc.irpc.common.IrpcResponse;
import com.ifcc.irpc.exceptions.IrpcException;
import com.ifcc.irpc.exceptions.IrpcServiceNotFoundException;
import com.ifcc.irpc.spi.ExtensionLoad;
import com.ifcc.irpc.spi.factory.ExtensionFactory;
import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.SimpleChannelInboundHandler;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;

import java.lang.reflect.Method;

/**
 * @author chenghaifeng
 * @date 2020-07-27
 * @description
 */
@Slf4j
public class NettyServerHandler extends SimpleChannelInboundHandler<IrpcRequest> {

    private ExtensionFactory factory = ExtensionLoad.getExtensionLoad(ExtensionFactory.class).getDefaultExtension();

    @Override
    protected void channelRead0(ChannelHandlerContext channelHandlerContext, IrpcRequest request) throws Exception {
        IrpcResponse response = new IrpcResponse();
        response.setRequestId(request.getRequestId());
        try {
            Object result = handle(request);
            response.setResult(result);
        } catch (Throwable t) {
            response.setException(t);
            t.printStackTrace();
        }
        channelHandlerContext.writeAndFlush(response);
    }

    private Object handle(IrpcRequest request) throws IrpcServiceNotFoundException, IrpcException {
        String targetServiceName = request.getTargetServiceName();
        String serviceName = request.getServiceName();
        String methodName = request.getMethodName();
        String[] parameterSignatures = request.getParameterSignatures();
        Object[] arguments = request.getArguments();
        Class<?> clazz = null;
        try {
            clazz = Class.forName(serviceName);
        } catch (ClassNotFoundException e) {
            throw new IrpcServiceNotFoundException("Target service not found: " + serviceName);
        }
        Object target = null;
        if (StringUtils.isNotBlank(targetServiceName)) {
            target = factory.getExtension(clazz, targetServiceName);
        }
        if (target == null) {
            target = factory.getExtension(clazz);
        }
        if (target == null) {
            throw new IrpcServiceNotFoundException("Target service not found: " + serviceName);
        }
        Class<?>[] parameterTypes = new Class[parameterSignatures.length];
        Method targetMethod = null;
        try {
            for (int i = 0; i < parameterSignatures.length; i++) {
                parameterTypes[i] = Class.forName(parameterSignatures[i]);
            }
            targetMethod = target.getClass().getMethod(methodName, parameterTypes);
        } catch (Exception e) {
            throw new IrpcServiceNotFoundException("Target service not found: " + serviceName);
        }
        if (targetMethod == null) {
            throw new IrpcServiceNotFoundException("Target service not found: " + serviceName);
        }
        targetMethod.setAccessible(true);
        try {
            return targetMethod.invoke(target, arguments);
        } catch (Exception e) {
            throw new IrpcException("Target service execution exception: " + serviceName);
        }
    }
}
