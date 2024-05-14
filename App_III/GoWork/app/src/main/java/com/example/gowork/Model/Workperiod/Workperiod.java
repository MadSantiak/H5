package com.example.gowork.Model.Workperiod;

import java.util.Date;

public class Workperiod {
    private int id;
    private double latitude;
    private double longitude;
    private Date startTime;
    private Date stopTime;
    private boolean atWorkplace;

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public Workperiod(double latitude, double longitude, Date startTime) {
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

    public Date getStartTime() {
        return startTime;
    }

    public void setStartTime(Date startTime) {
        this.startTime = startTime;
    }

    public Date getStopTime() {
        return stopTime;
    }

    public void setStopTime(Date stopTime) {
        this.stopTime = stopTime;
    }

    public boolean isAtWorkplace() {
        return atWorkplace;
    }

    public void setAtWorkplace(boolean atWorkplace) {
        this.atWorkplace = atWorkplace;
    }

}
