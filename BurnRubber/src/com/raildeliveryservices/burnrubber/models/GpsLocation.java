package com.raildeliveryservices.burnrubber.models;

public class GpsLocation {

    public static double latitude;
    public static double longitude;
    public static double bearing;
    public static double altitude;
    public static double speed;
    public static String address;

    public static String getLocationString() {

        String locationResult = "";

        if (latitude != 0 && longitude != 0) {
            locationResult += "Latitude:" + latitude + ";";
            locationResult += "Longitude:" + longitude + ";";
            locationResult += "Bearing:" + bearing + ";";
            locationResult += "Altitude:" + altitude + ";";
            locationResult += "Speed:" + speed + ";";
            locationResult += "Address:" + address + ";";
        }
        return locationResult;
    }
}
