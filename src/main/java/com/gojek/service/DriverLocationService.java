package com.gojek.service;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.gojek.domain.ErrorMessages;
import org.apache.commons.lang3.StringUtils;
import org.apache.log4j.Logger;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Component;

import java.io.IOException;
import java.util.List;


/**
 * Created by Amitesh on 09-02-2017.
 */

/**
 * This class contains methods requires to perform actions as required by Driver Location app
 */

@Component
public class DriverLocationService {

    private static Logger logger = Logger.getLogger(DriverLocationService.class);

    /**
     * Create and return ResponseEntity object from ErrorMessages object
     * @param errorMessages
     * @return ResponseEntity
     */
    public static ResponseEntity getErrorResponseEntity(ErrorMessages errorMessages) {
        List<String> errMsgs = errorMessages.getErrors();
        ObjectMapper writeMapper = new ObjectMapper();
        String errJson;
        try {
            errJson = writeMapper.writeValueAsString(errorMessages);
        } catch (IOException e) {
            logger.error("Error converting ErrorMessages to JSON: " + e.getMessage(), e);
            errJson = "\"errors\":[" + errorMessages.toString() + "]";
        }
        logger.debug("ErrorMessageJson: " + errJson);

        return new ResponseEntity<>(errJson, HttpStatus.UNPROCESSABLE_ENTITY);
    }

}
