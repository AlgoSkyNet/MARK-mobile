package com.journeytech.mark.mark;

/**
 * Created by rtyJa on 28/07/2017.
 */

public class VehicleListMap {
    String lati = "";
    String longi = "";

    public String getLati() {
        return lati;
    }

    public void setLati(String lati) {
        this.lati = lati;
    }

    public String getLongi() {
        return longi;
    }

    public void setLongi(String longi) {
        this.longi = longi;
    }

    String date = "";
    String time = "";
    String engine = "";
    String remarks = "";

    String getter = "";

/*    public VehicleListMap(String lat, String lng, String date, String time, String engine, String remarks) {
        this.lati = lat;
        this.longi = lng;
        this.date = date;
        this.time = time;
        this.engine = engine;
        this.remarks = remarks;
    }*/

    public String getGetter() {
        return getter;
    }

    public void setGetter(String getter) {
        this.getter = getter;
    }

    public String getDate() {
        return date;
    }

    public void setDate(String date) {
        this.date = date;
    }

    public String getTime() {
        return time;
    }

    public void setTime(String time) {
        this.time = time;
    }

    public String getEngine() {
        return engine;
    }

    public void setEngine(String engine) {
        this.engine = engine;
    }

    public String getRemarks() {
        return remarks;
    }

    public void setRemarks(String remarks) {
        this.remarks = remarks;
    }
}
