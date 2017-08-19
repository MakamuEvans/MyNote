package com.example.elm.login.services.note;

import android.app.IntentService;
import android.content.Intent;
import android.support.annotation.Nullable;
import android.util.Log;

import com.example.elm.login.api.ApiClient;
import com.example.elm.login.api.ApiInterface;
import com.example.elm.login.model.Note;
import com.example.elm.login.model.User;
import com.google.gson.Gson;
import com.orm.query.Condition;
import com.orm.query.Select;

import java.io.File;
import java.util.ArrayList;
import java.util.List;

import layout.NotesFragment;
import okhttp3.Credentials;
import okhttp3.MediaType;
import okhttp3.MultipartBody;
import okhttp3.RequestBody;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

/**
 * Created by elm on 7/29/17.
 */

public class SyncFavourite extends IntentService {
    private List<Note> notes=new ArrayList<>();
    private int count = 0;
    /**
     * Creates an IntentService.  Invoked by your subclass's constructor.
     *
     */
    public SyncFavourite() {
        super("SyncFavourite");
    }

    @Override
    protected void onHandleIntent(@Nullable Intent intent) {
        Log.e("huh", "service startedd");
        notes = Select.from(Note.class)
                .where(Condition.prop("favaouriteflag").eq(1))
                .list();

        Log.e("ai", notes.toString());
        count = notes.size();
        if (count >0){
            upload();
        }
    }

    public Integer upload(){
        Log.e("started", "yes");
        final Note note = Select.from(Note.class)
                .where(Condition.prop("favaouriteflag").eq(1))
                .first();

        if (note.getNoteid() == null){
            return 0;
        }
        System.out.println(note);
        User user = Select.from(User.class)
                .first();
        String credentials = Credentials.basic(user.getEmail(), user.getPass());

        ApiInterface apiInterface = ApiClient.getClient().create(ApiInterface.class);
        Call<Note> call = apiInterface.favourite(
                Integer.parseInt(note.getNoteid()),
                note.getFavourite(),
                credentials
        );
        call.enqueue(new Callback<Note>() {
            @Override
            public void onResponse(Call<Note> call, Response<Note> response) {
                Log.e("er", "returnedd");
                Note data = response.body();
                System.out.println(data.toString());
                if (response.isSuccessful()) {
                    note.setFavaouriteflag(false);
                    note.save();

                    //broadcast
                    String dt = new Gson().toJson(note);
                    Intent intent1 = new Intent();
                    intent1.setAction(NotesFragment.SyncReceiver.SYNC_ACTION);
                    intent1.putExtra("note", dt);
                    sendBroadcast(intent1);

                    notes = Select.from(Note.class)
                            .where(Condition.prop("favaouriteflag").eq(true))
                            .list();
                    count = notes.size();
                    checkOther();

                } else if (response.code() == 401) {
                    count = 0;
                } else {
                    // Handle other responses
                    count = 0;
                }
            }

            @Override
            public void onFailure(Call<Note> call, Throwable t) {

            }
        });
        return count;
    }

    /**
     * check if their is another record to upload
     */
    public void checkOther(){
        notes = Select.from(Note.class)
                .where(Condition.prop("favaouriteflag").eq(1))
                .list();

        count = notes.size();
        if (count >0){
            upload();
        }
    }

}
