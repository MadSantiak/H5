package com.example.spoiletown.toilet;

import java.io.Serializable;

public class Toilet implements Serializable {
    int id;
    double longitude;
    double latitude;
    double altitude;
    float bearing;
    boolean favorite;


    public Toilet(double longitude, double latitude, double altitude, float bearing, boolean favorite) {
        this.longitude = longitude;
        this.latitude = latitude;
        this.altitude = altitude;
        this.bearing = bearing;
        this.favorite = favorite;
    }

    public boolean isFavorite() {
        return favorite;
    }

    public void setFavorite(boolean favorite) {
        this.favorite = favorite;
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public double getLongitude() {
        return longitude;
    }

    public void setLongitude(double longitude) {
        this.longitude = longitude;
    }

    public double getLatitude() {
        return latitude;
    }

    public void setLatitude(double latitude) {
        this.latitude = latitude;
    }

    public double getAltitidue() {
        return altitude;
    }

    public void setAltitidue(double altitidue) {
        this.altitude = altitidue;
    }

    public float getBearing() {
        return bearing;
    }

    public void setBearing(float bearing) {
        this.bearing = bearing;
    }
}
