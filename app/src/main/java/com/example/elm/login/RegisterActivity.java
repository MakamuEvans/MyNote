package com.example.elm.login;

import android.provider.Settings;
import android.support.design.widget.Snackbar;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.MenuItem;
import android.view.View;
import android.widget.EditText;

import com.example.elm.login.api.ApiClient;
import com.example.elm.login.api.ApiInterface;
import com.example.elm.login.model.User;
import com.example.elm.login.network.NetworkUtil;

import org.json.JSONException;
import org.json.JSONObject;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class RegisterActivity extends AppCompatActivity {

    EditText first_name,last_name,email,password,confirm_password;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_register);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item){
        switch (item.getItemId()) {
            case android.R.id.home:
                //Write your logic here
                this.finish();
                return true;
            default:
                return super.onOptionsItemSelected(item);
        }
    }

    public void createAccount(final View view){
        //get all fields
        first_name = (EditText) findViewById(R.id.first_name);
        last_name = (EditText) findViewById(R.id.last_name);
        email = (EditText) findViewById(R.id.email_address);
        password = (EditText) findViewById(R.id.password);
        confirm_password = (EditText) findViewById(R.id.password_confirm);

        //validate
        if (first_name.getText().toString().isEmpty() || last_name.getText().toString().isEmpty()|| email.getText().toString().isEmpty() ||
                password.getText().toString().isEmpty() || confirm_password.getText().toString().isEmpty()){
            Snackbar.make(view, "No field(s) should be left BLANK.", Snackbar.LENGTH_LONG)
                    .setAction("Action", null).show();
        }else {
            //check password
            if (!password.getText().toString().equals(confirm_password.getText().toString())){
                Snackbar.make(view, "Passwords DO NOT MATCH, please retry", Snackbar.LENGTH_LONG)
                        .setAction("Action", null).show();
            }else {
                //all good
                //check if there is internet access
                String status = NetworkUtil.getConnectivityStatusString(getBaseContext());
                if (status.equals("Not connected to Internet")){
                    Snackbar.make(view, "Check your Inernet Connection and retry!", Snackbar.LENGTH_LONG)
                            .setAction("Action", null).show();
                }else {
                    //all clear to call server api

                    ApiInterface apiInterface = ApiClient.getClient().create(ApiInterface.class);
                    Call<User> call = apiInterface.getData(first_name.getText().toString());
                    call.enqueue(new Callback<User>() {
                        @Override
                        public void onResponse(Call<User> call, Response<User> response) {
                            User user = response.body();
                            System.out.println(user.getEmail());
                            Snackbar.make(view,user.getFirstname() , Snackbar.LENGTH_LONG)
                                    .setAction("Action", null).show();
                        }

                        @Override
                        public void onFailure(Call<User> call, Throwable t) {
                            Snackbar.make(view, "wtf", Snackbar.LENGTH_LONG)
                                    .setAction("Action", null).show();
                        }
                    });
                }

            }

        }


    }
}
