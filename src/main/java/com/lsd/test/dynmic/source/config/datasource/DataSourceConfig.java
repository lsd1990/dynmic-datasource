package com.lsd.test.dynmic.source.config.datasource;

import com.alibaba.druid.pool.DruidDataSource;
import org.hibernate.cache.internal.NoCachingRegionFactory;
import org.hibernate.cache.spi.RegionFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.autoconfigure.mail.MailProperties;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.boot.orm.jpa.hibernate.SpringPhysicalNamingStrategy;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Primary;
import org.springframework.orm.hibernate5.HibernateTransactionManager;
import org.springframework.orm.hibernate5.LocalSessionFactoryBean;

import javax.sql.DataSource;
import java.util.Properties;

@Configuration
@EnableConfigurationProperties({DatabaseProperties.class, HibernateProperites.class})
public class DataSourceConfig {

    @Autowired
    private DatabaseProperties databaseProperties;

    @Autowired
    private HibernateProperites hibernateProperites;

    @Autowired
    private DynamicDataSource dynamicDataSource;

    @Bean("defaultDataSource")
    public DruidDataSource createDataSource(){

        DruidDataSource dataSource = new DruidDataSource();

        dataSource.setInitialSize(databaseProperties.getInitialSize());
        dataSource.setMinIdle(databaseProperties.getMinIdle());
        dataSource.setMaxActive(databaseProperties.getMaxActive());
        dataSource.setMaxWait(databaseProperties.getMaxWait());
        dataSource.setTimeBetweenEvictionRunsMillis(databaseProperties.getTimeBetweenEvictionRunsMillis());
        dataSource.setMinEvictableIdleTimeMillis(databaseProperties.getMinEvictableIdleTimeMillis());
        dataSource.setValidationQuery(databaseProperties.getValidationQuery());
        dataSource.setTestWhileIdle(databaseProperties.getTestWhileIdle());
        dataSource.setTestOnBorrow(databaseProperties.getTestOnBorrow());
        dataSource.setTestOnReturn(databaseProperties.getTestOnReturn());
        dataSource.setPoolPreparedStatements(databaseProperties.getPoolPreparedStatements());
        dataSource.setMaxPoolPreparedStatementPerConnectionSize(databaseProperties.getMaxPoolPreparedStatementPerConnectionSize());
        dataSource.setRemoveAbandoned(databaseProperties.getRemoveAbandoned());
        dataSource.setRemoveAbandonedTimeout(databaseProperties.getRemoveAbandonedTimeout());

        return  dataSource;
    }

    @Bean()
    public RegionFactory createRegionFactory(){
        NoCachingRegionFactory factory = new NoCachingRegionFactory();

        return factory;
    }

    @Bean("transactionManager")
    @Primary
    public HibernateTransactionManager transactionManager(){

        HibernateTransactionManager transactionManager = new HibernateTransactionManager();
        transactionManager.setDataSource(dynamicDataSource);
        transactionManager.setSessionFactory(sessionFactory().getObject());
        return transactionManager;
    }


    @Bean(name = "sessionFactory")
    @Primary
    public LocalSessionFactoryBean sessionFactory() {
        LocalSessionFactoryBean sessionFactoryBean = createMysqlSessionFactory(dynamicDataSource);
        sessionFactoryBean.setPackagesToScan(hibernateProperites.getPackagesToScan());
        return sessionFactoryBean;
    }


    private LocalSessionFactoryBean createMysqlSessionFactory(DataSource dataSource) {
        LocalSessionFactoryBean sessionFactoryBean = new LocalSessionFactoryBean();
        sessionFactoryBean.setDataSource(dataSource);
        sessionFactoryBean.setPackagesToScan(hibernateProperites.getPackagesToScan());
        sessionFactoryBean.setPhysicalNamingStrategy(new SpringPhysicalNamingStrategy());
        Properties hibernateProperties = new Properties();
        hibernateProperties.put("hibernate.dialect", hibernateProperites.getDialect());
        hibernateProperties.put("hibernate.show_sql", hibernateProperites.getShowSql());
        hibernateProperties.put("hibernate.format_sql", hibernateProperites.getFormatSql());
        hibernateProperties.put("hibernate.use_sql_comments", hibernateProperites.getUseSqlComments());
        hibernateProperties.put("hibernate.generate_statistics", hibernateProperites.getGenerateStatistics());
        hibernateProperties.put("hibernate.cache.use_second_level_cache", hibernateProperites.getCacheUseSecondLevelCache());
        hibernateProperties.put("hibernate.cache.use_query_cache", hibernateProperites.getCacheUseQueryCache());
        hibernateProperties.put("hibernate.max_fetch_depth", hibernateProperites.getMaxFetchDepth());
        hibernateProperties.put("hibernate.default_batch_fetch_size", hibernateProperites.getJdbcFetchSize());
        hibernateProperties.put("hibernate.jdbc.batch_size", hibernateProperites.getJdbcBatchSize());
        hibernateProperties.put("hibernate.jdbc.fetch_size", hibernateProperites.getJdbcFetchSize());

        sessionFactoryBean.setHibernateProperties(hibernateProperties);
        return sessionFactoryBean;
    }
}
