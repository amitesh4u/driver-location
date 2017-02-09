package com.gojek;

import org.apache.log4j.Logger;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

/**
 * Created by Amitesh on 04-02-2017.
 */

@SpringBootApplication
public class DriverLocationApp {

    private static Logger logger = Logger.getLogger(DriverLocationApp.class);

    public static void main( String[] args ) {

        logger.debug("Starting Application");
        SpringApplication.run(DriverLocationApp.class, args);
    }
}
