package com.lsd.test.dynmic.source.config.redis;

public class RedisContextHolder {

    private static final ThreadLocal<String> dataSourceId = new InheritableThreadLocal<>();

    public static void setRedisKey(String tenantId) {
        dataSourceId.set(tenantId);
    }

    public static String getRedisKey() {
        return dataSourceId.get();
    }

    public static void clearRedisKey() {
        dataSourceId.remove();
    }
}
