package com.example.elm.login.api;

import java.io.IOException;

import okhttp3.Credentials;
import okhttp3.Interceptor;
import okhttp3.Request;
import okhttp3.Response;

/**
 * Created by elm on 7/11/17.
 */

public class BasicAuth implements Interceptor {
    private String credentials;

    public BasicAuth(String user, String password){
        this.credentials = Credentials.basic(user, password);
    }
    @Override
    public Response intercept(Chain chain) throws IOException {
        Request request = chain.request();
        Request authenticatedRequest = request.newBuilder()
                .header("Authorization", credentials).build();

        return chain.proceed(authenticatedRequest);
    }
}
