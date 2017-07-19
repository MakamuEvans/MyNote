package com.example.elm.login.api;

import com.example.elm.login.model.Note;
import com.example.elm.login.model.User;

import org.json.JSONObject;

import retrofit2.Call;
import retrofit2.http.Body;
import retrofit2.http.Field;
import retrofit2.http.FormUrlEncoded;
import retrofit2.http.Header;
import retrofit2.http.POST;
import retrofit2.http.Query;

/**
 * Created by elm on 7/10/17.
 */

public interface ApiInterface {
    @POST("register")
    @FormUrlEncoded
    Call<User> getData(@Field("first_name") String firstname,
                       @Field("last_name") String lastname,
                       @Field("email") String email,
                       @Field("password") String password);

    @POST("login")
    @FormUrlEncoded
    Call<User> getData (@Field("email") String email,
                        @Field("password") String password);

    @POST("notes/new")
    @FormUrlEncoded
    Call<Note> getData (@Field("title") String title,
                        @Field("note") String note,
                        @Header("Authorization") String auth);
}
