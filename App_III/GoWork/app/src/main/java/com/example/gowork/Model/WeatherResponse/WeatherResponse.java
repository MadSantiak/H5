package com.example.gowork.Model.WeatherResponse;

import com.google.gson.annotations.SerializedName;

public class WeatherResponse {
    @SerializedName("main")
    private WeatherMainInfo mainInfo;

    public WeatherMainInfo getMainInfo() {
        return mainInfo;
    }

    public class WeatherMainInfo {
        @SerializedName("temp")
        private double temperature;

        public double getTemperature() {
            return temperature;
        }
    }
}
