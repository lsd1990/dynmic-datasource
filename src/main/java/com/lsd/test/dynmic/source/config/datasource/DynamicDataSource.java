package com.lsd.test.dynmic.source.config.datasource;

import com.lsd.test.dynmic.source.remote.TenantDbService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.ApplicationContext;
import org.springframework.context.annotation.Lazy;
import org.springframework.jdbc.datasource.lookup.AbstractRoutingDataSource;
import org.springframework.stereotype.Component;
import org.springframework.util.StringUtils;

import javax.annotation.PostConstruct;
import javax.sql.DataSource;

@Slf4j
@Component
public class DynamicDataSource extends AbstractRoutingDataSource {

    @Autowired
    private ApplicationContext applicationContext;

    @Lazy
    @Autowired
    private DynamicDataSourceSummoner summoner;

    @Lazy
    @Autowired
    private TenantDbService tenantDbService;

    private static boolean loaded = false;

    @PostConstruct
    public void init(){
        // 防止重复执行
        if (!loaded) {
            loaded = true;
            try {
                summoner.registerDynamicDataSources();
            } catch (Exception e) {
                log.error("数据源初始化失败, Exception:", e);
            }
        }
    }

    @Override
    protected String determineCurrentLookupKey() {
        String tenantId = DatabaseContextHolder.getDataSourceKey();
        return DataSourceUtil.getDataSourceBeanId(tenantId);
    }

    @Override
    protected DataSource determineTargetDataSource() {
        String tenantId = DatabaseContextHolder.getDataSourceKey();
        String beanKey = DataSourceUtil.getDataSourceBeanId(tenantId);
        if (!StringUtils.hasText(tenantId) || applicationContext.containsBean(beanKey)) {
            return super.determineTargetDataSource();
        }
        if (tenantDbService.exist(tenantId)) {
            summoner.registerDynamicDataSources();
        }
        return super.determineTargetDataSource();
    }
}
