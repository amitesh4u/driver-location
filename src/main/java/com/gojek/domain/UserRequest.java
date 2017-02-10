package com.gojek.domain;

/**
 * Created by Amitesh on 09-02-2017.
 */

/**
 * This class store details of User Request for near by Drivers
 */
public class UserRequest {

    private int radius, limit;
    private double latitude, longitude;

    public UserRequest(double latitude, double longitude, int radius, int limit){
        this.latitude = latitude;
        this.longitude = longitude;
        this.radius = radius;
        this.limit = limit;
    }

    public int getRadius() {
        return radius;
    }

    public int getLimit() {
        return limit;
    }

    public double getLatitude() {
        return latitude;
    }

    public double getLongitude() {
        return longitude;
    }

    @Override
    public String toString() {
        return "UserRequest{" +
                "latitude=\"" + latitude + '\"' +
                ", longitude=\"" + longitude + '\"' +
                ", radius=\"" + radius + '\"' +
                ", limit=\"" + limit + '\"' +
                '}';
    }
}