package com.example.elm.login;

import android.Manifest;
import android.app.Activity;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.database.Cursor;
import android.graphics.Bitmap;
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
import android.widget.TextView;
import android.widget.Toast;

import com.example.elm.login.api.ApiClient;
import com.example.elm.login.api.ApiInterface;
import com.example.elm.login.model.Note;
import com.example.elm.login.model.User;
import com.example.elm.login.network.NetworkUtil;
import com.example.elm.login.services.note.UploadNote;
import com.google.gson.Gson;
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
    TextView save, update;
    Context context;
    public static final int READ_EXTERNAL_STORAGE = 123;
    public static final int CAMERA = 124;
    String imagePath = null;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_add_note);

        title = (EditText) findViewById(R.id.note_title);
        note = (EditText) findViewById(R.id.note_data);
        imagePlace = (ImageView) findViewById(R.id.image_place);
        save = (TextView) findViewById(R.id.save_note);
        update = (TextView) findViewById(R.id.update_note);


        ImageView delete_btn = (ImageView) findViewById(R.id.delete_image);
        delete_btn.setVisibility(View.INVISIBLE);
        update.setVisibility(View.INVISIBLE);

        Intent intent = getIntent();
        Bundle bundle = intent.getExtras();
        if (bundle != null){
            if (bundle.containsKey("noteId")){
                update.setVisibility(View.VISIBLE);
                save.setVisibility(View.INVISIBLE);
                Long note_id = intent.getExtras().getLong("noteId");
                Note note1 = Note.findById(Note.class, note_id);
                title.setText(note1.getTitle());
                note.setText(note1.getNote());
                imagePath = note1.getImage();

                if (imagePath != null){
                    int nh = (int) (BitmapFactory.decodeFile(note1.getImage()).getHeight() * (512.0 / BitmapFactory.decodeFile(note1.getImage()).getWidth()));
                    imagePlace.setImageBitmap(Bitmap.createScaledBitmap(BitmapFactory.decodeFile(note1.getImage()), 512, nh, true));
                    delete_btn.setVisibility(View.VISIBLE);
                }
            }

        }

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

    public void deleteImage(View view){
        imagePath = null;
        imagePlace.setImageBitmap(null);
        ImageView delete_btn = (ImageView) findViewById(R.id.delete_image);
        delete_btn.setVisibility(View.INVISIBLE);
    }

    public void saveNote(View view) {
        title = (EditText) findViewById(R.id.note_title);
        note = (EditText) findViewById(R.id.note_data);

        String status = NetworkUtil.getConnectivityStatusString(getBaseContext());
        //save locally
        Note note2 = new Note(null,
                title.getText().toString(),
                note.getText().toString(),
                null,
                null,
                true,
                false,
                false,
                imagePath);
        note2.save();

        Intent intent = new Intent("atherere");
        sendBroadcast(intent);

        String dt = new Gson().toJson(note2);
        Intent intent1 = new Intent();
        intent1.setAction(UploadNote.ACTION_RESP);
        intent1.putExtra("note", dt);
        sendBroadcast(intent1);

        super.onBackPressed();
    }

    public int getUserId(View view) {
        User user = Select.from(User.class)
                .first();

        return user.getUserid();
    }

    private static int LOAD_IMAGE_RESULTS = 1;
    private static int TAKE_PICTURE_RESULTS =2;

    public void takeImage(View view){
        Log.e("taking photo", "yes");
        if (ContextCompat.checkSelfPermission(AddNote.this, Manifest.permission.CAMERA) != PackageManager.PERMISSION_GRANTED){
            ActivityCompat.requestPermissions(AddNote.this, new String[]{Manifest.permission.CAMERA},CAMERA);
            return;
        }
        Intent intent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
        startActivityForResult(intent, TAKE_PICTURE_RESULTS);
    }

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
            case CAMERA: {
                if (grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED){
                    Intent intent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
                    startActivityForResult(intent, TAKE_PICTURE_RESULTS);
                }
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

        if (requestCode == TAKE_PICTURE_RESULTS && resultCode == RESULT_OK && data != null){
            Uri pickedImage = data.getData();
            Log.e("back", "back");


            String[] filepath = {MediaStore.Images.Media.DATA};
            Cursor cursor = getContentResolver().query(pickedImage, filepath, null, null, null);
            cursor.moveToFirst();
            imagePath = cursor.getString(cursor.getColumnIndex(filepath[0]));
            cursor.close();

            int nh = (int) (BitmapFactory.decodeFile(imagePath).getHeight() * (512.0 / BitmapFactory.decodeFile(imagePath).getWidth()));
            imagePlace.setImageBitmap(Bitmap.createScaledBitmap(BitmapFactory.decodeFile(imagePath), 512, nh, true));

            ImageView delete_btn = (ImageView) findViewById(R.id.delete_image);
            delete_btn.setVisibility(View.VISIBLE);


        }
    }
}
