package com.roman.fightnet.requests;


import com.roman.fightnet.requests.models.AppUser;
import com.roman.fightnet.requests.models.City;
import com.roman.fightnet.requests.models.Country;
import com.roman.fightnet.requests.models.searchCriteria.UserSearchCriteria;

import java.util.List;

import okhttp3.MultipartBody;
import retrofit2.Call;
import retrofit2.http.Body;
import retrofit2.http.GET;
import retrofit2.http.Header;
import retrofit2.http.POST;
import retrofit2.http.Query;

public interface ApiRequests {
    @POST("security/login")
    Call<Object> login(@Body Object req);

    @POST("security/sendCode")
    Call<Object> sendCode(@Body Object req);

    @POST("security/sign-up")
    Call<Object> registration(@Body Object req);

    @GET("util/getCountries")
    Call<List<Country>> getCountries();

    @GET("util/getCities")
    Call<List<City>> getCities(@Query("country") String countryName);

    @POST("util/findUser")
    Call<AppUser> findUserByEmail(@Body Object req);

    @GET("util/getFacebookAccessToken")
    Call<String> getFacebookAccessToken();

    @POST("util/updateChangableInfoToUser")
    Call<Object> updateChangableInfoToUser(@Body AppUser user);

    @POST("user/uploadPhoto")
    Call<Object> uploadPhoto(@Body MultipartBody build, @Header("Authorization") String token);

    @POST("util/listUsers")
    List<AppUser> listUsers(@Body UserSearchCriteria searchCriteria);
}
