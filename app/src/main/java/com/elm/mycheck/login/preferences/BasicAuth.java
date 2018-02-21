package com.elm.mycheck.login.preferences;

import okhttp3.Credentials;

/**
 * Created by elm on 7/11/17.
 */

public class BasicAuth {

    public String AuthorizationEncode(String email, String password){
        return Credentials.basic(email, password);
    }
}
