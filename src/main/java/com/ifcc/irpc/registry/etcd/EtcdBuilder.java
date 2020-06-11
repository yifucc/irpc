package com.ifcc.irpc.registry.etcd;

import io.etcd.jetcd.Client;
import io.etcd.jetcd.Lease;
import io.etcd.jetcd.lease.LeaseKeepAliveResponse;
import io.grpc.stub.StreamObserver;
import lombok.extern.slf4j.Slf4j;

/**
 * @author chenghaifeng
 * @date 2020-06-09
 * @description
 */
@Slf4j
public class EtcdBuilder {

    private String registryAddress;

    private Client etcd;

    private Lease lease;

    private long leaseId;

    private EtcdStatus status;



    public EtcdBuilder(String registryAddress) {
        this.registryAddress = registryAddress;
        this.status = EtcdStatus.NOT_CONNECTED;
    }

    public Client EtcdCli() {
        if(this.etcd != null && isConnected()) {
            return this.etcd;
        }
        return connectEtcd();
    }

    public long leaseId() {
        return this.leaseId;
    }

    private Client connectEtcd() {
        this.etcd = Client.builder().endpoints(registryAddress.split(",")).build();
        this.lease = etcd.getLeaseClient();
        try {
            this.leaseId = lease.grant(10).get().getID();
        } catch (Exception e) {
            log.error("", e);
        }
        this.keepAlive();
        this.status = EtcdStatus.CONNECTED;
        return this.etcd;
    }

    public boolean isConnected() {
        return EtcdStatus.CONNECTED.equals(this.status);
    }

    private void keepAlive() {
        try {
            lease.keepAlive(leaseId, new StreamObserver<LeaseKeepAliveResponse>() {
                /**
                 * 确定下一次租约时间后触发
                 * @param leaseKeepAliveResponse
                 */
                @Override
                public void onNext(LeaseKeepAliveResponse leaseKeepAliveResponse) {
                }

                /**
                 * 发生错误时触发
                 * @param throwable
                 */
                @Override
                public void onError(Throwable throwable) {
                    log.error("[EtcdBuilder] Etcd client keepAlive error.", throwable);
                    status = EtcdStatus.EXCEPTION;
                }

                /**
                 * 租约过期时触发
                 */
                @Override
                public void onCompleted() {
                    log.error("[EtcdBuilder] Etcd client lease has expired.");
                    status = EtcdStatus.EXPIRED;
                }
            });
        } catch (Exception e) {
            log.error(e.getMessage(), e);
        }
    }

    public enum EtcdStatus {
        /**
         * 未连接
         */
        NOT_CONNECTED,
        /**
         * 已连接
         */
        CONNECTED,
        /**
         * 异常
         */
        EXCEPTION,
        /**
         * 租约过期
         */
        EXPIRED;
    }
}
