package com.gojek.config;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.PropertySource;
import org.springframework.context.annotation.PropertySources;

/**
 * Created by Amitesh on 09-02-2017.
 */


/**
 * This class loads validation related properties (validation.XXX) from files in the given order
 */

@Configuration
@ConfigurationProperties(prefix = "validation")
@PropertySources({
        @PropertySource("classpath:common.properties"),
        @PropertySource("classpath:env.properties")
})
public class ValidationConfig {

    @Value("${driverIdMin:1}")
    private int driverIdMin=1;
    @Value("${driverIdMax:50000}")
    private int driverIdMax=50000;

    @Value("${latitudeMin:-90}")
    private double latitudeMin=-90;
    @Value("${latitudeMax:90}")
    private double latitudeMax=90;

    @Value("${longitudeMin:-90}")
    private double longitudeMin=-90;
    @Value("${longitudeMax:90}")
    private double longitudeMax=90;

    @Value("${accuracyMin:-90}")
    private double accuracyMin=-90;
    @Value("${accuracyMax:90}")
    private double accuracyMax=90;

    public int getDriverIdMin() {
        return driverIdMin;
    }

    public int getDriverIdMax() {
        return driverIdMax;
    }

    public double getLatitudeMin() {
        return latitudeMin;
    }

    public double getLatitudeMax() {
        return latitudeMax;
    }

    public double getLongitudeMin() {
        return longitudeMin;
    }

    public double getLongitudeMax() {
        return longitudeMax;
    }

    public double getAccuracyMin() {
        return accuracyMin;
    }

    public double getAccuracyMax() {
        return accuracyMax;
    }
}
