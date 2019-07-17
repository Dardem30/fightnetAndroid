package com.roman.fightnet.requests;


import com.google.gson.Gson;
import com.google.gson.GsonBuilder;

import okhttp3.OkHttpClient;
import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;

public class RetrofitService {

    public static <S> S createService(Class<S> serviceClass){
        Gson gson = new GsonBuilder()
                .setLenient()
                .create();
        Retrofit.Builder sBuilder =
                new Retrofit.Builder()
                        .baseUrl("https://fightnet.herokuapp.com/")
                        .addConverterFactory(GsonConverterFactory.create(gson));
        Retrofit retrofit = sBuilder
                .client(new OkHttpClient.Builder().build())
                .build();
        return retrofit.create(serviceClass);
    }


}