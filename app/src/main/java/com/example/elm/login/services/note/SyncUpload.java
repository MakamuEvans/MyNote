package com.example.elm.login.services.note;

import android.app.IntentService;
import android.app.Service;
import android.content.Intent;
import android.os.IBinder;
import android.support.annotation.Nullable;
import android.util.Log;
import android.widget.Toast;

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
 * Created by elm on 7/19/17.
 */

public class SyncUpload extends IntentService {
    private List<Note> notes=new ArrayList<>();
    private int count = 0;

    /**
     * Creates an IntentService.  Invoked by your subclass's constructor.
     *
     */
    public SyncUpload() {
        super("SyncUpload");
    }

    @Override
    protected void onHandleIntent(@Nullable Intent intent) {
        Log.e("huh", "service started");
        notes = Select.from(Note.class)
                .where(Condition.prop("uploadflag").eq(1))
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
                .where(Condition.prop("uploadflag").eq(1))
                .first();
        User user = Select.from(User.class)
                .first();
        String credentials = Credentials.basic(user.getEmail(), user.getPass());

        if (note.getImage() != null){
            File file = new File(note.getImage());

            RequestBody requestBody = RequestBody.create(MediaType.parse("*/*"), file);
            MultipartBody.Part fileToUpload = MultipartBody.Part.createFormData("file", file.getName(), requestBody);
            MultipartBody.Part titlePart = MultipartBody.Part.createFormData("title", note.getTitle());
            MultipartBody.Part notePart = MultipartBody.Part.createFormData("note", note.getNote());
            RequestBody filename = RequestBody.create(MediaType.parse("text/plain"), file.getName());

            ApiInterface apiInterface = ApiClient.getClient().create(ApiInterface.class);
            Call<Note> call = apiInterface.getData(
                    titlePart,
                    notePart,
                    credentials,
                    fileToUpload,
                    filename
            );
            call.enqueue(new Callback<Note>() {
                @Override
                public void onResponse(Call<Note> call, Response<Note> response) {

                    Log.e("er", "returnedd");
                    Note data = response.body();
                    //System.out.println(data);
                    Log.e("tuone", data.getNoteid());
                    if (response.isSuccessful()) {
                        note.setNoteid(data.getNoteid());
                        note.setStatus("Success");
                        note.setUploadflag(false);
                        note.save();

                        //broadcast
                        String dt = new Gson().toJson(note);
                        Intent intent1 = new Intent();
                        intent1.setAction(NotesFragment.SyncReceiver.SYNC_ACTION);
                        intent1.putExtra("note", dt);
                        sendBroadcast(intent1);

                        notes = Select.from(Note.class)
                                .where(Condition.prop("uploadflag").eq(true))
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
                    count = 0;
                }
            });
        }else {
            ApiInterface apiInterface = ApiClient.getClient().create(ApiInterface.class);
            Call<Note> call = apiInterface.getData(
                    note.getTitle(),
                    note.getNote(),
                    credentials
            );
            call.enqueue(new Callback<Note>() {
                @Override
                public void onResponse(Call<Note> call, Response<Note> response) {

                    Log.e("er", "returnedd");
                    Note data = response.body();
                    System.out.println(data.toString());
                    if (response.isSuccessful()) {
                        note.setNoteid(data.getNoteid());
                        note.setStatus("Success");
                        note.setUploadflag(false);
                        note.setCreated_at(data.getCreated_at());
                        note.save();

                        //broadcast
                        String dt = new Gson().toJson(note);
                        Intent intent1 = new Intent();
                        intent1.setAction(NotesFragment.SyncReceiver.SYNC_ACTION);
                        intent1.putExtra("note", dt);
                        sendBroadcast(intent1);

                        notes = Select.from(Note.class)
                                .where(Condition.prop("uploadflag").eq(true))
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
                    count = 0;
                }
            });
        }



        return count;
    }

    /**
     * check if their is another record to upload
     */
    public void checkOther(){
        notes = Select.from(Note.class)
                .where(Condition.prop("uploadflag").eq(1))
                .list();

        count = notes.size();
        if (count >0){
            upload();
        }
    }

}
