package com.example.gowork.Controllers;

import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;

public class ServiceBuilder {
    // From School:
    //private static final String URL = "http://192.168.0.157:8080/";

    // From home:
    private static final String URL = "http://192.168.1.145:8080/";

    private static Retrofit retrofit =
            new Retrofit.Builder().baseUrl(URL)
                    .addConverterFactory(GsonConverterFactory.create())
                    .build();

    public static <P> P buildService(Class<P> serviceType)
    {
        return retrofit.create(serviceType);
    }
}