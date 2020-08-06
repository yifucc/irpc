package com.ifcc.irpc.boot;

import lombok.extern.slf4j.Slf4j;

import java.io.BufferedReader;
import java.io.InputStream;
import java.io.InputStreamReader;

/**
 * @author chenghaifeng
 * @date 2020-08-06
 * @description
 */
@Slf4j
class IrpcBanner {
    private static final String IRPC_BOOT = " :: iRPC :: ";
    IrpcBanner() {}

    public void printBanner() {
        InputStream stream = Thread.currentThread().getContextClassLoader().getResourceAsStream("banner.txt");
        if (stream != null) {
            BufferedReader reader = new BufferedReader(new InputStreamReader(stream));
            String line = null;
            StringBuilder builder = new StringBuilder();
            try {
                while ((line = reader.readLine()) != null) {
                    builder.append("\n");
                    builder.append(line);
                }
                builder.append("\n");
                builder.append(IRPC_BOOT);
                builder.append(IrpcVersion.getVersion());
                log.info(builder.toString());
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
    }
}
