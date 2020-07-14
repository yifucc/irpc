package test.com.ifcc.irpc.discovery;

import com.ifcc.irpc.annotation.client.IrpcConsumer;

/**
 * @author chenghaifeng
 * @date 2020-07-14
 * @description
 */
@IrpcConsumer
public interface IComsumer {
    Object getInfo();
}
