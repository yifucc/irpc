package com.ifcc.irpc;

import com.ifcc.irpc.registry.Registry;
import com.ifcc.irpc.spi.ExtensionLoad;

/**
 * @author chenghaifeng
 * @date 2020-06-12
 * @description
 */
public class SPIMain {

    public static void main(String[] args) {
        Registry zookeeper = ExtensionLoad.getExtensionLoad(Registry.class).getDefaultExtension();
        System.out.println(zookeeper);
    }
}
