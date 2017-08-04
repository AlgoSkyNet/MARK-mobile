package com.journeytech.mark.mark.model;

/**
 * Created by rtyJa on 05/07/2017.
 */

public class LocationHolder {

    private String longitude,latitude,location,remarks;

    public LocationHolder(String latitude, String longitude, String location, String remarks) {
        this.longitude = longitude;
        this.latitude = latitude;
        this.location = location;
        this.remarks = remarks;
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

    public String getRemarks() {
        return remarks;
    }

    public void setRemarks(String remarks) {
        this.remarks = remarks;
    }

}