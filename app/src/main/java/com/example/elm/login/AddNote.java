package com.example.elm.login;

import android.Manifest;
import android.app.Activity;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.database.Cursor;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.os.Build;
import android.provider.MediaStore;
import android.support.annotation.NonNull;
import android.support.design.widget.Snackbar;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.ContextCompat;
import android.support.v4.media.MediaBrowserCompat;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.util.Patterns;
import android.view.View;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.Toast;

import com.example.elm.login.api.ApiClient;
import com.example.elm.login.api.ApiInterface;
import com.example.elm.login.model.Note;
import com.example.elm.login.model.User;
import com.example.elm.login.network.NetworkUtil;
import com.example.elm.login.services.note.UploadNote;
import com.orm.query.Select;

import java.io.FileNotFoundException;

import layout.NotesFragment;
import okhttp3.Credentials;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class AddNote extends AppCompatActivity {
    EditText title, note;
    ImageView imageView, imagePlace;
    Context context;
    public static final int READ_EXTERNAL_STORAGE = 123;
    public static final int WRITE_EXTERNAL_STORAGE = 123;
    String imagePath = null;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_add_note);

        imageView = (ImageView) findViewById(R.id.close_page);
        imageView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                AddNote.super.onBackPressed();
            }
        });
    }

    @Override
    public void onBackPressed() {
        super.onBackPressed();
        overridePendingTransition(R.anim.slide_out_down, R.anim.slide_in_up);
    }

    public void saveNote(View view) {
        title = (EditText) findViewById(R.id.note_title);
        note = (EditText) findViewById(R.id.note_data);

        String status = NetworkUtil.getConnectivityStatusString(getBaseContext());
        //save locally
        if (status.equals("Not connected to Internet")) {
            Toast.makeText(getBaseContext(), "No Internet Access. Saving Locally...", Toast.LENGTH_SHORT).show();
            Intent intent = new Intent(this, UploadNote.class);
            intent.putExtra("title", title.getText().toString());
            intent.putExtra("note", note.getText().toString());
            intent.putExtra("internet", false);
            if (imagePath != null){
                intent.putExtra("image", imagePath);
            }else {
                intent.putExtra("image", "null");
            }
            startService(intent);
            super.onBackPressed();
            overridePendingTransition(R.anim.slide_out_down, R.anim.slide_in_up);
        } else {
            //upload
            //create auth tokens from user email and password
            User user = Select.from(User.class)
                    .first();
            String credentials = Credentials.basic(user.getEmail(), user.getPass());
            Intent intent = new Intent(this, UploadNote.class);
            intent.putExtra("title", title.getText().toString());
            intent.putExtra("note", note.getText().toString());
            intent.putExtra("credentials", credentials);
            intent.putExtra("internet", true);
            if (imagePath != null){
                intent.putExtra("image", imagePath);
            }
            startService(intent);
            super.onBackPressed();
        }

    }

    public int getUserId(View view) {
        User user = Select.from(User.class)
                .first();

        return user.getUserid();
    }

    private static int LOAD_IMAGE_RESULTS = 1;

    public void insertImage(View view) {
        Log.e("ati", "what");
        if (ContextCompat.checkSelfPermission(AddNote.this, Manifest.permission.READ_EXTERNAL_STORAGE) != PackageManager.PERMISSION_GRANTED &&
                ContextCompat.checkSelfPermission(AddNote.this, Manifest.permission.WRITE_EXTERNAL_STORAGE) != PackageManager.PERMISSION_GRANTED) {
            ActivityCompat.requestPermissions(AddNote.this, new String[]{Manifest.permission.READ_EXTERNAL_STORAGE, Manifest.permission.WRITE_EXTERNAL_STORAGE},
                    READ_EXTERNAL_STORAGE);
            return;
        }
        Intent intent = new Intent(Intent.ACTION_PICK, MediaStore.Images.Media.EXTERNAL_CONTENT_URI);
        startActivityForResult(intent, LOAD_IMAGE_RESULTS);

       /* if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {

        } else {

        }*/
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        switch (requestCode) {
            case READ_EXTERNAL_STORAGE: {
                if (grantResults.length > 0
                        && grantResults[0] == PackageManager.PERMISSION_GRANTED
                        && grantResults[1] == PackageManager.PERMISSION_GRANTED) {
                    Intent intent = new Intent(Intent.ACTION_PICK, MediaStore.Images.Media.EXTERNAL_CONTENT_URI);
                    startActivityForResult(intent, LOAD_IMAGE_RESULTS);
                } else {
                    Toast.makeText(getBaseContext(), "Access Denied", Toast.LENGTH_SHORT).show();
                }
                return;
            }
        }
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        imagePlace = (ImageView) findViewById(R.id.image_place);
        Log.e("back", "back");

        if (requestCode == LOAD_IMAGE_RESULTS && resultCode == RESULT_OK && data != null) {
            Uri pickedImage = data.getData();
            Log.e("back", "back");


            String[] filepath = {MediaStore.Images.Media.DATA};
            Cursor cursor = getContentResolver().query(pickedImage, filepath, null, null, null);
            cursor.moveToFirst();
            imagePath = cursor.getString(cursor.getColumnIndex(filepath[0]));

            imagePlace.setImageBitmap(BitmapFactory.decodeFile(imagePath));

            cursor.close();
        }
    }
}
