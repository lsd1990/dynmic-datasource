package com.lsd.test.dynmic.source.config.datasource;

import lombok.Data;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.context.properties.ConfigurationProperties;

@ConfigurationProperties(
        prefix = "hibernate",
        ignoreInvalidFields = true
)
@Data
public class HibernateProperites {

    private String dialect;

    private String showSql;

    private String formatSql;

    private String useSqlComments;

    private String generateStatistics;

    private String cacheUseSecondLevelCache;

    private String cacheUseQueryCache;

    private String maxFetchDepth;

    private String defaultBatchFetchSize;

    private String jdbcBatchSize;

    private String jdbcFetchSize;

    private String packagesToScan;
}
