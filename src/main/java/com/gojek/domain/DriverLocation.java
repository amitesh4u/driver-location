package com.gojek.domain;

import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.Document;

import java.time.LocalDateTime;

/**
 * Created by Amitesh on 09-02-2017.
 */

/**
 * This class stores Driver location details
 */
@Document
public class DriverLocation {

    @Id
    private int id;
    /* Keep type as Object. In case of primitive,
     *  jackson will assign default value 0.0
     *  if the keys are not present in JSON
      */
    private Double latitude, longitude, accuracy;
    private LocalDateTime at;

    // Required for jackson 2 to convert from Json string to Object
    public DriverLocation() {}

    public DriverLocation(int id, Double latitude, Double longitude, Double accuracy, LocalDateTime at) {
        this.id = id;
        this.latitude = latitude;
        this.longitude = longitude;
        this.accuracy = accuracy;
        this.at = at;
    }

    public int getId() {
        return id;
    }

    public Double getLatitude() {
        return latitude;
    }

    public Double getLongitude() {
        return longitude;
    }

    public Double getAccuracy() {
        return accuracy;
    }

    public void setLatitude(Double latitude) {
        this.latitude = latitude;
    }

    public void setLongitude(Double longitude) {
        this.longitude = longitude;
    }

    public void setAccuracy(Double accuracy) {
        this.accuracy = accuracy;
    }

    public void setId(int id) {
        this.id = id;
    }

    public LocalDateTime getAt() {
        return at;
    }

    public void setAt(LocalDateTime at) {
        this.at = at;
    }

    @Override
    public String toString() {
        return "{" +
                "id=" + id +
                ", latitude=" + latitude +
                ", longitude=" + longitude +
                ", accuracy=" + accuracy +
                ", at=\"" + at + '\"' +
                '}';
    }
}
