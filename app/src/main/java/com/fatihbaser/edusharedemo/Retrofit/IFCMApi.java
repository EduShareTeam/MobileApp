package com.fatihbaser.edusharedemo.Retrofit;

import com.fatihbaser.edusharedemo.models.FCMBody;
import com.fatihbaser.edusharedemo.models.FCMResponse;

import retrofit2.Call;
import retrofit2.http.Body;
import retrofit2.http.Headers;
import retrofit2.http.POST;

public interface IFCMApi {

    @Headers({
            "Content-Type:application/json",
            "Authorization:key=AAAAAwzMup8:APA91bGJCvBi1JvcEbG1eqBZDu-hLsR5pzw1Xzq6kQcHT9WB_VX1eele8H7IOeKOiRms1Vi2fSOWWxCLQq1VNuO1sNUbm7oOCkEqTfAiypAdbwbTF0SRXnhcwAN79ZYJssk4INd2bU79"
    })
    @POST("fcm/send")
    Call<FCMResponse> send(@Body FCMBody body);
}
