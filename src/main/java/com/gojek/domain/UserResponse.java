package com.gojek.domain;

/**
 * Created by Amitesh on 09-02-2017.
 */

/**
 * This class stores User Response details which will be sent back to Customer
 */
public class UserResponse {

    private int id, distance;
    private double latitude, longitude;

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public double getLatitude() {
        return latitude;
    }

    public void setLatitude(double latitude) {
        this.latitude = latitude;
    }

    public double getLongitude() {
        return longitude;
    }

    public void setLongitude(double longitude) {
        this.longitude = longitude;
    }

    public int getDistance() {
        return distance;
    }

    public void setDistance(int distance) {
        this.distance = distance;
    }

    @Override
    public String toString() {
        return "UserResponse{" +
                "id=\"" + id + '\"' +
                ", latitude=\"" + latitude + '\"' +
                ", longitude=\"" + longitude + '\"' +
                ", distance=\"" + distance + '\"' +
                '}';
    }
}
