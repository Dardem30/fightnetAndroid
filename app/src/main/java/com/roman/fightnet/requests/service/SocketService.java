package com.roman.fightnet.requests.service;

import android.util.Log;

import com.roman.fightnet.requests.ApiRequests;
import com.roman.fightnet.requests.RetrofitService;
import com.roman.fightnet.requests.models.SocketMessage;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class SocketService {
    private final ApiRequests requests = RetrofitService.createService(ApiRequests.class);

    public void sendMessage(final SocketMessage message) {
        requests.sendMessage(message).enqueue(new Callback<Object>() {
            @Override
            public void onResponse(Call call, Response response) {
                Log.i("Socket", "Message is sent successfully");
            }

            @Override
            public void onFailure(Call call, Throwable t) {
                Log.e("Socket", "Failed to send message", t);
            }
        });
    }
}
