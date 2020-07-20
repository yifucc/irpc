package com.ifcc.irpc.registry.etcd;

import com.ifcc.irpc.common.Const;
import com.ifcc.irpc.common.URL;
import com.ifcc.irpc.exceptions.RegistryServiceFailedException;
import com.ifcc.irpc.registry.Registry;
import com.ifcc.irpc.spi.annotation.Inject;
import io.etcd.jetcd.ByteSequence;
import io.etcd.jetcd.Client;
import io.etcd.jetcd.KV;
import io.etcd.jetcd.Watch;
import io.etcd.jetcd.kv.PutResponse;
import io.etcd.jetcd.options.PutOption;
import io.etcd.jetcd.watch.WatchEvent;
import lombok.extern.slf4j.Slf4j;

import java.nio.charset.Charset;
import java.text.MessageFormat;
import java.util.concurrent.CompletableFuture;

/**
 * @author chenghaifeng
 * @date 2020-06-09
 * @description etcd实现注册器
 */
@Slf4j
public class EtcdRegistry implements Registry {

    @Inject
    private EtcdBuilder etcdBuilder;

    public EtcdRegistry(EtcdBuilder etcdBuilder) {
        this.etcdBuilder = etcdBuilder;
    }

    public EtcdRegistry() {}

    @Override
    public void register(URL url) throws RegistryServiceFailedException {
        Client etcd = etcdBuilder.etcdCli();
        String registerUrl = url.getHost() + Const.COLON + url.getPort();
        String key = MessageFormat.format("{0}/{1}{2}/{3}", Const.ZK_REGISTRY_PATH, url.getService(), Const.ZK_PROVIDERS_PATH, registerUrl);
        try {
            KV kv = etcd.getKVClient();
            CompletableFuture<PutResponse> future = kv.put(ByteSequence.from(key, Charset.forName("utf-8")), ByteSequence.EMPTY, PutOption.newBuilder().withLeaseId(etcdBuilder.leaseId()).build());
            future.handleAsync(
                (PutResponse putResponse, Throwable throwable) -> {
                    if (throwable != null) {
                        try {
                            Thread.sleep(10000);
                            register(url);
                        } catch (Exception e) {
                            e.printStackTrace();
                        }
                    }
                    log.info("[EtcdRegistry] service: {} registered to etcd successfully.", url.getService());
                    return putResponse;
                }
            );
            kv.txn();
            if (!url.getHasWatched().get()) {
                watch(key, url);
            }
        } catch (Exception e) {
            throw new RegistryServiceFailedException("[EtcdRegistry] Etcd register service failed.", e);
        }
    }

    private void watch(String key, URL url) {
        Client etcd = etcdBuilder.etcdCli();
        Watch watch = etcd.getWatchClient();
        watch.watch(ByteSequence.from(key, Charset.forName("utf-8")),
                response -> {
            for (WatchEvent event : response.getEvents()) {
                if (WatchEvent.EventType.DELETE == event.getEventType()) {
                    try {
                        register(url);
                        break;
                    } catch (Exception e) {
                        e.printStackTrace();
                    }
                }
            }
        },
                error -> {
            error.printStackTrace();
            try {
                Thread.sleep(10000);
            } catch (Exception e) {
                e.printStackTrace();
            }
            watch(key, url);
                });
        url.getHasWatched().set(true);
    }


}
