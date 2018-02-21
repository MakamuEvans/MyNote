package com.elm.mycheck.login.api;

import com.elm.mycheck.login.model.Note;
import com.elm.mycheck.login.model.User;

import okhttp3.MultipartBody;
import retrofit2.Call;
import retrofit2.http.Field;
import retrofit2.http.FormUrlEncoded;
import retrofit2.http.GET;
import retrofit2.http.Header;
import retrofit2.http.Multipart;
import retrofit2.http.POST;
import retrofit2.http.Part;
import retrofit2.http.Path;
import retrofit2.http.Query;

/**
 * Created by elm on 7/10/17.
 */

public interface ApiInterface {
    //register endpoint
    @POST("register")
    @FormUrlEncoded
    Call<User> getData(@Field("first_name") String firstname,
                       @Field("last_name") String lastname,
                       @Field("email") String email,
                       @Field("password") String password);

    //login endpoint
    @POST("login")
    @FormUrlEncoded
    Call<User> getData (@Field("email") String email,
                        @Field("password") String password);

    //new note endpoint -->with pic
    @POST("notes/new")
    @Multipart
    Call<Note> getData (@Part MultipartBody.Part title,
                        @Part MultipartBody.Part note,
                        @Header("Authorization") String auth,
                        @Part MultipartBody.Part file);

    //new note endpoint -->without pic
    @POST("notes/new")
    @FormUrlEncoded
    Call<Note> getData (@Field("title") String title,
                        @Field("note") String note,
                        @Header("Authorization") String auth);

    //update note endpoint -->with pic
    @POST("notes/update")
    @Multipart
    Call<Note> updateData (@Part MultipartBody.Part serverId,
                           @Part MultipartBody.Part title,
                           @Part MultipartBody.Part note,
                           @Header("Authorization") String auth,
                           @Part MultipartBody.Part file);

    //update endpoint -->without pic
    @POST("notes/update")
    @FormUrlEncoded
    Call<Note> updateData (@Field("serverId") String serverId,
                        @Field("title") String title,
                        @Field("note") String note,
                        @Header("Authorization") String auth);

    //delete note endpoint
    @GET("notes/delete/{id}")
    Call<Note> deleteNote (@Path("id") int serverId,
                           @Header("Authorization") String auth);

    //fetch all notes
    @GET("notes/all")
    Call<Note> getNotes();

    //fetch updates
    @GET("notes/updates")
    Call<Note> getUpdates();

    //favourite
    @GET("notes/favourite/{id}")
    Call<Note> favourite(@Path("id") int serverId,
                         @Query("status") Boolean status,
                         @Header("Authorization") String auth);
}
