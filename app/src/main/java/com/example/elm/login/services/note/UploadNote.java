package com.example.elm.login.services.note;

import android.app.IntentService;
import android.content.Intent;
import android.support.annotation.Nullable;
import android.widget.Toast;

import com.example.elm.login.AddNote;
import com.example.elm.login.api.ApiClient;
import com.example.elm.login.api.ApiInterface;
import com.example.elm.login.model.Note;

import layout.NotesFragment;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

/**
 * Created by elm on 7/19/17.
 */

public class UploadNote extends IntentService{
    public static final String ACTION_RESP = "com.example.elm.login.services.note.Upload";

    /**
     * Creates an IntentService.  Invoked by your subclass's constructor.
     *
     */
    public UploadNote() {
        super("UploadNote");
    }

    @Override
    protected void onHandleIntent(@Nullable Intent intent) {
        String title = intent.getStringExtra("title");
        String note = intent.getStringExtra("note");
        String credentials = intent.getStringExtra("credentials");

        System.out.println(title);

        ApiInterface apiInterface = ApiClient.getClient().create(ApiInterface.class);
        Call<Note> call = apiInterface.getData(
                title,
                note,
                credentials
        );
        call.enqueue(new Callback<Note>() {
            @Override
            public void onResponse(Call<Note> call, Response<Note> response) {

                Note data = response.body();
                if (response.isSuccessful()) {
                    Note note = new Note(
                            data.getNoteid(),
                            data.getTitle(),
                            data.getNote(),
                            "success",
                            "10-10-July",
                            false,
                            false,
                            false
                    );
                    note.save();

                    //broadcast
                    Intent intent1 = new Intent();
                    intent1.setAction(ACTION_RESP);
                    //intent1.addCategory(Intent.ACTION_DEFAULT);
                    sendBroadcast(intent1);

                } else if (response.code() == 401) {
                    Toast.makeText(getBaseContext(), "Auth Error", Toast.LENGTH_SHORT).show();

                } else {
                    // Handle other responses
                    Toast.makeText(getBaseContext(), "fk!", Toast.LENGTH_SHORT).show();

                }
            }

            @Override
            public void onFailure(Call<Note> call, Throwable t) {
                Toast.makeText(getBaseContext(), "Error yaa?", Toast.LENGTH_SHORT).show();

            }
        });
    }
}
