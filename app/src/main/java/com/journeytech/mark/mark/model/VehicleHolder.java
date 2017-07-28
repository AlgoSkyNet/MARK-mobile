package com.journeytech.mark.mark.model;

/**
 * Created by rtyJa on 26/07/2017.
 */

public class VehicleHolder {
    String vehicle = "";
    String latitude = "";
    String longitude = "";

    public VehicleHolder(String latitude, String longitude) {
        this.latitude = latitude;
        this.longitude = longitude;
    }

    public String getLatitude() {
        return latitude;
    }

    public void setLatitude(String latitude) {
        this.latitude = latitude;
    }

    public String getLongitude() {
        return longitude;
    }

    public void setLongitude(String longitude) {
        this.longitude = longitude;
    }

    public String getVehicle() {
        return vehicle ;
    }

    public void setVehicle(String vehicle) {
        this.vehicle = vehicle;
    }

/*    public VehicleHolder(String vehicle) {
        this.vehicle = vehicle;
    }*/
}
