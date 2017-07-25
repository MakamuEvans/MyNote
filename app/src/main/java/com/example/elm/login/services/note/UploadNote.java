package com.example.elm.login.services.note;

import android.app.IntentService;
import android.content.Intent;
import android.net.Uri;
import android.provider.MediaStore;
import android.support.annotation.Nullable;
import android.widget.Toast;

import com.example.elm.login.AddNote;
import com.example.elm.login.api.ApiClient;
import com.example.elm.login.api.ApiInterface;
import com.example.elm.login.model.Note;
import com.google.gson.Gson;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.Serializable;

import layout.NotesFragment;
import okhttp3.MediaType;
import okhttp3.MultipartBody;
import okhttp3.RequestBody;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;
import retrofit2.http.Field;

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
        Boolean internet = intent.getExtras().getBoolean("internet");
        final String image = intent.getStringExtra("image");

        if (internet){
            upload(title, note, credentials, image);
        }else {
            Note newNote = new Note(null,title,note,null,null,true,null,null,image.equals("null")? null: image);
            newNote.save();

            String dt = new Gson().toJson(newNote);
            Intent intent1 = new Intent();
            intent1.setAction(ACTION_RESP);
            intent1.putExtra("note", dt);
            //intent1.addCategory(Intent.ACTION_DEFAULT);
            sendBroadcast(intent1);
        }

    }

    public void upload(final String title, String note, String credentials, final String image){
        if (image.equals("null")){

        }else {

        }
        File file = new File(image);

        RequestBody requestBody = RequestBody.create(MediaType.parse("*/*"), file);
        MultipartBody.Part fileToUpload = MultipartBody.Part.createFormData("file", file.getName(), requestBody);
        MultipartBody.Part titlePart = MultipartBody.Part.createFormData("title", title);
        MultipartBody.Part notePart = MultipartBody.Part.createFormData("note", note);
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

                Note data = response.body();
                if (response.isSuccessful()) {
                    System.out.println(data);
                    String savedImage = null;
                    /*try {
                        savedImage = MediaStore.Images.Media.insertImage(
                                getContentResolver(),
                                image,
                                title,
                                title
                        );

                    } catch (FileNotFoundException e) {
                        e.printStackTrace();
                    }*/
                    Note note = new Note(
                            data.getNoteid(),
                            data.getTitle(),
                            data.getNote(),
                            "success",
                            "10-10-July",
                            false,
                            false,
                            false,
                            image

                    );
                    note.save();

                    //broadcast
                    String dt = new Gson().toJson(note);
                    Intent intent1 = new Intent();
                    intent1.setAction(ACTION_RESP);
                    intent1.putExtra("note", dt);
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

    public void huh(){
        /*try {
            String savedImage = MediaStore.Images.Media.insertImage(
                    getContentResolver(),
                    imagePath,
                    "Image",
                    "Same"
            );
            Uri imageUri = Uri.parse(savedImage);
            imagePlace.setImageURI(imageUri);
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        }*/
    }
}
