package com.example.myapplication;

import com.example.myapplication.Models.MyResponse;

import okhttp3.RequestBody;
import retrofit2.Call;
import retrofit2.http.Multipart;
import retrofit2.http.POST;
import retrofit2.http.Part;

public interface MyAPI {
    String BASE_URL = "http://195.19.44.146/letovo/";
    @Multipart
    @POST("api.php?action=do")
    Call<MyResponse> uploadImage(@Part("image\"; filename=\"test.jpg\" ")
                                 RequestBody file,
                                 @Part("desc") RequestBody desc);
}
