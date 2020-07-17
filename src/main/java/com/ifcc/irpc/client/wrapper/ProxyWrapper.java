package com.ifcc.irpc.client.wrapper;

import com.ifcc.irpc.discovery.Discovery;
import com.ifcc.irpc.discovery.DiscoveryContext;
import com.ifcc.irpc.spi.annotation.Inject;
import com.ifcc.irpc.utils.LocalIpUtil;
import org.apache.commons.lang3.StringUtils;

import java.io.File;
import java.io.FileInputStream;
import java.lang.reflect.InvocationHandler;
import java.lang.reflect.Method;
import java.lang.reflect.Proxy;
import java.net.URL;
import java.util.Properties;

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
        if (interfaceClass == null) {
            throw new IllegalArgumentException("Interface class cannot be null");
        }
        String resolve = System.getProperty(interfaceClass.getName());
        String resolveFile = null;
        if(StringUtils.isBlank(resolve)) {
            resolveFile = System.getProperty("irpc.resolve.file");
            if (StringUtils.isBlank(resolveFile)) {
                URL resource = this.getClass().getClassLoader().getResource("irpc.properties");
                if(resource != null) {
                    resolveFile = resource.getPath();
                }
            }
            if (StringUtils.isNotBlank(resolveFile)) {
                Properties props = new Properties();
                FileInputStream inputStream = null;
                try {
                    inputStream = new FileInputStream(new File(resolveFile));
                    props.load(inputStream);
                } catch (Exception e) {
                    e.printStackTrace();
                } finally {
                    try {
                        if(inputStream != null) {
                            inputStream.close();
                        }
                    } catch (Exception e) {
                        e.printStackTrace();
                    }
                }
                resolve = props.getProperty(interfaceClass.getName());
            }
        }
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

    private T createProxy() {
        return null;
    }
}
