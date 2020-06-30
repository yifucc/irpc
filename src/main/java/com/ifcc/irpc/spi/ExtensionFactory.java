package com.ifcc.irpc.spi;

import com.google.common.collect.Maps;
import com.ifcc.irpc.common.config.Config;
import com.ifcc.irpc.common.config.ConfigLoader;
import com.ifcc.irpc.common.config.PropertiesProvider;
import com.ifcc.irpc.discovery.Discovery;
import com.ifcc.irpc.registry.Registry;
import org.apache.commons.lang3.StringUtils;

import java.lang.reflect.Field;
import java.util.HashMap;
import java.util.Map;

/**
 * @author chenghaifeng
 * @date 2020-06-29
 * @description
 */
public class ExtensionFactory {
    private static SpiContext spiContext;

    public static <T> T getExtension(Class<T> type, String name) {
        return ExtensionLoad.getExtensionLoad(type).getExtension(name);
    }

    public static <T> T getExtension(Class<T> type) {
        String name = getSpiContext().getCustomConfig().get(type.getName());
        if (StringUtils.isBlank(name)) {
            return ExtensionLoad.getExtensionLoad(type).getDefaultExtension();
        }
        return ExtensionLoad.getExtensionLoad(type).getExtension(name);
    }

    public static SpiContext getSpiContext() {
        if (spiContext == null) {
            try {
                // 初始化spiContext
                spiContext = new SpiContext();
                PropertiesProvider provider = new PropertiesProvider("irpc.properties");
                Config config = new ConfigLoader(provider).load();
                Map<String, String> map = Maps.newHashMap();
                map.put(Registry.class.getName(), config.getRegistry());
                map.put(Discovery.class.getName(), config.getRegistry());
                Field[] fields = Config.class.getDeclaredFields();
                for (Field field : fields) {
                    field.setAccessible(true);
                    map.put(field.getName(), field.get(config).toString());
                }
                spiContext.setCustomConfig(map);
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
        return spiContext;
    }
}
