package com.amitesh;

import org.apache.log4j.Logger;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

/**
 * Created by Amitesh on 04-02-2017.
 */

@SpringBootApplication
/* Single @SpringBootApplication is equivalent to @Configuration, @EnableAutoConfiguration, @ComponentScan */
public class DriverLocationApp {

    private static Logger logger = Logger.getLogger(DriverLocationApp.class);

    public static void main( String[] args ) {

        logger.info("Starting Application");
        SpringApplication.run(DriverLocationApp.class, args);
    }
}
