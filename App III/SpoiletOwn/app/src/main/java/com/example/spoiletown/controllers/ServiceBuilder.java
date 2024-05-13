package com.example.spoiletown.controllers;

import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;

public class ServiceBuilder {
    private static final String URL = "http://localhost:8080/SpoiletAPI/api/";

    private static Retrofit retrofit =
            new Retrofit.Builder().baseUrl(URL)
                    .addConverterFactory(GsonConverterFactory.create())
                    .build();

    public static <P> P buildService(Class<P> serviceType)
    {
        return retrofit.create(serviceType);
    }
}