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
import com.gojek.domain.ErrorMessages;
import com.gojek.domain.UserRequest;
import com.gojek.domain.UserResponse;
import com.gojek.service.DriverLocationService;
import com.gojek.service.ValidationService;
import com.gojek.util.GeoUtil;
import org.apache.commons.lang3.StringUtils;
import org.apache.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.io.IOException;
import java.time.LocalDateTime;
import java.time.temporal.ChronoUnit;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
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
        logger.debug("Error messages: " + errMsgs);
        ResponseEntity responseEntity = null;
        if (errMsgs.isEmpty()) {
            location.setId(id);
            location.setAt(LocalDateTime.now());
            dlRepository.save(location);
            responseEntity = RESPONSE_ENTITY_OK;
        } else {
            responseEntity = dls.getErrorResponseEntity(errorMessages);
        }
        return responseEntity;
    }

    @RequestMapping(method = RequestMethod.GET, value = "/drivers")
    public ResponseEntity<String> getDrivers(@RequestParam(value = "latitude") double latitude,
                                             @RequestParam(value = "longitude") double longitude,
                                             @RequestParam(value = "radius", defaultValue = "500") int radius,
                                             @RequestParam(value = "limit", defaultValue = "10") int limit) {
        logger.info("Request to /drivers with input: latitude=" + latitude
                + " | longitude=" + longitude + " | radius=" + radius + " | limit=" + limit);

        UserRequest userRequest = new UserRequest(latitude, longitude, radius, limit);
        ErrorMessages errorMessages = vs.validateData(userRequest);
        List<String> errMsgs = errorMessages.getErrors();

        ResponseEntity responseEntity = null;
        if (errMsgs.isEmpty()) {
            long dataFreshnessLimitInMin = envConfig.getDataFreshnessLimitInMin();
            logger.debug("Data Freshness Limit: " + dataFreshnessLimitInMin + " min");

            List<DriverLocation> driverLocations;
            if (dataFreshnessLimitInMin <= 0) {
                //Find all data
                driverLocations = dlRepository.findAll();
            } else {
                LocalDateTime timeLimit = LocalDateTime.now().minus(limit, ChronoUnit.MINUTES);
                logger.debug("Time limit above which driver's location data will be considered: " + timeLimit);
                //Find data inserted within time limit
                driverLocations = dlRepository.findByAtGreaterThan(timeLimit);
            }
            List<UserResponse> userResponses = new ArrayList<>();
            double latitude1 =  userRequest.getLatitude();
            double longitude1 =  userRequest.getLongitude();
            for (DriverLocation driverLocation : driverLocations) {
                logger.debug("Driver Location: " + driverLocation);
                double latitude2 = driverLocation.getLatitude();
                double longitude2 = driverLocation.getLongitude();
                double accuracy = driverLocation.getAccuracy();

                /* Due to accuracy there is a range of Latitude/Longitude.
                 * By picking one Latitude/Longitude for comparison, we can reduce the comparisons by 50%.
                 *
                 * Steps:
                 *  If Driver's Lat/Long is greater then User Lat/Long then use Driver's Lat/Long + Accuracy for comparison
                 *  else use Lat/Long - Accuracy for comparison
                 */
                latitude2 = (latitude2 > latitude1)? latitude2 + accuracy : latitude2 - accuracy;
                longitude2 = (longitude2 > longitude1)? longitude2 + accuracy : longitude2 - accuracy;

                /* Reset the boundary */
                latitude2 = latitude2 > vc.getLatitudeMax() ?  vc.getLatitudeMax() : latitude2;
                latitude2 = latitude2 < vc.getLatitudeMin() ?  vc.getLatitudeMin() : latitude2;
                longitude2 = longitude2 > vc.getLongitudeMax() ?  vc.getLongitudeMax(): longitude2;
                longitude2 = longitude2 < vc.getLongitudeMin() ?  vc.getLongitudeMin(): longitude2;

                logger.debug("Updated Driver's Latitude and Longitude for comparison: " + latitude2 + "|" + longitude2);

                int distance = (int) GeoUtil.distanceBetweenCoordinatesMeters(latitude1,longitude1,latitude2,longitude2);
                logger.debug("Distance between co-ordinates in meters: " + distance);
                radius = userRequest.getRadius();
                if(distance <= userRequest.getRadius()){
                    UserResponse userResponse = new UserResponse();
                    userResponse.setId(driverLocation.getId());
                    userResponse.setLatitude(latitude2);
                    userResponse.setLongitude(longitude2);
                    userResponse.setDistance(distance);

                    userResponses.add(userResponse);
                }
            }
            if (envConfig.isDriverLocationResponseSortedByDistance()) {
                userResponses.sort(Comparator.comparingDouble(UserResponse::getDistance));
                if (!envConfig.isDriverLocationResponseSortedAsc()) {
                    Collections.reverse(userResponses);
                }
            }
            ObjectMapper writeMapper = new ObjectMapper();
            String userResponseJSON;
            limit = userResponses.size() > userRequest.getLimit()? userRequest.getLimit(): userResponses.size();
            try {
                userResponseJSON = writeMapper.writeValueAsString(userResponses.subList(0,limit));
            } catch (IOException e) {
                logger.error("Error converting User Response to JSON: " + e.getMessage(), e);
                userResponseJSON = "[" + StringUtils.join(userResponses.subList(0,limit), ",") + "]";
            }
            logger.debug("UserResponseJSON: " + userResponseJSON);

            responseEntity = new ResponseEntity<>(userResponseJSON, HttpStatus.OK);
        } else {
            responseEntity = dls.getErrorResponseEntity(errorMessages);
        }
        return responseEntity;
    }


}
