package com.gojek.service;

import com.gojek.config.ValidationConfig;
import com.gojek.domain.DriverLocation;
import com.gojek.domain.ErrorMessages;
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

    @Autowired
    private ValidationConfig vc;

    public ErrorMessages validateData(DriverLocation location) {
        ErrorMessages errorMessages = new ErrorMessages();
        List<String> errMsgs = errorMessages.getErrors();
        Double latitude = location.getLatitude();
        System.out.println("Latitude: " + latitude);
        if(latitude == null || latitude < vc.getLatitudeMin() || latitude > vc.getLatitudeMax() ){
            errMsgs.add("Latitude should be between +/- 90");
        }
        Double longitude = location.getLongitude();
        System.out.println("longitude: " + longitude);
        if(longitude == null || longitude < vc.getLongitudeMin() || longitude > vc.getLongitudeMax() ){
            errMsgs.add("Longitude should be between +/- 90");
        }
        Double accuracy = location.getAccuracy();
        System.out.println("accuracy: " + accuracy);
        if(accuracy == null || accuracy < vc.getAccuracyMin() || accuracy > vc.getAccuracyMax() ){
            errMsgs.add("Accuracy should be between +/- 90");
        }
        return errorMessages;
    }
}
