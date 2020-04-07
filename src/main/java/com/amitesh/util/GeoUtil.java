package com.amitesh.util;

/**
 * Created by Amitesh on 09-02-2017.
 */

/**
 * Util class to provide functions related to Geographical calculations i.e. distance between two co-ordinates.
 * <p>
 * Note:
 * a) North latitudes are Positive
 * b) South latitudes are Negative
 * c) East longitudes are Positive
 * d) West longitudes are Negative
 */

public class GeoUtil {

    private static final double EARTH_RADIUS_KM = 111.18957696;

    /**
     * This function calculates distance between two co-ordinates by
     * applying Law of cosines and return details in Kilometers:
     * a1 = lat1 (in Radian);
     * a2 = lat2 (in Radian);
     * delta = (lon1-lon2) (in Radian);
     * d = acos( sin a1 * sin a2 + cos a1 * cos a2 * cos delta ) * R (Earth Radius in KM)
     *
     * @param lat1
     * @param lon1
     * @param lat2
     * @param lon2
     * @return double
     */
    public static double distanceBetweenCoordinatesKM(double lat1, double lon1, double lat2, double lon2) {
        double lat1Rad = deg2rad(lat1), lat2Rad = deg2rad(lat2);
        double thetaRad = deg2rad(lon1 - lon2);

        double dist = (Math.sin(lat1Rad) * Math.sin(lat2Rad)) + (Math.cos(lat1Rad) * Math.cos(lat2Rad) * Math.cos((thetaRad)));
        dist = rad2deg(Math.acos(dist));
        dist *= EARTH_RADIUS_KM;

        return dist;
    }

    /**
     * This function calculates distance between two co-ordinates by
     * applying Law of cosines and return details in meters:
     * a1 = lat1 (in Radian);
     * a2 = lat2 (in Radian);
     * delta = (lon1-lon2) (in Radian);
     * d = acos( sin a1 * sin a2 + cos a1 * cos a2 * cos delta ) * R (Earth Radius in KM)
     *
     * @param lat1
     * @param lon1
     * @param lat2
     * @param lon2
     * @return double
     */
    public static double distanceBetweenCoordinatesMeters(double lat1, double lon1, double lat2, double lon2) {
        return distanceBetweenCoordinatesKM(lat1, lon1, lat2, lon2)*1000;
    }

    /**
     * This function converts decimal degrees to radians
     *
     * @param deg
     * @return double
     */
    private static double deg2rad(double deg) {
        return (deg * Math.PI / 180.0);
    }

    /**
     * This function converts radians to decimal degrees
     *
     * @param rad
     * @return double
     */

    private static double rad2deg(double rad) { return (rad * 180 / Math.PI); }
}



