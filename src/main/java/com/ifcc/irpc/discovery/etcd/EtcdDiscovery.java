package com.ifcc.irpc.discovery.etcd;

import com.ifcc.irpc.common.Const;
import com.ifcc.irpc.discovery.AbstractDiscovery;
import com.ifcc.irpc.discovery.DiscoveryContext;
import com.ifcc.irpc.exceptions.DiscoveryServiceFailedException;
import com.ifcc.irpc.registry.etcd.EtcdBuilder;
import io.etcd.jetcd.ByteSequence;
import io.etcd.jetcd.Client;
import io.etcd.jetcd.KV;
import io.etcd.jetcd.KeyValue;
import io.etcd.jetcd.Watch;
import io.etcd.jetcd.kv.GetResponse;
import io.etcd.jetcd.options.GetOption;
import io.etcd.jetcd.options.WatchOption;
import lombok.extern.slf4j.Slf4j;

import java.nio.charset.Charset;
import java.text.MessageFormat;
import java.util.concurrent.CompletableFuture;

/**
 * @author chenghaifeng
 * @date 2020-06-11
 * @description etcd实现的发现器
 */
@Slf4j
public class EtcdDiscovery extends AbstractDiscovery {

    private EtcdBuilder builder;

    public EtcdDiscovery(EtcdBuilder builder) {
        super();
        this.builder = builder;
    }

    @Override
    public void discover(DiscoveryContext ctx) throws DiscoveryServiceFailedException {
        try {
            Client etcd = builder.EtcdCli();
            KV kv = etcd.getKVClient();
            String key = MessageFormat.format("{0}/{1}{2}/", Const.ZK_REGISTRY_PATH, ctx.getService(), Const.ZK_PROVIDERS_PATH);
            CompletableFuture<GetResponse> future = kv.get(ByteSequence.from(key, Charset.forName("utf-8")), GetOption.newBuilder().withPrefix(ByteSequence.from(key, Charset.forName("utf-8"))).build());
            future.handleAsync(
                    (GetResponse getResponse, Throwable throwable) -> {
                        if (throwable != null) {
                            try {
                                discover(ctx);
                                Thread.sleep(10000);
                                return null;
                            } catch (Exception e) {
                                e.printStackTrace();
                            }
                        }
                        this.serverAddress().clear();
                        for (KeyValue keyValue : getResponse.getKvs()) {
                            String addr = keyValue.getKey().toString(Charset.forName("utf-8")).replace(key, "");
                            this.serverAddress().add(addr);
                        }
                        return getResponse;
                    }
            );
            if (!ctx.getHasWatched()) {
                watch(key, ctx);
            }
            String consumerKey = MessageFormat.format("{0}/{1}{2}/{3}", Const.ZK_REGISTRY_PATH, ctx.getService(), Const.ZK_CONSUMERS_PATH, ctx.getIp());
            kv.put(ByteSequence.from(consumerKey, Charset.forName("utf-8")), ByteSequence.EMPTY);

        } catch (Exception e) {
            throw new DiscoveryServiceFailedException("[EtcdDiscovery] Etcd discovery service failed.", e);
        }
    }

    private void watch(String key, DiscoveryContext ctx) {
        Watch watch = builder.EtcdCli().getWatchClient();
        watch.watch(ByteSequence.from(key, Charset.forName("utf-8")), WatchOption.newBuilder().withPrefix(ByteSequence.from(key, Charset.forName("utf-8"))).build(),
                res -> {
                    try {
                        discover(ctx);
                    } catch (Exception e) {
                        log.error("", e);
                    }
                },
                error -> {
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
