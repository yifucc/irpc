package com.ifcc.irpc.client.wrapper;

import com.ifcc.irpc.annotation.client.IrpcConsumer;
import com.ifcc.irpc.cache.Cache;
import com.ifcc.irpc.cache.decorators.ScheduledCache;
import com.ifcc.irpc.cache.impl.IrpcCache;
import com.ifcc.irpc.client.Client;
import com.ifcc.irpc.client.ClientFactory;
import com.ifcc.irpc.common.Const;
import com.ifcc.irpc.common.Invocation;
import com.ifcc.irpc.common.IrpcRequest;
import com.ifcc.irpc.common.Result;
import com.ifcc.irpc.common.URL;
import com.ifcc.irpc.common.config.IrpcConfig;
import com.ifcc.irpc.discovery.Discovery;
import com.ifcc.irpc.exceptions.IrpcException;
import com.ifcc.irpc.spi.annotation.Inject;
import com.ifcc.irpc.utils.LocalIpUtil;
import org.apache.commons.lang3.StringUtils;

import java.io.File;
import java.io.FileInputStream;
import java.lang.reflect.InvocationHandler;
import java.lang.reflect.Method;
import java.lang.reflect.Proxy;
import java.util.Map;
import java.util.Properties;

/**
 * @author chenghaifeng
 * @date 2020-07-14
 * @description
 */
public class ProxyWrapper<T> {
    private Class<T> interfaceClass;
    private URL url;
    private T proxy;
    private Cache<Invocation, Object> cache;

    @Inject
    private Discovery discovery;

    @Inject
    private ClientFactory clientFactory;

    @Inject
    private IrpcConfig config;

    public ProxyWrapper(Class<T> clazz) {
        this.interfaceClass = clazz;
        if (config.getCacheTime() != Const.OFF_STATUS) {
            this.cache = new ScheduledCache(new IrpcCache(), config.getCacheTime());
        }
    }

    public void init() {
        if (interfaceClass == null) {
            throw new IllegalArgumentException("Interface class cannot be null");
        }
        String resolve = System.getProperty(interfaceClass.getName());
        String resolveFile = null;
        if(StringUtils.isBlank(resolve)) {
            resolveFile = System.getProperty("irpc.resolve.file");
            if (StringUtils.isBlank(resolveFile)) {
                java.net.URL resource = this.getClass().getClassLoader().getResource("irpc.properties");
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
        url = new URL(LocalIpUtil.localRealIp(), interfaceClass.getName());
        try {
            discovery.discover(url);
            Thread.sleep(5000);
        } catch (Exception e) {
            e.printStackTrace();
        }
        proxy = createProxy();
    }

    public T getObject() {
        return proxy;
    }

    @SuppressWarnings("unchecked")
    private T createProxy() {
        return  (T)Proxy.newProxyInstance(interfaceClass.getClassLoader(), new Class[]{interfaceClass}, new InvocationHandler() {
            @Override
            public Object invoke(Object proxy, Method method, Object[] args) throws Throwable {
                IrpcRequest request = new IrpcRequest(interfaceClass.getName(), method.getName(), method.getParameterTypes(), args);
                Object cache = getCache(request);
                if (cache != null) {
                    return cache;
                }
                Map<String, URL> urls = ProxyWrapper.this.url.getUrls();
                if(urls.isEmpty()) {
                    throw new IrpcException("There is no available service provider.");
                }
                URL targetUrl = urls.values().iterator().next();
                Client client = clientFactory.getClient(targetUrl);
                IrpcConsumer irpcConsumer = interfaceClass.getAnnotation(IrpcConsumer.class);
                if (irpcConsumer != null && StringUtils.isNotBlank(irpcConsumer.targetName())) {
                    request.setTargetServiceName(irpcConsumer.targetName());
                }
                Result result = client.send(request).get();
                if (result.getException() != null) {
                    throw result.getException();
                }
                putCache(request,result.getValue());
                return result.getValue();
            }
        });
    }

    private Object getCache(IrpcRequest request) {
        if (cache == null) {
            return null;
        }
        return cache.get(request);
    }

    private void putCache(IrpcRequest request, Object value) {
        if (cache == null) {
            return;
        }
        cache.put(request, value);
    }
}
