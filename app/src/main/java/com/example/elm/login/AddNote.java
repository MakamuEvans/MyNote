package com.example.elm.login;

import android.app.ProgressDialog;
import android.content.Intent;
import android.content.SharedPreferences;
import android.database.Cursor;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.provider.MediaStore;
import android.support.design.widget.Snackbar;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
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

import layout.NotesFragment;
import okhttp3.Credentials;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class AddNote extends AppCompatActivity {
    EditText title, note;
    ImageView imageView, imagePlace;

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

    public void saveNote(View view){
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
            startService(intent);
            super.onBackPressed();
            overridePendingTransition(R.anim.slide_out_down, R.anim.slide_in_up);
        }else {
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
            startService(intent);
            super.onBackPressed();
        }

    }

    public int getUserId(View view){
        User user = Select.from(User.class)
                .first();

        return user.getUserid();
    }

    private static int LOAD_IMAGE_RESULTS = 1;

    public void insertImage(View view){
        Intent intent = new Intent(Intent.ACTION_PICK, MediaStore.Images.Media.EXTERNAL_CONTENT_URI);
        startActivityForResult(intent, LOAD_IMAGE_RESULTS);
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        imagePlace = (ImageView) findViewById(R.id.image_place);

        if (requestCode == LOAD_IMAGE_RESULTS && resultCode == RESULT_OK && data != null){
            Uri pickedImage= data.getData();

            String[] filepath = { MediaStore.Images.Media.DATA};
            Cursor cursor = getContentResolver().query(pickedImage, filepath, null,null,null);
            cursor.moveToFirst();
            String imagePath = cursor.getString(cursor.getColumnIndex(filepath[0]));

            imagePlace.setImageBitmap(BitmapFactory.decodeFile(imagePath));

            cursor.close();
        }
    }
}
