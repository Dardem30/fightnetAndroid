package com.roman.fightnet.requests.service;


import android.util.Log;

import com.roman.fightnet.requests.ApiRequests;
import com.roman.fightnet.requests.RetrofitService;
import com.roman.fightnet.requests.models.AppUser;
import com.roman.fightnet.requests.models.Conversation;
import com.roman.fightnet.requests.models.Invite;
import com.roman.fightnet.requests.models.Marker;
import com.roman.fightnet.requests.models.Message;
import com.roman.fightnet.requests.models.SearchResponse;
import com.roman.fightnet.requests.models.forms.RequestForm;
import com.roman.fightnet.requests.models.searchCriteria.InvitesSearchCriteria;
import com.roman.fightnet.requests.models.searchCriteria.MapSearchCriteria;
import com.roman.fightnet.requests.models.searchCriteria.UserSearchCriteria;

import java.io.File;
import java.io.IOException;
import java.util.List;
import java.util.UUID;

import okhttp3.MediaType;
import okhttp3.MultipartBody;
import okhttp3.RequestBody;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class UserService {
    private final ApiRequests requests = RetrofitService.createService(ApiRequests.class);

    public Call<AppUser> findUser(final String email) {
        final RequestForm form = new RequestForm();
        form.setEmail(email);
        return requests.findUserByEmail(form);
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

    public Call<Object> uploadVideo(final File video, final String fighterInviterEmail, final String fighterInvitedEmail, final UUID id, final String fightStyle, final String token) {
        return requests.uploadVideo(new MultipartBody.Builder()
                .setType(MultipartBody.FORM)
                .addFormDataPart("file", video.getName(), RequestBody.create(MediaType.parse("multipart/form-data"), video))
                .addFormDataPart("fighterEmail1", fighterInviterEmail)
                .addFormDataPart("fighterEmail2", fighterInvitedEmail)
                .addFormDataPart("inviteId", id.toString())
                .addFormDataPart("style", fightStyle)
                .build(), token);
    }
    public Call<SearchResponse<AppUser>> listUsers(final UserSearchCriteria searchCriteria) {
        return requests.listUsers(searchCriteria);
    }

    public Call<List<Conversation>> getConversations(final String email, String token) {
        final RequestForm form = new RequestForm();
        form.setEmail(email);
        return requests.getConversations(form, token);
    }
    public Call<List<Message>> getDialog(final String userEmail, final String email, String token) {
        final RequestForm form = new RequestForm();
        form.setEmail1(userEmail);
        form.setEmail2(email);
        return requests.getDialog(form, token);
    }
    public Call<List<Marker>> getMarkers(final MapSearchCriteria searchCriteria) {
        return requests.getMarkers(searchCriteria);
    }

    public void invite(final Invite invite, final String token) {
        requests.invite(invite, token).enqueue(new Callback<Invite>() {
            @Override
            public void onResponse(Call<Invite> call, Response<Invite> response) {
                Log.i("Invite", "User successfully invited");
            }

            @Override
            public void onFailure(Call<Invite> call, Throwable t) {
                Log.e("Invite", "Error during trying to create invite", t);
            }
        });
    }

    public Call<SearchResponse<Invite>> getInvites(final InvitesSearchCriteria invitesSearchCriteria) {
        return requests.getInvites(invitesSearchCriteria);
    }

    public void acceptInvite(final Invite invite) {
        requests.acceptInvite(invite).enqueue(new Callback<Void>() {
            @Override
            public void onResponse(Call<Void> call, Response<Void> response) {
                Log.i("Invite", "User successfully accepted invite");
            }

            @Override
            public void onFailure(Call<Void> call, Throwable t) {
                Log.e("Invite", "Error during trying to accept invite", t);
            }
        });
    }

    public void declineInvite(final String id) {
        requests.declineInvite(id).enqueue(new Callback<Void>() {
            @Override
            public void onResponse(Call<Void> call, Response<Void> response) {
                Log.i("Invite", "User successfully declined invite");
            }

            @Override
            public void onFailure(Call<Void> call, Throwable t) {
                Log.e("Invite", "Error during trying to decline invite", t);
            }
        });
    }

    public Call<SearchResponse<Invite>> getPlannedFights(InvitesSearchCriteria invitesSearchCriteria) {
        return requests.getPlannedFights(invitesSearchCriteria);
    }
}
