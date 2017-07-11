package com.example.elm.login.api;

import okhttp3.OkHttpClient;
import okhttp3.logging.HttpLoggingInterceptor;
import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;

import static okhttp3.logging.HttpLoggingInterceptor.*;

/**
 * Created by elm on 7/10/17.
 */

public class ApiClient {
    public static final String BASE_URL = "http://192.168.43.175/mynote/public/api/";
    private static Retrofit retrofit=null;

    static Level level= Level.BODY;
    static HttpLoggingInterceptor interceptor= new HttpLoggingInterceptor()
            .setLevel(level);
    static OkHttpClient client2 = new OkHttpClient.Builder()
            .addInterceptor(interceptor)
            .build();

    static OkHttpClient client = new OkHttpClient.Builder()
            .addInterceptor(new BasicAuth("mk@g.c", "juja1994"))
            .build();

    public static Retrofit getClient(){
        if (retrofit==null){
            retrofit = new Retrofit.Builder()
                    .baseUrl(BASE_URL)
                    .client(client2)
                    .addConverterFactory(GsonConverterFactory.create())
                    .build();
        }
        return retrofit;
    }
}
