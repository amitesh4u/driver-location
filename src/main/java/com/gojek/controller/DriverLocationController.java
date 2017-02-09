package com.gojek.controller;

/**
 * Created by Amitesh on 04-02-2017.
 */

import com.gojek.config.EnvConfig;
import org.apache.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RestController;


@RestController
public class DriverLocationController {

    private Logger logger = Logger.getLogger(DriverLocationController.class);

    @Autowired
    private EnvConfig envConfig;

   @RequestMapping(method = RequestMethod.GET, value = "/")
    public void defaultMethod() {
        logger.info("This is an info log entry");
        logger.debug("This is an debug log entry");
        logger.error("This is an error log entry");

        logger.info("Request to /");
        logger.debug(envConfig.getEnv() + " | " + envConfig.getEnvText());
        System.out.println(envConfig.getEnv() + " | " + envConfig.getEnvText());
    }

}
