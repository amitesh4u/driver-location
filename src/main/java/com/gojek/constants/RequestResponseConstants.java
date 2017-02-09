package com.gojek.constants;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;

/**
 * Created by Amitesh on 09-02-2017.
 */
public final class RequestResponseConstants {

    public static final ResponseEntity RESPONSE_ENTITY_NOT_FOUND = new ResponseEntity<>("{}", HttpStatus.NOT_FOUND);
    public static final ResponseEntity RESPONSE_ENTITY_OK = new ResponseEntity<>("{}",HttpStatus.OK);

}
