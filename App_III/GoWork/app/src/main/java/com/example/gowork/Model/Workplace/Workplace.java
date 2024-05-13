package com.example.gowork.Model.Workplace;

public class Workplace {
    private static Workplace instance;
    private String name;
    private float longitude;
    private float latitude;

    public Workplace(String name, float longitude, float latitude) {
        this.name = name;
        this.longitude = longitude;
        this.latitude = latitude;
    }

    public static void setInstance(Workplace instance) {
        Workplace.instance = instance;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public float getLongitude() {
        return longitude;
    }

    public void setLongitude(float longitude) {
        this.longitude = longitude;
    }

    public float getLatitude() {
        return latitude;
    }

    public void setLatitude(float latitude) {
        this.latitude = latitude;
    }




}
