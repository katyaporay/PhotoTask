package com.example.myapplication;

import com.example.myapplication.Models.ServerRequest;
import com.example.myapplication.Models.ServerResponse;

import retrofit2.Call;
import retrofit2.http.Body;
import retrofit2.http.POST;

public interface AuthAPI {
    @POST("http://195.19.44.146/auth/")
    Call<ServerResponse> operation(@Body ServerRequest request);
}
