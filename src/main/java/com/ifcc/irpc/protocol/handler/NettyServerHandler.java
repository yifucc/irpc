package com.ifcc.irpc.protocol.handler;

import com.ifcc.irpc.common.IrpcRequest;
import com.ifcc.irpc.common.IrpcResponse;
import com.ifcc.irpc.spi.ExtensionLoad;
import com.ifcc.irpc.spi.factory.ExtensionFactory;
import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.SimpleChannelInboundHandler;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;

import java.lang.reflect.InvocationTargetException;
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

    private Object handle(IrpcRequest request) throws ClassNotFoundException, NoSuchMethodException, InvocationTargetException, IllegalAccessException {
        String targetServiceName = request.getTargetServiceName();
        String serviceName = request.getServiceName();
        String methodName = request.getMethodName();
        String[] parameterSignatures = request.getParameterSignatures();
        Object[] arguments = request.getArguments();
        Class<?> clazz = Class.forName(serviceName);
        Object target = null;
        if (StringUtils.isNotBlank(targetServiceName)) {
            target = factory.getExtension(clazz, targetServiceName);
        }
        if (target == null) {
            factory.getExtension(clazz);
        }
        if (target == null) {
            throw new ClassNotFoundException("Target service not found: " + serviceName);
        }
        Class<?>[] parameterTypes = new Class[parameterSignatures.length];
        for (int i = 0; i < parameterSignatures.length; i++) {
            parameterTypes[i] = Class.forName(parameterSignatures[i]);
        }
        Method targetMethod = target.getClass().getMethod(methodName, parameterTypes);
        targetMethod.setAccessible(true);
        return targetMethod.invoke(target, arguments);
    }
}
