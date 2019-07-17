package com.roman.fightnet.requests.service;


import com.google.gson.Gson;
import com.roman.fightnet.requests.ApiRequests;
import com.roman.fightnet.requests.RetrofitService;
import com.roman.fightnet.requests.models.City;
import com.roman.fightnet.requests.models.Country;
import com.roman.fightnet.requests.service.util.UtilService;

import java.util.List;

import retrofit2.Call;


public class AuthService {
    private final ApiRequests requests = RetrofitService.createService(ApiRequests.class);
    private final Gson gson = new Gson();
    public final Call<Object> login(final String email, final String password) {
        return requests.login(gson.fromJson("{'email': " + email + ", 'password': " + password + "}", Object.class));
    }

    public Call<List<Country>> getCountries() {
        return requests.getCountries();
    }

    public Call<List<City>> getCities(final String country) {
        return requests.getCities(country);
    }

    public Call<Object> sendCode(final Object o, final Object... user) {
        return requests.sendCode(gson.fromJson(UtilService.createJson(o, user), Object.class));
    }
    public Call<Object> registration(final String email, final String code) {
        return requests.registration(gson.fromJson("{'email': " + email + ", 'code': " + code + "}", Object.class));
    }


}
