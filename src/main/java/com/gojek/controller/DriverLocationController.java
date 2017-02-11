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
import com.gojek.database.DriverLocationRepository;
import com.gojek.domain.DriverLocation;
import com.gojek.domain.DriverLocationRequest;
import com.gojek.domain.ErrorMessages;
import com.gojek.domain.DriverLocationResponse;
import com.gojek.service.DriverLocationService;
import com.gojek.service.ValidationService;
import org.apache.commons.lang3.StringUtils;
import org.apache.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.io.IOException;
import java.time.LocalDateTime;
import java.time.temporal.ChronoUnit;
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

    @Autowired
    private DriverLocationRepository dlRepository;

   @RequestMapping("/")
    public String defaultMethod() {
        logger.info("Request to /");
        return "Hello. Please use # PUT /drivers/{id}/location  OR # GET /drivers apis ";
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
        DriverLocation driverLocation;
        try {
            driverLocation = readMapper.readValue(locationStatus, DriverLocation.class);
        } catch (IOException e) {
            logger.error("Error converting Location status JSON to Object: " + e.getMessage());
            return new ResponseEntity<>("{\"errors\": [\"Invalid fields in body: Use Latitude,Longitude and Accuracy\"]}", HttpStatus.UNPROCESSABLE_ENTITY);
        }

        /* Validate for invalid data and return errors as JSON */
        ErrorMessages errorMessages = vs.validateData(driverLocation);
        List<String> errMsgs = errorMessages.getErrors();
        logger.debug("Error messages: " + errMsgs);
        ResponseEntity responseEntity;
        if (errMsgs.isEmpty()) {
            driverLocation.setId(id);
            driverLocation.setAt(LocalDateTime.now());
            dlRepository.save(driverLocation);
            responseEntity = RESPONSE_ENTITY_OK;
        } else {
            responseEntity = dls.getErrorResponseEntity(errorMessages, HttpStatus.UNPROCESSABLE_ENTITY);
        }
        return responseEntity;
    }

    @RequestMapping(method = RequestMethod.GET, value = "/drivers")
    public ResponseEntity<String> getDrivers(@RequestParam(value = "latitude") Double latitude,
                                             @RequestParam(value = "longitude") Double longitude,
                                             @RequestParam(value = "radius", defaultValue = "500") int radius,
                                             @RequestParam(value = "limit", defaultValue = "10") int limit) {
        logger.info("Request to /drivers with input: latitude=" + latitude
                + " | longitude=" + longitude + " | radius=" + radius + " | limit=" + limit);

        DriverLocationRequest driverLocationRequest = new DriverLocationRequest(latitude, longitude, radius, limit);
        ErrorMessages errorMessages = vs.validateData(driverLocationRequest);
        List<String> errMsgs = errorMessages.getErrors();
        logger.debug("Error messages: " + errMsgs);

        ResponseEntity responseEntity;
        if (errMsgs.isEmpty()) {
            List<DriverLocation> driverLocations = dls.fetchDriverLocations();

            List<DriverLocationResponse> driverLocationResponses = dls.fetchDrivers(driverLocationRequest, driverLocations);

            String driverLocationResponseJSON = dls.getDriverLocationResponseJSON(driverLocationRequest, driverLocationResponses);

            responseEntity = new ResponseEntity<>(driverLocationResponseJSON, HttpStatus.OK);
        } else {
            responseEntity = dls.getErrorResponseEntity(errorMessages, HttpStatus.BAD_REQUEST);
        }
        return responseEntity;
    }
}
