package com.example.gowork.Model.WorkPeriod;

import java.util.Date;

public class WorkPeriod {
    private double latitude;
    private double longitude;

    public WorkPeriod(double latitude, double longitude, Date startTime) {
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

    public float getDistance() {
        return distance;
    }

    public void setDistance(float distance) {
        this.distance = distance;
    }

    private Date startTime;
    private Date stopTime;
    private boolean atWorkplace;
    private float distance;
}
