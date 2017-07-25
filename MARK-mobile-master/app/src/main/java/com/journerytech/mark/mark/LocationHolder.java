package com.journerytech.mark.mark;

/**
 * Created by rtyJa on 05/07/2017.
 */

public class LocationHolder {

    private String longitude,latitude,location,direction;

    public LocationHolder(String longitude, String latitude, String location, String direction) {
        this.longitude = longitude;
        this.latitude = latitude;
        this.location = location;
        this.direction = direction;
    }

    public String getLongitude() {
        return longitude;
    }

    public void setLongitude(String longitude) {
        this.longitude = longitude;
    }

    public String getLatitude() {
        return latitude;
    }

    public void setLatitude(String latitude) {
        this.latitude = latitude;
    }

    public String getLocation() {
        return location;
    }

    public void setLocation(String location) {
        this.location = location;
    }

    public String getDirection() {
        return direction;
    }

    public void setDirection(String direction) {
        this.direction = direction;
    }
}