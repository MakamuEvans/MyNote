package com.example.elm.login;

import android.Manifest;
import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Color;
import android.net.Uri;
import android.os.Bundle;
import android.provider.MediaStore;
import android.support.annotation.NonNull;
import android.support.design.widget.Snackbar;
import android.support.v4.app.ActivityCompat;
import android.support.v4.app.FragmentManager;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.View;
import android.view.WindowManager;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.example.elm.login.model.Note;
import com.example.elm.login.model.User;
import com.example.elm.login.network.NetworkUtil;
import com.google.gson.Gson;
import com.orm.query.Select;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import jp.wasabeef.richeditor.RichEditor;
import layout.NotesFragment;
import layout.ReminderFragment;
import layout.ToDo2;

public class AddNote extends AppCompatActivity {
    public static final String ACTION_RESP = "com.example.elm.login.services.note.Upload";
    EditText title, note;
    ImageView imageView, imagePlace;
    TextView save, update;
    RichEditor richEditor;
    Boolean savedStatus = false;
    Long note_id;
    Boolean breakStatus = false;
    Context context;
    public static final int READ_EXTERNAL_STORAGE = 123;
    public static final int CAMERA = 124;
    String imagePath = null;
    String merged = null;
    List<String> array = new ArrayList<>();
    Boolean imageStatus = true;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_add_note);

        getWindow().setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_ADJUST_RESIZE);

        richEditor= (RichEditor) findViewById(R.id.notes_editor);
        //richEditor.setEditorHeight(1000);
        //richEditor.setEditorFontSize(22);
        richEditor.setEditorBackgroundColor(Color.TRANSPARENT);
        richEditor.setPadding(10,10,10,10);
        richEditor.setPlaceholder("Touch to add notes");

        title = (EditText) findViewById(R.id.note_title);
        //note = (EditText) findViewById(R.id.note_data);
        save = (TextView) findViewById(R.id.save_note);
        update = (TextView) findViewById(R.id.update_note);

        update.setVisibility(View.INVISIBLE);

        Intent intent = getIntent();
        Bundle bundle = intent.getExtras();
        if (bundle != null){
            if (bundle.containsKey("noteId")){
                update.setVisibility(View.VISIBLE);
                save.setVisibility(View.INVISIBLE);
                note_id = intent.getExtras().getLong("noteId");
                Note note1 = Note.findById(Note.class, note_id);
                title.setText(note1.getTitle());
                richEditor.setHtml(note1.getNote());
                if (note1.getNote() != null){
                    if (note1.getNote().contains("<img src=")){
                        imageStatus = false;
                    }
                }
            }

        }

        imageView = (ImageView) findViewById(R.id.close_page);
        imageView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(final View v) {
                onBackPressed();

            }
        });

        findViewById(R.id.editor_bold).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (title.hasFocus()){
                    Toast.makeText(getBaseContext(), "Only applicable to Note's body",Toast.LENGTH_SHORT).show();
                }else {
                    richEditor.setBold();
                }
            }
        });


        findViewById(R.id.editor_bullets).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (title.hasFocus()){
                    Toast.makeText(getBaseContext(), "Only applicable to Note's body",Toast.LENGTH_SHORT).show();
                }else {
                    richEditor.setBullets();
                }
            }
        });

        findViewById(R.id.editor_numbers).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (title.hasFocus()){
                    Toast.makeText(getBaseContext(), "Only applicable to Note's body",Toast.LENGTH_SHORT).show();
                }else {
                    richEditor.setNumbers();
                }
            }
        });


        richEditor.setOnTextChangeListener(new RichEditor.OnTextChangeListener() {
            @Override public void onTextChange(String text) {
                if (text.contains("<img src=")){
                    imageStatus = false;
                    if (!text.contains("<br><img src=")){
                        String finalHtml = text.replace("<img src=", "<br><img src=");
                        finalHtml = finalHtml.replace("alt=\"Image\">", "alt=\"Image\"><br>");
                        richEditor.setHtml(finalHtml);
                        richEditor.focusEditor();
                    }
                }else {
                    imageStatus = true;
                }

                if (text.isEmpty()){
                    if (title.getText().toString().isEmpty()){
                        savedStatus = false;
                    }
                }
            }
        });

    }
    View view1;

    @Override
    public void onBackPressed() {
        //super.onBackPressed();
        closeActivity(view1);
        overridePendingTransition(R.anim.slide_out_down, R.anim.slide_in_up);
    }

    public void closeActivity(final View view){
        if (title.getText().toString().isEmpty() && richEditor.getHtml() == null){
            AddNote.super.onBackPressed();
        }else {
            Log.e("yow", "hehe");
            //AlertDialog alertDialog =new AlertDialog.Builder(AddNote.this).create();
            new AlertDialog.Builder(AddNote.this)
                    .setTitle("You have changes!")
                    .setMessage("Click on save to avoid losing your note")
                    .setPositiveButton("Save", new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialog, int which) {
                            saveNote(view);
                        }
                    })
                    .setNegativeButton("Discard", new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialog, int which) {
                            AddNote.super.onBackPressed();
                        }
                    })
                    .setNeutralButton("Cancel", new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialog, int which) {

                        }
                    })
                    .create()
                    .show();
        }

    }

    public void saveNote(View view) {
        String noteData;
        title = (EditText) findViewById(R.id.note_title);
        //note = (EditText) findViewById(R.id.note_data);
        richEditor= (RichEditor) findViewById(R.id.notes_editor);
        if (richEditor.getHtml() == null){
            noteData = null;
        }else {
            noteData = richEditor.getHtml();
        }
        //Log.e("hee", richEditor.getHtml());

        String status = NetworkUtil.getConnectivityStatusString(getBaseContext());
        if (imagePath != null){
            merged = android.text.TextUtils.join(",", array);
        }
        //save locally
        Note note2 = new Note(
                null,
                title.getText().toString(),
                noteData,
                null,
                null,
                false,
                false,
                true,
                false,
                false,
                merged,
                null);
        note2.save();

        Log.e("dt", note2.toString());

        Intent intent = new Intent("newUpload");
        sendBroadcast(intent);

        String dt = new Gson().toJson(note2);
        Intent intent1 = new Intent();
        intent1.setAction(AddNote.ACTION_RESP);
        intent1.putExtra("note", dt);
        sendBroadcast(intent1);
        int page = 1;

        Intent intent2 = new Intent(this, Navigation.class);
        intent2.putExtra("page", page);
        startActivity(intent2);
    }

    public void updateNote(View view){
        String noteData;
        title = (EditText) findViewById(R.id.note_title);
        //note = (EditText) findViewById(R.id.note_data);
        richEditor= (RichEditor) findViewById(R.id.notes_editor);
        if (richEditor.getHtml() == null){
            noteData = null;
        }else {
            noteData = richEditor.getHtml();
        }
        //Log.e("hee", richEditor.getHtml());

        String status = NetworkUtil.getConnectivityStatusString(getBaseContext());
        if (imagePath != null){
            merged = android.text.TextUtils.join(",", array);
        }
        //save locally
        Note note1 = Note.findById(Note.class, note_id);
        note1.setTitle(title.getText().toString());
        note1.setNote(noteData);
        note1.setUpdateflag(true);
        note1.save();

        Intent intent = new Intent("Update");
        sendBroadcast(intent);

        String dt = new Gson().toJson(note1);
        Intent intent1 = new Intent();
        intent1.setAction(NotesFragment.SyncReceiver.SYNC_ACTION);
        intent1.putExtra("note", dt);
        sendBroadcast(intent1);

        this.finish();
    }

    public int getUserId(View view) {
        User user = Select.from(User.class)
                .first();

        return user.getUserid();
    }

    private static int LOAD_IMAGE_RESULTS = 1;
    private static int TAKE_PICTURE_RESULTS =2;

    public void cameraImage(View view){
        if (ContextCompat.checkSelfPermission(AddNote.this, Manifest.permission.CAMERA) != PackageManager.PERMISSION_GRANTED) {
            ActivityCompat.requestPermissions(AddNote.this, new String[]{Manifest.permission.CAMERA},
                    CAMERA);
            return;
        }
        if (title.hasFocus()){
            Toast.makeText(getBaseContext(), "Only applicable when Focus is in Note's body",Toast.LENGTH_SHORT).show();
        }else {
            insertImage(view, true);
        }
    }

    public void galleryImage(View view){
        if (ContextCompat.checkSelfPermission(AddNote.this, Manifest.permission.READ_EXTERNAL_STORAGE) != PackageManager.PERMISSION_GRANTED &&
                ContextCompat.checkSelfPermission(AddNote.this, Manifest.permission.WRITE_EXTERNAL_STORAGE) != PackageManager.PERMISSION_GRANTED) {
            ActivityCompat.requestPermissions(AddNote.this, new String[]{Manifest.permission.READ_EXTERNAL_STORAGE, Manifest.permission.WRITE_EXTERNAL_STORAGE},
                    READ_EXTERNAL_STORAGE);
            return;
        }
        if (title.hasFocus()){
            Toast.makeText(getBaseContext(), "Only applicable when Focus is in Note's body",Toast.LENGTH_SHORT).show();
        }else {
            insertImage(view,false);
        }
    }

    public void insertImage(View view, Boolean camera) {
        Log.e("ati", "what");
        if (imageStatus){
            if (camera){
                Intent intent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
                startActivityForResult(intent, TAKE_PICTURE_RESULTS);
            }else {
                Intent intent = new Intent(Intent.ACTION_PICK, MediaStore.Images.Media.EXTERNAL_CONTENT_URI);
                startActivityForResult(intent, LOAD_IMAGE_RESULTS);
            }
        }else {
            Toast.makeText(view.getContext(), "Only one Image Supported! Delete current image and try again.", Toast.LENGTH_LONG).show();
        }


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

        richEditor= (RichEditor) findViewById(R.id.notes_editor);
        Log.e("back", "back");

        if (requestCode == LOAD_IMAGE_RESULTS && resultCode == RESULT_OK && data != null) {
            Uri pickedImage = data.getData();
            Log.e("back", "back");


            String[] filepath = {MediaStore.Images.Media.DATA};
            Cursor cursor = getContentResolver().query(pickedImage, filepath, null, null, null);
            cursor.moveToFirst();
            imagePath = cursor.getString(cursor.getColumnIndex(filepath[0]));
            array.add(imagePath);

            //imagePlace.setImageBitmap(BitmapFactory.decodeFile(imagePath));
            richEditor.insertImage(imagePath + "\" style=\"width:80%;", "Image");
            cursor.close();
        }

        if (requestCode == TAKE_PICTURE_RESULTS && resultCode == RESULT_OK && data != null){
            Uri pickedImage = data.getData();
            Log.e("cam", "cam");

            String[] filepath = {MediaStore.Images.Media.DATA};
            Cursor cursor = getContentResolver().query(pickedImage, filepath, null, null, null);
            cursor.moveToFirst();
            imagePath = cursor.getString(cursor.getColumnIndex(filepath[0]));
            Log.e("aii", imagePath);

            richEditor.insertImage(imagePath + "\" style=\"width:80%;", "Image");
            cursor.close();
        }
    }
}
