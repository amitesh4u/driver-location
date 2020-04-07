package com.amitesh.controller;

/**
 * Created by Amitesh on 04-02-2017.
 */

/**
 * This is the main controller class for Driver Location app
 */

import com.fasterxml.jackson.databind.ObjectMapper;
import com.amitesh.config.EnvConfig;
import com.amitesh.config.ValidationConfig;
import com.amitesh.database.DriverLocationRepository;
import com.amitesh.domain.DriverLocation;
import com.amitesh.domain.DriverLocationRequest;
import com.amitesh.domain.ErrorMessages;
import com.amitesh.domain.DriverLocationResponse;
import com.amitesh.service.DriverLocationService;
import com.amitesh.service.ValidationService;
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

import static com.amitesh.constants.RequestResponseConstants.RESPONSE_ENTITY_NOT_FOUND;
import static com.amitesh.constants.RequestResponseConstants.RESPONSE_ENTITY_OK;

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
        return "Hello. Please use # PUT /drivers/{id}/location OR # GET /drivers apis";
    }

    /**
     * This API is used to save Driver Location
     * @param idStr
     * @param locationStatus
     * @return ResponseEntity<String>
     */
    @RequestMapping(method = RequestMethod.PUT, value = "/drivers/{id}/location")
    public ResponseEntity<String> setDriverLocation(@PathVariable(value = "id") String idStr, @RequestBody String locationStatus) {
        logger.info("Request to /drivers/" + idStr + "/location with data: " + locationStatus);

        /* Verify if ID is integer */
        if (!StringUtils.isNumeric(idStr)) {
            return RESPONSE_ENTITY_NOT_FOUND;
        }
        /* Verify if the ID in under limit */
        int id = Integer.parseInt(idStr);
        if (id < vc.getDriverIdMin() || id > vc.getDriverIdMax()) {
            return RESPONSE_ENTITY_NOT_FOUND;
        }

        /* Save the JSON location details to Object */
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
        if(logger.isDebugEnabled()) logger.debug("Error messages: " + errMsgs);
        ResponseEntity responseEntity;
        if (errMsgs.isEmpty()) {
            /* Save the location details */
            driverLocation.setId(id);
            driverLocation.setAt(LocalDateTime.now());
            dlRepository.save(driverLocation);
            responseEntity = RESPONSE_ENTITY_OK;
        } else {
            responseEntity = dls.getErrorResponseEntity(errorMessages, HttpStatus.UNPROCESSABLE_ENTITY);
        }
        return responseEntity;
    }

    /**
     * This API returns near by driver location details for the Customer coordinates
     * @param latitude
     * @param longitude
     * @param radius
     * @param limit
     * @return ResponseEntity<String>
     */
    @RequestMapping(method = RequestMethod.GET, value = "/drivers")
    public ResponseEntity<String> getDrivers(@RequestParam(value = "latitude") Double latitude,
                                             @RequestParam(value = "longitude") Double longitude,
                                             @RequestParam(value = "radius", defaultValue = "500") int radius,
                                             @RequestParam(value = "limit", defaultValue = "10") int limit) {
        logger.info("Request to /drivers with input: latitude=" + latitude
                + " | longitude=" + longitude + " | radius=" + radius + " | limit=" + limit);

         /* Validate for invalid data and return errors as JSON */
        DriverLocationRequest driverLocationRequest = new DriverLocationRequest(latitude, longitude, radius, limit);
        ErrorMessages errorMessages = vs.validateData(driverLocationRequest);
        List<String> errMsgs = errorMessages.getErrors();
        if(logger.isDebugEnabled()) logger.debug("Error messages: " + errMsgs);

        ResponseEntity responseEntity;
        if (errMsgs.isEmpty()) {
            /* Fetch all applicable Driver Locations */
            List<DriverLocation> driverLocations = dls.fetchDriverLocations();
            if(logger.isDebugEnabled()) logger.debug("Driver Locations: " + driverLocations);

            /* Fetch near by driver locations */
            List<DriverLocationResponse> driverLocationResponses = dls.fetchDrivers(driverLocationRequest, driverLocations);
            if(logger.isDebugEnabled()) logger.debug("Driver Locations Responses: " + driverLocationResponses);

            /* Sort by Distance */
            dls.sortByDistance(driverLocationResponses);
            if(logger.isDebugEnabled()) logger.debug("Driver Locations Responses sorted: " + driverLocationResponses);

            /* Convert the result object to JSON */
            String driverLocationResponseJSON = dls.getDriverLocationResponseJSON(driverLocationRequest, driverLocationResponses);
            if(logger.isDebugEnabled()) logger.debug("Driver Location Response JSON: " + driverLocationResponseJSON);

            responseEntity = new ResponseEntity<>(driverLocationResponseJSON, HttpStatus.OK);
        } else {
            responseEntity = dls.getErrorResponseEntity(errorMessages, HttpStatus.BAD_REQUEST);
        }
        return responseEntity;
    }
}
