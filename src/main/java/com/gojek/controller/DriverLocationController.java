package com.gojek.controller;

/**
 * Created by Amitesh on 04-02-2017.
 */

/**
 * This is the main controller class for Driver Location app
 */

import com.fasterxml.jackson.databind.ObjectMapper;
import com.gojek.config.EnvConfig;
import com.gojek.config.ValidationConfig;
import com.gojek.domain.DriverLocation;
import com.gojek.domain.ErrorMessages;
import com.gojek.service.DriverLocationService;
import com.gojek.service.ValidationService;
import org.apache.commons.lang3.StringUtils;
import org.apache.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.io.IOException;
import java.util.List;

import static com.gojek.constants.RequestResponseConstants.RESPONSE_ENTITY_NOT_FOUND;
import static com.gojek.constants.RequestResponseConstants.RESPONSE_ENTITY_OK;

@RestController
public class DriverLocationController {

    private Logger logger = Logger.getLogger(DriverLocationController.class);

    @Autowired
    private EnvConfig envConfig;

    @Autowired
    private ValidationConfig vc;

    @Autowired
    private ValidationService vs;

    @Autowired
    private DriverLocationService dls;

   @RequestMapping(method = RequestMethod.GET, value = "/")
    public void defaultMethod() {
        logger.info("This is an info log entry");
        logger.debug("This is an debug log entry");
        logger.error("This is an error log entry");

        logger.info("Request to /");
        logger.debug(envConfig.getEnv()
                + " | " + envConfig.getDataFreshnessLimitInMin()
                + " | " + envConfig.isDriverLocationResponseSortedByDistance()
                + " | " + envConfig.isDriverLocationResponseSortedAsc());
    }

    @RequestMapping(method = RequestMethod.PUT, value = "/drivers/{id}/location")
    public ResponseEntity<String> setDriverLocation(@PathVariable(value = "id") String idStr, @RequestBody String locationStatus) {
        logger.info("Request to /drivers/" + idStr + "/location with data: " + locationStatus);

        if (!StringUtils.isNumeric(idStr)) {
            return RESPONSE_ENTITY_NOT_FOUND;
        }
        int id = Integer.parseInt(idStr);
        if (id < vc.getDriverIdMin() || id > vc.getDriverIdMax()) {
            return RESPONSE_ENTITY_NOT_FOUND;
        }

        //JSON from String to Object
        ObjectMapper readMapper = new ObjectMapper();
        DriverLocation location;
        try {
            location = readMapper.readValue(locationStatus, DriverLocation.class);
        } catch (IOException e) {
            logger.error("Error converting Location status JSON to Object: " + e.getMessage());
            return new ResponseEntity<>("{\"errors\": [\"Invalid fields in body: Use Latitude,Longitude and Accuracy\"]}", HttpStatus.UNPROCESSABLE_ENTITY);
        }

        /* Validate for invalid data and return errors as JSON */
        ErrorMessages errorMessages = vs.validateData(location);
        List<String> errMsgs = errorMessages.getErrors();

        ResponseEntity responseEntity = null;
        if (errMsgs.isEmpty()) {
            responseEntity = RESPONSE_ENTITY_OK;
        } else {
            responseEntity = dls.getErrorResponseEntity(errorMessages);
        }
        return responseEntity;
    }

}
