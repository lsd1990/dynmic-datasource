package com.lsd.test.dynmic.source.config.datasource;

import com.alibaba.druid.filter.config.ConfigTools;
import com.alibaba.druid.pool.DruidDataSource;
import com.lsd.test.dynmic.source.dto.TenantDbConfig;
import com.lsd.test.dynmic.source.remote.TenantDbService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.support.AbstractBeanDefinition;
import org.springframework.beans.factory.support.BeanDefinitionBuilder;
import org.springframework.beans.factory.support.DefaultListableBeanFactory;
import org.springframework.context.ConfigurableApplicationContext;
import org.springframework.stereotype.Component;
import org.springframework.util.CollectionUtils;
import org.springframework.util.StringUtils;

import java.util.*;

@Slf4j
@Component
public class DynamicDataSourceSummoner {

    private static final String DEFAULT_DATA_SOURCE_BEAN_KEY = "defaultDataSource";

    @Autowired
    private ConfigurableApplicationContext applicationContext;

    @Autowired
    private DynamicDataSource dynamicDataSource;

    @Autowired
    private TenantDbService tenantDbService;


    /**
     * 从数据库读取租户的DB配置,并动态注入Spring容器
     */
    public void registerDynamicDataSources() {
        // 获取所有租户的DB配置
        List<TenantDbConfig> tenantConfigEntities = tenantDbService.listAll();
        if (CollectionUtils.isEmpty(tenantConfigEntities)) {
            throw new IllegalStateException("应用程序初始化失败,请先配置数据源");
        }
        // 把数据源bean注册到容器中
        addDataSourceBeans(tenantConfigEntities);
    }
    /**
     * 根据DataSource创建bean并注册到容器中
     */
    private void addDataSourceBeans(List<TenantDbConfig> tenantConfigEntities) {
        Map<Object, Object> targetDataSources = new HashMap<>();

        DefaultListableBeanFactory beanFactory = (DefaultListableBeanFactory) applicationContext.getAutowireCapableBeanFactory();
        for (TenantDbConfig entity : tenantConfigEntities) {
            String beanId = DataSourceUtil.getDataSourceBeanId(entity.getTenantId());

            // 如果该数据源已经在spring里面注册过,则不重新注册
            if (applicationContext.containsBean(beanId)) {

                DruidDataSource existsDataSource = applicationContext.getBean(beanId, DruidDataSource.class);
                if (isSameDataSource(existsDataSource, entity)) {
                    continue;
                }
            }

            //  组装bean
            AbstractBeanDefinition beanDefinition = getBeanDefinition(entity, beanId);

            //  注册bean
            beanFactory.registerBeanDefinition(beanId, beanDefinition);

            //  放入map中，注意一定是刚才创建bean对象
            targetDataSources.put(beanId, applicationContext.getBean(beanId));
        }
        //  将创建的map对象set到 targetDataSources；
        dynamicDataSource.setTargetDataSources(targetDataSources);
        //  必须执行此操作，才会重新初始化AbstractRoutingDataSource 中的 resolvedDataSources，也只有这样，动态切换才会起效
        dynamicDataSource.afterPropertiesSet();
    }
    /**
     * 组装数据源spring bean
     */
    private AbstractBeanDefinition getBeanDefinition(TenantDbConfig entity, String beanKey) {
        BeanDefinitionBuilder builder = BeanDefinitionBuilder.genericBeanDefinition(DruidDataSource.class);
        builder.getBeanDefinition().setAttribute("id", beanKey);
        // 其他配置继承defaultDataSource
        builder.setParentName(DEFAULT_DATA_SOURCE_BEAN_KEY);
        builder.setInitMethodName("init");
        builder.setDestroyMethodName("close");
        builder.addPropertyValue("name", beanKey);
        builder.addPropertyValue("url", DataSourceUtil.getJDBCUrl(entity.getDbUrl()));
        builder.addPropertyValue("username", entity.getDbUser());
        builder.addPropertyValue("password", entity.getDbPassword());
//        TODO 暂时不加锁
//        builder.addPropertyValue("connectionProperties", DataSourceUtil.getConnectionProperties(entity.getPublicKey()));
        return builder.getBeanDefinition();
    }
    /**
     * 判断Spring容器里面的DataSource与数据库的DataSource信息是否一致
     * 备注:这里没有判断public_key,因为另外三个信息基本可以确定唯一了
     */
    private boolean isSameDataSource(DruidDataSource existsDataSource, TenantDbConfig entity) {


        boolean sameUrl = Objects.equals(existsDataSource.getUrl(), DataSourceUtil.getJDBCUrl(entity.getDbUrl()));
        if (!sameUrl) {
            return false;
        }
        boolean sameUser = Objects.equals(existsDataSource.getUsername(), entity.getDbUser());
        if (!sameUser) {
            return false;
        }

        if(StringUtils.isEmpty(entity.getPublicKey())){
            return true;
        }

        try {
            String decryptPassword = ConfigTools.decrypt(entity.getPublicKey(), entity.getDbPassword());
            return Objects.equals(existsDataSource.getPassword(), decryptPassword);
        } catch (Exception e) {
            log.error("数据源密码校验失败,Exception:{}", e);
            return false;
        }
    }
}

