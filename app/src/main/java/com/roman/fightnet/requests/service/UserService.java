package com.roman.fightnet.requests.service;


import com.google.gson.Gson;
import com.roman.fightnet.requests.ApiRequests;
import com.roman.fightnet.requests.RetrofitService;
import com.roman.fightnet.requests.models.AppUser;
import com.roman.fightnet.requests.models.SearchResponse;
import com.roman.fightnet.requests.models.searchCriteria.UserSearchCriteria;

import java.io.File;
import java.io.IOException;

import okhttp3.MediaType;
import okhttp3.MultipartBody;
import okhttp3.RequestBody;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class UserService {
    private final ApiRequests requests = RetrofitService.createService(ApiRequests.class);
    private final Gson gson = new Gson();

    public Call<AppUser> findUser(final String email) {
        return requests.findUserByEmail(gson.fromJson("{'email': " + email + "}", Object.class));
    }

    public Call<String> getFacebookToken() {
        return requests.getFacebookAccessToken();
    }

    public void updateChangableInfoToUser(final AppUser user) throws IOException {
        requests.updateChangableInfoToUser(user).enqueue(new Callback<Object>() {
            @Override
            public void onResponse(Call call, Response response) {

            }

            @Override
            public void onFailure(Call call, Throwable t) {

            }
        });
    }

    public Call<Object> uploadPhoto(final File photo, final String email, final String token) {
        return requests.uploadPhoto(new MultipartBody.Builder()
                .setType(MultipartBody.FORM)
                .addFormDataPart("file", photo.getName(), RequestBody.create(MediaType.parse("multipart/form-data"), photo))
                .addFormDataPart("email", email)
                .build(), token);
    }
    public Call<SearchResponse<AppUser>> listUsers(final UserSearchCriteria searchCriteria) {
        return requests.listUsers(searchCriteria);
    }
}
