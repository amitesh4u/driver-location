package com.amitesh.domain;

/**
 * Created by Amitesh on 09-02-2017.
 */

/**
 * This class store details of Driver Location Request for near by Drivers
 */
public class DriverLocationRequest {

    private int radius, limit;
    private double latitude, longitude;

    public DriverLocationRequest(double latitude, double longitude, int radius, int limit){
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
        return "{" +
                "latitude=" + latitude +
                ", longitude=" + longitude +
                ", radius=" + radius +
                ", limit=" + limit +
                '}';
    }
}
