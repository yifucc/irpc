package com.ifcc.irpc.boot;

import org.apache.commons.lang3.StringUtils;

/**
 * @author chenghaifeng
 * @date 2020-08-06
 * @description
 */
public final class IrpcVersion {

    private static final String DEFAULT_IRPC_VERSION = "1.0.0 - default";

    private IrpcVersion() {}

    public static String getVersion() {
        String version = IrpcVersion.class.getPackage().getImplementationVersion();
        return StringUtils.isNotBlank(version)? version : DEFAULT_IRPC_VERSION;
    }
}
