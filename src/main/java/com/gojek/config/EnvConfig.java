package com.gojek.config;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.PropertySource;
import org.springframework.context.annotation.PropertySources;
import org.springframework.context.support.PropertySourcesPlaceholderConfigurer;

/**
 * Created by Amitesh on 04-02-2017.
 */

/**
 * This class loads properties from files in the given order
 */

@Configuration
@PropertySources({
        @PropertySource("classpath:common.properties"),
        @PropertySource("classpath:env.properties")
})
public class EnvConfig {

    @Value("${env:dev}")
    public String env;

    @Value("${dataFreshnessLimitInMin:0}")
    private Long dataFreshnessLimitInMin;

    @Value("${driverLocationResponseSortedByDistance:true}")
    private boolean driverLocationResponseSortedByDistance;

    @Value("${driverLocationResponseSortedAsc:true}")
    private boolean driverLocationResponseSortedAsc;

    public String getEnv() {
        return env;
    }

    public Long getDataFreshnessLimitInMin() {
        return dataFreshnessLimitInMin;
    }

    public boolean isDriverLocationResponseSortedByDistance() {
        return driverLocationResponseSortedByDistance;
    }

    public boolean isDriverLocationResponseSortedAsc() {
        return driverLocationResponseSortedAsc;
    }

    @Bean
    public static PropertySourcesPlaceholderConfigurer propertySourcesPlaceholderConfigurer() {
        PropertySourcesPlaceholderConfigurer p =  new PropertySourcesPlaceholderConfigurer();
        p.setIgnoreResourceNotFound(true);

        return p;
    }
}