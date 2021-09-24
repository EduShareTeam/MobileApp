package com.fatihbaser.edusharedemo.providers;

import com.fatihbaser.edusharedemo.Retrofit.IFCMApi;
import com.fatihbaser.edusharedemo.Retrofit.RetrofitClient;
import com.fatihbaser.edusharedemo.models.FCMBody;
import com.fatihbaser.edusharedemo.models.FCMResponse;

import retrofit2.Call;

public class NotificationProvider {

    private String url = "https://fcm.googleapis.com";

    public NotificationProvider() {

    }

    public Call<FCMResponse> sendNotification(FCMBody body) {
        return RetrofitClient.getClient(url).create(IFCMApi.class).send(body);
    }
}
