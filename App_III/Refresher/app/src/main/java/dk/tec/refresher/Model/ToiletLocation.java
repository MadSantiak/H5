package dk.tec.refresher.Model;

import android.location.Location;
import android.os.Build;

import androidx.annotation.RequiresApi;

import dk.tec.refresher.MainActivity;

public class ToiletLocation {
    private float direction;
    private double latitude;
    private double longitude;
    private double altitude;

    public void registerToiletLocation(Location location){
        if (location == null) return;
        direction = location.getBearing();
        latitude = location.getLatitude();
        longitude = location.getLongitude();
        altitude = location.getLatitude();
        MainActivity.toilets.add(this);
    }


    public ToiletLocation(Location location){
        if (location != null) {
            direction = location.getBearing();
            latitude = location.getLatitude();
            longitude = location.getLongitude();
            altitude = location.getAltitude();
            MainActivity.toilets.add(this);
        }
    }
    public ToiletLocation() {
    }

    public ToiletLocation(float direction, double latitude, double longitude, double altitude) {
        this.direction = direction;
        this.latitude = latitude;
        this.longitude = longitude;
        this.altitude = altitude;
    }

    public float getDirection() {
        return direction;
    }

    public void setDirection(float direction) {
        this.direction = direction;
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

    public double getAltitude() {
        return altitude;
    }

    public void setAltitude(double altitude) {
        this.altitude = altitude;
    }
}
