package com.lsd.test.dynmic.source.config.datasource;

public class DataSourceContextHolder {

    private static final ThreadLocal<String> dataSourceId = new InheritableThreadLocal<>();

    public static void setDataSourceKey(String tenantId) {
        dataSourceId.set(tenantId);
    }

    public static String getDataSourceKey() {
        return dataSourceId.get();
    }

    public static void clearDataSourceKey() {
        dataSourceId.remove();
    }
}
