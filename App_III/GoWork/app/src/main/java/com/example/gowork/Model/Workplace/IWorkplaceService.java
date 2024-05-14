package com.example.gowork.Model.Workplace;

import java.util.List;

import retrofit2.Call;
import retrofit2.http.Body;
import retrofit2.http.DELETE;
import retrofit2.http.GET;
import retrofit2.http.POST;
import retrofit2.http.PUT;
import retrofit2.http.Path;

public interface IWorkplaceService {
    @GET("workplace")
    Call<List<Workplace>> getAllWorkplace();

    @GET("workplace/{id}")
    Call<Workplace> getWorkplaceById(@Path("id") int id);

    @POST("workplace")
    Call<Integer> addWorkplace(@Body Workplace Workplace);

    @DELETE("workplace/{id}")
    Call<Void> delWorkplace(@Path("id") int id);

    @PUT("workplace")
    Call<Void> updateWorkplace(@Body Workplace Workplace);
}
