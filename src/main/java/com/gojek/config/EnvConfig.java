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

@Configuration
@PropertySources({
        @PropertySource("classpath:common.properties"),
        @PropertySource("classpath:env.properties")
})
public class EnvConfig {

    @Value("${env:dev}")
    public String env;

    @Value("${envText:dev}")
    public String envText;

   public String getEnv() {
        return env;
    }

    public String getEnvText() {
        return envText;
    }

    @Bean
    public static PropertySourcesPlaceholderConfigurer propertySourcesPlaceholderConfigurer() {
        PropertySourcesPlaceholderConfigurer p =  new PropertySourcesPlaceholderConfigurer();
        p.setIgnoreResourceNotFound(true);

        return p;
    }
}