package com.example.gowork.Model.Workperiod;

import retrofit2.Call;
import retrofit2.http.*;

import java.util.List;

public interface IWorkperiodService {
    @GET("workperiod")
    Call<List<Workperiod>> getAllWorkperiod();

    @GET("workperiod/{id}")
    Call<Workperiod> getWorkperiodById(@Path("id") int id);

    @POST("workperiod")
    Call<Integer> addWorkperiod(@Body Workperiod Workperiod);

    @DELETE("workperiod/{id}")
    Call<Void> delWorkperiod(@Path("id") int id);

    @PUT("workperiod")
    Call<Void> updateWorkperiod(@Body Workperiod Workperiod);
}
