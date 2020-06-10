package com.ifcc.irpc.registry.etcd;

import io.etcd.jetcd.Client;
import io.etcd.jetcd.CloseableClient;
import io.etcd.jetcd.Lease;
import io.etcd.jetcd.lease.LeaseKeepAliveResponse;
import io.etcd.jetcd.options.LeaseOption;
import io.grpc.stub.StreamObserver;
import lombok.extern.slf4j.Slf4j;

import java.util.concurrent.Executors;

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

    private String status;



    public EtcdBuilder(String registryAddress) {
        this.registryAddress = registryAddress;
        this.status = "NOT_CONNECTED";
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
            
        }
        this.keepAlive();
        this.status = "CONNECTED";
        return this.etcd;
    }

    public boolean isConnected() {
        return "CONNECTED".equals(this.status);
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
                    log.info("续约");
                }

                /**
                 * 发生错误时触发
                 * @param throwable
                 */
                @Override
                public void onError(Throwable throwable) {
                    log.error("发生异常");
                    status = "EXCEPTION";
                }

                /**
                 * 租约过期时触发
                 */
                @Override
                public void onCompleted() {
                    log.error("租约过期");
                    status = "EXPIRED";
                }
            });
        } catch (Exception e) {
            log.error(e.getMessage(), e);
        }
    }
}
