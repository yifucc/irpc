package com.ifcc.irpc.client.wrapper;

import com.ifcc.irpc.discovery.Discovery;
import com.ifcc.irpc.discovery.DiscoveryContext;
import com.ifcc.irpc.spi.annotation.Inject;
import com.ifcc.irpc.utils.LocalIpUtil;

import java.lang.reflect.InvocationHandler;
import java.lang.reflect.Method;
import java.lang.reflect.Proxy;

/**
 * @author chenghaifeng
 * @date 2020-07-14
 * @description
 */
public class ProxyWrapper<T> {
    private Class<T> interfaceClass;
    private DiscoveryContext context;
    private T proxy;

    @Inject
    private Discovery discovery;

    public ProxyWrapper(Class<T> clazz) {
        this.interfaceClass = clazz;
    }

    @SuppressWarnings("unchecked")
    public void init() {
        context = new DiscoveryContext(interfaceClass.getName(), LocalIpUtil.localRealIp());
        try {
            discovery.discover(context);
        } catch (Exception e) {
            e.printStackTrace();
        }
        proxy = (T)Proxy.newProxyInstance(interfaceClass.getClassLoader(), new Class[]{interfaceClass}, new InvocationHandler() {
            @Override
            public Object invoke(Object proxy, Method method, Object[] args) throws Throwable {
                return null;
            }
        });
    }

    public T getObject() {
        return proxy;
    }
}
