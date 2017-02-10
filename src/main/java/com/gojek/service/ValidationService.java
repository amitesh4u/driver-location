package com.gojek.service;

import com.gojek.config.ValidationConfig;
import com.gojek.domain.DriverLocation;
import com.gojek.domain.ErrorMessages;
import com.gojek.domain.UserRequest;
import org.apache.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.util.List;

/**
 * Created by Amitesh on 09-02-2017.
 */

/**
 * This class contains methods requires to perform validation actions as required by Driver Location app
 */
@Component
public class ValidationService {

    private static Logger logger = Logger.getLogger(ValidationService.class);

    @Autowired
    private ValidationConfig vc;

    /**
     * Validate DriverLocation object fields and return Error messages object with list of error messages
     * @param location
     * @return ErrorMessages
     */
    public ErrorMessages validateData(DriverLocation location) {
        ErrorMessages errorMessages = new ErrorMessages();
        List<String> errMsgs = errorMessages.getErrors();
        Double latitude = location.getLatitude();
        logger.debug("Latitude: " + latitude);
        if(latitude == null || latitude < vc.getLatitudeMin() || latitude > vc.getLatitudeMax() ){
            errMsgs.add("Latitude should be between " +  vc.getLatitudeMin() + " and " +  vc.getLatitudeMax());
        }
        Double longitude = location.getLongitude();
        logger.debug("longitude: " + longitude);
        if(longitude == null || longitude < vc.getLongitudeMin() || longitude > vc.getLongitudeMax() ){
            errMsgs.add("Longitude should be between " +  vc.getLongitudeMin() + " and " +  vc.getLongitudeMax());
        }
        Double accuracy = location.getAccuracy();
        logger.debug("Accuracy: " + accuracy);
        if(accuracy == null || accuracy < vc.getAccuracyMin() || accuracy > vc.getAccuracyMax() ){
            errMsgs.add("Accuracy should be between " +  vc.getAccuracyMin() + " and " +  vc.getAccuracyMax());
        }
        return errorMessages;
    }

    /**
     * Validate UserRequest object fields and return Error messages object with list of error messages
     * @param userRequest
     * @return ErrorMessages
     */
    public ErrorMessages validateData(UserRequest userRequest) {
        ErrorMessages errorMessages = new ErrorMessages();
        List<String> errMsgs = errorMessages.getErrors();
        double latitude = userRequest.getLatitude();
        logger.debug("Latitude: " + latitude);
        if(latitude < vc.getLatitudeMin() || latitude > vc.getLatitudeMax() ){
            errMsgs.add("Latitude should be between " +  vc.getLatitudeMin() + " and " +  vc.getLatitudeMax());
        }
        double longitude = userRequest.getLongitude();
        logger.debug("longitude: " + longitude);
        if(longitude < vc.getLongitudeMin() || longitude > vc.getLongitudeMax() ){
            errMsgs.add("Longitude should be between " +  vc.getLongitudeMin() + " and " +  vc.getLongitudeMax());
        }

        int limit = userRequest.getLimit();
        logger.debug("Limit: " + limit);
        if(limit < 1 ){
            errMsgs.add("Limit should be greater than 0");
        }

        int radius = userRequest.getRadius();
        logger.debug("Radius: " + radius);
        if(radius < 1 ){
            errMsgs.add("Radius should be greater than 0");
        }
        return errorMessages;
    }
}
