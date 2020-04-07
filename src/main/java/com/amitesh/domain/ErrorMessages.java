package com.amitesh.domain;

import org.apache.commons.lang3.StringUtils;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by Amitesh on 09-02-2017.
 */

/**
 * This class stores Error message details
 */
public class ErrorMessages {

    List<String> errors = new ArrayList<>();

    public List<String> getErrors() {
        return errors;
    }

    public void setErrors(List<String> errors) {
        this.errors = errors;
    }

    @Override
    public String toString(){
        return "\"" + StringUtils.join(errors, "\",\"") + "\"";
    }
}
