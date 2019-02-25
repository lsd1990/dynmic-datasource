package com.lsd.test.dynmic.source.config.datasource;

import lombok.Data;
import org.springframework.boot.context.properties.ConfigurationProperties;

@ConfigurationProperties(
        prefix = "database",
        ignoreInvalidFields = true
)
@Data
public class DatabaseProperties {

    private Integer initialSize;
    private Integer minIdle;
    private Integer maxWait;
    private Integer maxActive;
    private Integer timeBetweenEvictionRunsMillis;
    private Integer minEvictableIdleTimeMillis;
    private String validationQuery;
    private Boolean testWhileIdle;
    private Boolean testOnBorrow;
    private Boolean testOnReturn;
    private Boolean poolPreparedStatements;
    private Integer maxPoolPreparedStatementPerConnectionSize;
    private Boolean removeAbandoned;
    private Integer removeAbandonedTimeout;

}
