package com.elm.mycheck.login;

import android.app.ProgressDialog;
import android.content.Intent;
import android.content.SharedPreferences;
import android.support.annotation.Nullable;
import android.support.design.widget.Snackbar;
import android.support.v7.app.AppCompatActivity;

import android.os.Bundle;
import android.util.Patterns;
import android.view.View;
import android.widget.TextView;
import android.widget.Toast;

import com.elm.mycheck.login.api.ApiClient;
import com.elm.mycheck.login.api.ApiInterface;
import com.elm.mycheck.login.model.User;
import com.elm.mycheck.login.network.NetworkUtil;

import java.net.SocketTimeoutException;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

/**
 * A login screen that offers login via email/password.
 */
public class LoginActivity extends AppCompatActivity {
    TextView email, password;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);
    }

    public void logIn(final View view){
        email = (TextView) findViewById(R.id.email);
        password = (TextView) findViewById(R.id.password);

        //validate section.

        //api call
        String status = NetworkUtil.getConnectivityStatusString(getBaseContext());
        if(email.getText().toString().isEmpty() || password.getText().toString().isEmpty()){
            Snackbar.make(view, "Email and/or Password required.", Snackbar.LENGTH_LONG)
                    .setAction("Action", null).show();
        }else if (!Patterns.EMAIL_ADDRESS.matcher(email.getText().toString()).matches()){
            Snackbar.make(view, "Please input a valid Email Address", Snackbar.LENGTH_LONG)
                    .setAction("Action", null).show();
        }else if (status.equals("Not connected to Internet")){
            Snackbar.make(view, "Check your Internet Connection and retry!", Snackbar.LENGTH_LONG)
                    .setAction("Action", null).show();
        }else {
            //all clear to call server api
            final ProgressDialog progressDialog = new ProgressDialog(this);
            progressDialog.setIndeterminate(true);
            progressDialog.setMessage("Please wait...");
            progressDialog.setCanceledOnTouchOutside(false);
            progressDialog.show();

            ApiInterface apiInterface = ApiClient.getClient().create(ApiInterface.class);
            Call<User> call = apiInterface.getData(email.getText().toString(), password.getText().toString());
            call.enqueue(new Callback<User>() {
                @Override
                public void onResponse(Call<User> call, Response<User> response) {
                    progressDialog.dismiss();
                    if (response.isSuccessful()) {
                        User usr = response.body();
                        if (response.body().getStatus().equals("Success")){
                            //save credentials for auth.basic api calls
                            System.out.println(response.body().getFirstname());
                            User user = new User(
                                    usr.getUserid(),
                                    usr.getFirstname(),
                                    usr.getLastname(),
                                    usr.getEmail(),
                                    usr.getPass(),
                                    null
                            );
                            user.save();
                            SharedPreferences preferences = getSharedPreferences("myPref", 0);
                            SharedPreferences.Editor editor =preferences.edit();
                            editor.putBoolean("loggedIn", true);
                            editor.commit();

                            Intent intent = new Intent(LoginActivity.this, Navigation.class);
                            startActivity(intent);
                            Toast.makeText(getBaseContext(), "Success!", Toast.LENGTH_SHORT).show();
                        }else {
                            Toast.makeText(getBaseContext(), "Invalid Credentials. Please Retry", Toast.LENGTH_SHORT).show();
                        }

                    } else if (response.code() == 401) {

                    } else {
                        // Handle other responses
                    }
                }

                @Override
                public void onFailure(Call<User> call, Throwable t) {
                    progressDialog.dismiss();
                    if (t instanceof SocketTimeoutException){
                        Snackbar.make(view, "Server TimeOut.Please retry in a moment", Snackbar.LENGTH_LONG)
                                .setAction("Action", null).show();
                    }else {
                        Snackbar.make(view, "There was an unexpected error contacting the server. Please retry after a moment.", Snackbar.LENGTH_LONG)
                                .setAction("Action", null).show();
                    }
                }
            });
        }
    }


    public void passwordReset(View view){
        Intent intent = new Intent(LoginActivity.this, PasswordReset.class);
        startActivity(intent);
    }

    public void register(View view){
        Intent intent = new Intent(LoginActivity.this, RegisterActivity.class);
        startActivity(intent);
    }
}

