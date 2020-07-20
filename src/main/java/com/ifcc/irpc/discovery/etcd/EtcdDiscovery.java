package com.ifcc.irpc.discovery.etcd;

import com.ifcc.irpc.codec.serialization.Serialization;
import com.ifcc.irpc.codec.serialization.msgpack.MsgpackSerialization;
import com.ifcc.irpc.common.Const;
import com.ifcc.irpc.common.URL;
import com.ifcc.irpc.discovery.Discovery;
import com.ifcc.irpc.exceptions.DiscoveryServiceFailedException;
import com.ifcc.irpc.registry.etcd.EtcdBuilder;
import com.ifcc.irpc.spi.annotation.Inject;
import io.etcd.jetcd.ByteSequence;
import io.etcd.jetcd.Client;
import io.etcd.jetcd.KV;
import io.etcd.jetcd.KeyValue;
import io.etcd.jetcd.Watch;
import io.etcd.jetcd.kv.GetResponse;
import io.etcd.jetcd.options.GetOption;
import io.etcd.jetcd.options.PutOption;
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
public class EtcdDiscovery implements Discovery {

    @Inject
    private EtcdBuilder builder;

    private Serialization serialization = new MsgpackSerialization();

    public EtcdDiscovery(EtcdBuilder builder) {
        this.builder = builder;
    }

    public EtcdDiscovery() {}

    @Override
    public void discover(URL url) throws DiscoveryServiceFailedException {
        try {
            Client etcd = builder.etcdCli();
            KV kv = etcd.getKVClient();
            String key = MessageFormat.format("{0}/{1}{2}/", Const.ZK_REGISTRY_PATH, url.getService(), Const.ZK_PROVIDERS_PATH);
            CompletableFuture<GetResponse> future = kv.get(ByteSequence.from(key, Charset.forName("utf-8")), GetOption.newBuilder().withPrefix(ByteSequence.from(key, Charset.forName("utf-8"))).build());
            future.handleAsync(
                    (GetResponse getResponse, Throwable throwable) -> {
                        if (throwable != null) {
                            try {
                                Thread.sleep(10000);
                                discover(url);
                                return null;
                            } catch (Exception e) {
                                e.printStackTrace();
                            }
                        }
                        url.getUrls().clear();
                        for (KeyValue keyValue : getResponse.getKvs()) {
                            String addr = keyValue.getKey().toString(Charset.forName("utf-8")).replace(key, "");
                            URL discoveryUrl = serialization.unMarshal(URL.class, keyValue.getValue().getBytes());
                            url.getUrls().put(addr, discoveryUrl);
                        }
                        if(url.getUrls().isEmpty()) {
                            log.error("[EtcdDiscovery] There is no available service provider.");
                        }
                        return getResponse;
                    }
            );
            if (!url.getHasWatched().get()) {
                watch(key, url);
            }
            String consumerKey = MessageFormat.format("{0}/{1}{2}/{3}", Const.ZK_REGISTRY_PATH, url.getService(), Const.ZK_CONSUMERS_PATH, url.getHost());
            kv.put(ByteSequence.from(consumerKey, Charset.forName("utf-8")), ByteSequence.EMPTY, PutOption.newBuilder().withLeaseId(builder.leaseId()).build());

        } catch (Exception e) {
            throw new DiscoveryServiceFailedException("[EtcdDiscovery] Etcd discovery service failed.", e);
        }
    }

    private void watch(String key, URL url) {
        Watch watch = builder.etcdCli().getWatchClient();
        watch.watch(ByteSequence.from(key, Charset.forName("utf-8")), WatchOption.newBuilder().withPrefix(ByteSequence.from(key, Charset.forName("utf-8"))).build(),
                res -> {
                    try {
                        discover(url);
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
                    watch(key, url);
                });
        url.getHasWatched().set(true);
    }
}
