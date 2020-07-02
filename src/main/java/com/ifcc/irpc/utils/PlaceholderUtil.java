package com.ifcc.irpc.utils;

import org.apache.commons.lang3.StringUtils;

import java.util.Properties;

/**
 * @author chenghaifeng
 * @date 2020-07-02
 * @description
 */
public class PlaceholderUtil {
    private final static String PLACEHOLDER_PREFIX = "${";
    private final static String PLACEHOLDER_SUFFIX = "}";
    private final static String VALUE_SEPARATOR = ":";

    public static String resolveStringValue(Properties props, String name) {
        if(StringUtils.isBlank(name)) {
            return null;
        }
        name = name.trim();
        if (!name.startsWith(PLACEHOLDER_PREFIX)) {
            return name;
        }
        name = name.substring(PLACEHOLDER_PREFIX.length(), name.length() - PLACEHOLDER_SUFFIX.length());
        if (name.contains(VALUE_SEPARATOR)) {
            String[] stringValue = name.split(VALUE_SEPARATOR);
            return props.getProperty(stringValue[0].trim(), stringValue[1].trim());
        }
        return props.getProperty(name);
    }
}
