package com.roman.fightnet.requests;


import com.roman.fightnet.requests.models.AppUser;
import com.roman.fightnet.requests.models.City;
import com.roman.fightnet.requests.models.Conversation;
import com.roman.fightnet.requests.models.Country;
import com.roman.fightnet.requests.models.Invite;
import com.roman.fightnet.requests.models.Marker;
import com.roman.fightnet.requests.models.Message;
import com.roman.fightnet.requests.models.SearchResponse;
import com.roman.fightnet.requests.models.SocketMessage;
import com.roman.fightnet.requests.models.searchCriteria.InvitesSearchCriteria;
import com.roman.fightnet.requests.models.searchCriteria.MapSearchCriteria;
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
    Call<SearchResponse<AppUser>> listUsers(@Body UserSearchCriteria searchCriteria);

    @POST("message/getConversations")
    Call<List<Conversation>> getConversations(@Body Object req, @Header("Authorization") String token);

    @POST("message/getDialog")
    Call<List<Message>> getDialog(@Body Object req, @Header("Authorization") String token);

    @POST("message/send")
    Call<Object> sendMessage(@Body SocketMessage req);

    @POST("util/getMarkers")
    Call<List<Marker>> getMarkers(@Body MapSearchCriteria searchCriteria);

    @POST("user/invite")
    Call<Invite> invite(@Body Object req, @Header("Authorization") String token);

    @POST("util/getInvitesForUser")
    Call<SearchResponse<Invite>> getInvites(@Body InvitesSearchCriteria invitesSearchCriteria);

    @POST("util/acceptInvite")
    Call<Void> acceptInvite(@Body Invite invite);

    @POST("util/declineInvite")
    Call<Void> declineInvite(@Query("inviteId") String id);

    @POST("util/getPlannedFights")
    Call<SearchResponse<Invite>> getPlannedFights(@Body InvitesSearchCriteria invitesSearchCriteria);

    @POST("user/uploadVideo")
    Call<Object> uploadVideo(@Body MultipartBody build,@Header("Authorization") String token);
}
