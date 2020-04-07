package com.amitesh.service;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.amitesh.config.EnvConfig;
import com.amitesh.config.ValidationConfig;
import com.amitesh.database.DriverLocationRepository;
import com.amitesh.domain.DriverLocation;
import com.amitesh.domain.DriverLocationRequest;
import com.amitesh.domain.DriverLocationResponse;
import com.amitesh.domain.ErrorMessages;
import com.amitesh.util.GeoUtil;
import org.apache.commons.lang3.StringUtils;
import org.apache.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Component;

import java.io.IOException;
import java.time.LocalDateTime;
import java.time.temporal.ChronoUnit;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;


/**
 * Created by Amitesh on 09-02-2017.
 */

/**
 * This class contains methods requires to perform actions as required by Driver Location app
 */

@Component
public class DriverLocationService {

    private Logger logger = Logger.getLogger(DriverLocationService.class);

    @Autowired
    private EnvConfig envConfig;

    @Autowired
    private ValidationConfig vc;

    @Autowired
    private DriverLocationRepository dlRepository;

    /**
     * Create and return ResponseEntity object from ErrorMessages object
     * @param errorMessages
     * @return ResponseEntity
     */
    public ResponseEntity getErrorResponseEntity(ErrorMessages errorMessages, HttpStatus httpStatus) {
        ObjectMapper writeMapper = new ObjectMapper();
        String errJson;
        try {
            errJson = writeMapper.writeValueAsString(errorMessages);
        } catch (IOException e) {
            logger.error("Error converting ErrorMessages to JSON: " + e.getMessage(), e);
            errJson = "\"errors\":[" + errorMessages.toString() + "]";
        }
        if(logger.isDebugEnabled()) logger.debug("ErrorMessageJson: " + errJson);

        return new ResponseEntity<>(errJson, httpStatus);
    }


    /**
     * Take User coordinates, compare it with the driver locations and return applicable driver locations.
     * @param driverLocationRequest
     * @param driverLocations
     * @return List<DriverLocationResponse>
     */
    public List<DriverLocationResponse> fetchDrivers(DriverLocationRequest driverLocationRequest, List<DriverLocation> driverLocations) {
        List<DriverLocationResponse> driverLocationResponses = new ArrayList<>();
        int radius = driverLocationRequest.getRadius();
        double latitude1 =  driverLocationRequest.getLatitude();
        double longitude1 =  driverLocationRequest.getLongitude();

        for (DriverLocation driverLocation : driverLocations) {

            if(logger.isDebugEnabled()) logger.debug("Driver Location: " + driverLocation);

            double latitude2Orig = driverLocation.getLatitude();
            double longitude2Orig = driverLocation.getLongitude();
            double accuracy = driverLocation.getAccuracy();

            /* Due to accuracy there is a range of Latitude/Longitude.
             * By picking one Latitude/Longitude for comparison, we can reduce the comparisons by 50%.
             *
             * Steps:
             *  If Driver's Lat/Long is greater then User Lat/Long
             *  then use Driver's Lat/Long + Accuracy for comparison
             *  else use Lat/Long - Accuracy for comparison
             */
            double latitude2 = (latitude2Orig > latitude1)? latitude2Orig + accuracy : latitude2Orig - accuracy;
            double longitude2 = (longitude2Orig > longitude1)? longitude2Orig + accuracy : longitude2Orig - accuracy;

            if(logger.isDebugEnabled()) logger.debug("Updated Driver's Latitude and Longitude for comparison: " + latitude2 + "|" + longitude2);

            /* Reset the boundary */
            latitude2 = latitude2 > vc.getLatitudeMax() ?  vc.getLatitudeMax() : latitude2;
            latitude2 = latitude2 < vc.getLatitudeMin() ?  vc.getLatitudeMin() : latitude2;
            longitude2 = longitude2 > vc.getLongitudeMax() ?  vc.getLongitudeMax(): longitude2;
            longitude2 = longitude2 < vc.getLongitudeMin() ?  vc.getLongitudeMin(): longitude2;

            if(logger.isDebugEnabled()) logger.debug("Updated Driver's Latitude and Longitude after boundary reset for comparison: " + latitude2 + "|" + longitude2);

            int distance = Math.abs((int) GeoUtil.distanceBetweenCoordinatesMeters(latitude1,longitude1,latitude2,longitude2));
            if(logger.isDebugEnabled()) logger.debug("Distance between co-ordinates in meters: " + distance);

            if(distance <= radius){
                DriverLocationResponse driverLocationResponse = new DriverLocationResponse();
                driverLocationResponse.setId(driverLocation.getId());
                driverLocationResponse.setLatitude(latitude2Orig);
                driverLocationResponse.setLongitude(longitude2Orig);
                driverLocationResponse.setDistance(distance);

                driverLocationResponses.add(driverLocationResponse);
            }
        }
        return driverLocationResponses;
    }

    public void sortByDistance(List<DriverLocationResponse> driverLocationResponses) {
    /* Sort by distance */
        if (envConfig.isDriverLocationResponseSortedByDistance()) {
            driverLocationResponses.sort(Comparator.comparingDouble(DriverLocationResponse::getDistance));
            if (!envConfig.isDriverLocationResponseSortedAsc()) {
                Collections.reverse(driverLocationResponses);
            }
        }
    }


    /**
     * Convert and return Driver Location Response JSON
     * @param driverLocationRequest
     * @param driverLocationResponses
     * @return String
     */
    public String getDriverLocationResponseJSON(DriverLocationRequest driverLocationRequest, List<DriverLocationResponse> driverLocationResponses) {
        int limit;ObjectMapper writeMapper = new ObjectMapper();
        String driverLocationResponseJSON;
        /* If Driver Location list is less than req limit then return the list else return a sub list of size limit */
        limit = driverLocationResponses.size() > driverLocationRequest.getLimit()? driverLocationRequest.getLimit(): driverLocationResponses.size();
        if(logger.isDebugEnabled()) logger.debug("Updated limit: " + limit);

        try {
            driverLocationResponseJSON = writeMapper.writeValueAsString(driverLocationResponses.subList(0,limit));
        } catch (IOException e) {
            logger.error("Error converting Driver Location response to JSON: " + e.getMessage(), e);
            driverLocationResponseJSON = "[" + StringUtils.join(driverLocationResponses.subList(0,limit), ",") + "]";
        }
        return driverLocationResponseJSON;
    }


    /**
     * Fetch DriverLocation details from DB
     * @return List<DriverLocation>
     */
    public List<DriverLocation> fetchDriverLocations() {
        long dataFreshnessLimitInMin = envConfig.getDataFreshnessLimitInMin();
        if(logger.isDebugEnabled()) logger.debug("Data Freshness Limit: " + dataFreshnessLimitInMin + " min");

        List<DriverLocation> driverLocations;
        if (dataFreshnessLimitInMin <= 0) {
            //Find all data
            driverLocations = dlRepository.findAll();
        } else {
            LocalDateTime timeLimit = LocalDateTime.now().minus(envConfig.getDataFreshnessLimitInMin(), ChronoUnit.MINUTES);
            if(logger.isDebugEnabled()) logger.debug("Time limit only above which driver's location data will be considered: " + timeLimit);
            //Find data inserted within time limit
            driverLocations = dlRepository.findByAtGreaterThan(timeLimit);
        }
        return driverLocations;
    }
}
