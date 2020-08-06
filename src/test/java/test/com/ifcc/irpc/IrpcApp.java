package test.com.ifcc.irpc;

import com.ifcc.irpc.boot.IrpcApplication;
import com.ifcc.irpc.annotation.server.IrpcServer;

/**
 * @author chenghaifeng
 * @date 2020-07-24
 * @description
 */
@IrpcServer(scanBasePackages = {"test.com.ifcc.irpc"})
public class IrpcApp {
    public static void main(String[] args) {
        IrpcApplication.run(IrpcApp.class, args);
    }
}