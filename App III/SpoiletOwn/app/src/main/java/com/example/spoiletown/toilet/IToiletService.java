package com.example.spoiletown.toilet;
import java.util.List;

import retrofit2.Call;
import retrofit2.http.Body;
import retrofit2.http.DELETE;
import retrofit2.http.GET;
import retrofit2.http.POST;
import retrofit2.http.PUT;
import retrofit2.http.Path;

public interface IToiletService {
    @GET("Toilet")
    Call<List<Toilet>> getAllToilet();

    @GET("Toilet/{id}")
    Call<Toilet> getToiletById(@Path("id") int id);

    @POST("Toilet")
    Call<Integer> addToilet(@Body Toilet toilet);

    @DELETE("Toilet/{id}")
    Call<Void> delToilet(@Path("id") int id);

    @PUT("Toilet/{id}")
    Call<Void> updateToilet(@Body Toilet toilet);
}