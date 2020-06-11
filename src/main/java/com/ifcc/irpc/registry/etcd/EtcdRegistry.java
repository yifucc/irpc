package com.ifcc.irpc.registry.etcd;

import com.ifcc.irpc.common.Const;
import com.ifcc.irpc.exceptions.RegistryServiceFailedException;
import com.ifcc.irpc.registry.Registry;
import com.ifcc.irpc.registry.RegistryContext;
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

    private EtcdBuilder etcdBuilder;

    public EtcdRegistry(EtcdBuilder etcdBuilder) {
        this.etcdBuilder = etcdBuilder;
    }

    @Override
    public void register(RegistryContext ctx) throws RegistryServiceFailedException {
        Client etcd = etcdBuilder.EtcdCli();
        String key = MessageFormat.format("{0}/{1}{2}/{3}", Const.ZK_REGISTRY_PATH, ctx.getService(), Const.ZK_PROVIDERS_PATH, ctx.getUrl());
        try {
            KV kv = etcd.getKVClient();
            CompletableFuture<PutResponse> future = kv.put(ByteSequence.from(key, Charset.forName("utf-8")), ByteSequence.EMPTY, PutOption.newBuilder().withLeaseId(etcdBuilder.leaseId()).build());
            future.handleAsync(
                (PutResponse putResponse, Throwable throwable) -> {
                    if (throwable != null) {
                        try {
                            Thread.sleep(10000);
                            register(ctx);
                        } catch (Exception e) {
                            e.printStackTrace();
                        }
                    }
                    log.info("[EtcdRegistry] service: {} registered to etcd successfully.", ctx.getService());
                    return putResponse;
                }
            );
            kv.txn();
            if (!ctx.getHasWatched()) {
                watch(key, ctx);
            }
        } catch (Exception e) {
            throw new RegistryServiceFailedException("[EtcdRegistry] Etcd register service failed.", e);
        }
    }

    private void watch(String key, RegistryContext ctx) {
        Client etcd = etcdBuilder.EtcdCli();
        Watch watch = etcd.getWatchClient();
        watch.watch(ByteSequence.from(key, Charset.forName("utf-8")),
                response -> {
            for (WatchEvent event : response.getEvents()) {
                if (WatchEvent.EventType.DELETE == event.getEventType()) {
                    try {
                        register(ctx);
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
            watch(key, ctx);
                });
        ctx.setHasWatched(true);
    }


}
