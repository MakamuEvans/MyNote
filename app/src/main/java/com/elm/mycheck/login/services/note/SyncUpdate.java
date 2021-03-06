package com.elm.mycheck.login.services.note;

import android.app.IntentService;
import android.content.Intent;
import android.support.annotation.Nullable;
import android.util.Log;

import com.elm.mycheck.login.api.ApiClient;
import com.elm.mycheck.login.api.ApiInterface;
import com.elm.mycheck.login.model.Note;
import com.elm.mycheck.login.model.User;
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

public class SyncUpdate extends IntentService {
    private List<Note> notes=new ArrayList<>();
    private int count = 0;

    /**
     * Creates an IntentService.  Invoked by your subclass's constructor.
     *
     */
    public SyncUpdate() {
        super("SyncUpdate");
    }

    @Override
    protected void onHandleIntent(@Nullable Intent intent) {
        Log.e("huh", "service started");
        notes = Select.from(Note.class)
                .where(Condition.prop("updateflag").eq(1))
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
                .where(Condition.prop("updateflag").eq(1))
                .first();
        User user = Select.from(User.class)
                .first();
        if (note.getNoteid() == null){
            return 0;
        }
        String credentials = Credentials.basic(user.getEmail(), user.getPass());

        if (note.getImage() != null){
            File file = new File(note.getImage());

            RequestBody requestBody = RequestBody.create(MediaType.parse("*/*"), file);
            MultipartBody.Part fileToUpload = MultipartBody.Part.createFormData("file", file.getName(), requestBody);
            MultipartBody.Part titlePart = MultipartBody.Part.createFormData("title", note.getTitle());
            MultipartBody.Part serverId = MultipartBody.Part.createFormData("title", note.getNoteid());
            MultipartBody.Part notePart = MultipartBody.Part.createFormData("note", note.getNote());
            RequestBody filename = RequestBody.create(MediaType.parse("text/plain"), file.getName());

            ApiInterface apiInterface = ApiClient.getClient().create(ApiInterface.class);
            Call<Note> call = apiInterface.updateData(
                    serverId,
                    titlePart,
                    notePart,
                    credentials,
                    fileToUpload
            );
            call.enqueue(new Callback<Note>() {
                @Override
                public void onResponse(Call<Note> call, Response<Note> response) {

                    Log.e("er", "returnedd");
                    Note data = response.body();
                    if (response.isSuccessful()) {
                        Log.e("note", data.getNote());
                        note.setTitle(data.getTitle());
                        note.setNote(data.getNote());
                        note.setStatus("Success");
                        note.setUpdateflag(false);
                        note.setCreated_at(data.getCreated_at());
                        note.save();

                        //broadcast
                        String dt = new Gson().toJson(note);
                        Intent intent1 = new Intent();
                        intent1.setAction(NotesFragment.SyncReceiver.SYNC_ACTION);
                        intent1.putExtra("note", dt);
                        sendBroadcast(intent1);

                        notes = Select.from(Note.class)
                                .where(Condition.prop("updateflag").eq(true))
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
            Call<Note> call = apiInterface.updateData(
                    note.getNoteid(),
                    note.getTitle(),
                    note.getNote(),
                    credentials
            );
            call.enqueue(new Callback<Note>() {
                @Override
                public void onResponse(Call<Note> call, Response<Note> response) {

                    Log.e("er", "returnedd");
                    Note data = response.body();
                    if (response.isSuccessful()) {
                        note.setUpdateflag(false);
                        note.setNote(data.getNote());
                        note.setTitle(data.getTitle());
                        note.setCreated_at(data.getCreated_at());
                        note.save();

                        //broadcast
                        String dt = new Gson().toJson(note);
                        Intent intent1 = new Intent();
                        intent1.setAction(NotesFragment.SyncReceiver.SYNC_ACTION);
                        intent1.putExtra("note", dt);
                        sendBroadcast(intent1);

                        notes = Select.from(Note.class)
                                .where(Condition.prop("updateflag").eq(true))
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
                .where(Condition.prop("updateflag").eq(1))
                .list();

        count = notes.size();
        if (count >0){
            upload();
        }
    }
}
