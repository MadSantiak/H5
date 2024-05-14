package com.example.gowork.Model.Workperiod;

import java.util.Date;

public class Workperiod {
    private int id;
    private double latitude;
    private double longitude;
    private String startTime;
    private String stopTime;
    private boolean atWorkplace;

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public Workperiod(double latitude, double longitude, String startTime) {
        this.latitude = latitude;
        this.longitude = longitude;
        this.startTime = startTime;
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

    public String getStartTime() {
        return startTime;
    }

    public void setStartTime(String startTime) {
        this.startTime = startTime;
    }

    public String getStopTime() {
        return stopTime;
    }

    public void setStopTime(String stopTime) {
        this.stopTime = stopTime;
    }

    public boolean isAtWorkplace() {
        return atWorkplace;
    }

    public void setAtWorkplace(boolean atWorkplace) {
        this.atWorkplace = atWorkplace;
    }

}
