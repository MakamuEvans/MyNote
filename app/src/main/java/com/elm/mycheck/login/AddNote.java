package com.elm.mycheck.login;

import android.Manifest;
import android.app.AlertDialog;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.content.res.Resources;
import android.database.Cursor;
import android.graphics.Color;
import android.net.Uri;
import android.os.Bundle;
import android.provider.MediaStore;
import android.support.annotation.ColorInt;
import android.support.annotation.NonNull;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AppCompatActivity;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.Log;
import android.util.TypedValue;
import android.view.Gravity;
import android.view.View;
import android.view.WindowManager;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.elm.mycheck.login.model.Category;
import com.elm.mycheck.login.model.Note;
import com.elm.mycheck.login.model.User;
import com.google.gson.Gson;
import com.orm.query.Select;
import com.tooltip.Tooltip;
import com.valdesekamdem.library.mdtoast.MDToast;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import jp.wasabeef.richeditor.RichEditor;
import layout.NotesFragment;

public class AddNote extends AppCompatActivity {
    public static final String ACTION_RESP = "com.elm.mycheck.login.services.note.Upload";
    private EditText title, note;
    private ImageView imageView, imagePlace, strike_through;
    private TextView save, update, add_category;
    private RichEditor richEditor;
    private Long category = null;
    private Boolean savedStatus = false;
    private Long note_id;
    private Boolean breakStatus = false;
    private Context context;
    public static final int READ_EXTERNAL_STORAGE = 123;
    public static final int CAMERA = 124;
    private String imagePath = null;
    private String merged = null;
    private List<String> array = new ArrayList<>();
    private Boolean imageStatus = true;
    private Boolean editListener =false;
    private Boolean editmode = false;
    private View view1;
    private CategoryReceiver categoryReceiver;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        SharedPreferences preferences = getSharedPreferences("myPref", 0);
        //Boolean fk = getSharedPreferences("myPref", 0).getBoolean("loggedIn", false);
        String theme = getSharedPreferences("myPref", 0).getString("theme", "Default");
        Log.e("Theme", theme);
        if (theme == "tomato")
            setTheme(R.style.AppTheme_NoActionBar);
        if (theme == "tangarine")
            setTheme(R.style.AppTheme_NoActionBar_Tangarine);
        if (theme.equalsIgnoreCase("banana"))
            setTheme(R.style.AppTheme_NoActionBar_Banana);
        if (theme.equalsIgnoreCase("basil"))
            setTheme(R.style.AppTheme_NoActionBar_Basil);
        if (theme.equalsIgnoreCase("sage"))
            setTheme(R.style.AppTheme_NoActionBar_Sage);
        if (theme.equalsIgnoreCase("peacock"))
            setTheme(R.style.AppTheme_NoActionBar_Peacock);
        if (theme.equalsIgnoreCase("blueberry"))
            setTheme(R.style.AppTheme_NoActionBar_BlueBerry);
        if (theme.equalsIgnoreCase("lavender"))
            setTheme(R.style.AppTheme_NoActionBar_Lavender);
        if (theme.equalsIgnoreCase("grape"))
            setTheme(R.style.AppTheme_NoActionBar_Grape);
        if (theme.equalsIgnoreCase("flamingo"))
            setTheme(R.style.AppTheme_NoActionBar_Flamingo);
        if (theme.equalsIgnoreCase("graphite"))
            setTheme(R.style.AppTheme_NoActionBar_Graphite);


        setContentView(R.layout.activity_add_note);

        getWindow().setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_ADJUST_RESIZE);

        richEditor= (RichEditor) findViewById(R.id.notes_editor);
        //richEditor.setEditorHeight(1000);
        //richEditor.setEditorFontSize(22);
        richEditor.setEditorBackgroundColor(Color.TRANSPARENT);
        richEditor.setPadding(10,10,10,10);
        richEditor.setPlaceholder("Touch to add notes");


        //register receivers
        IntentFilter intentFilter = new IntentFilter(CategoryReceiver.ACTIION_REP);
        categoryReceiver = new AddNote.CategoryReceiver();
        registerReceiver(categoryReceiver,intentFilter);


        title = (EditText) findViewById(R.id.note_title);
        //note = (EditText) findViewById(R.id.note_data);
        save = (TextView) findViewById(R.id.save_note);
        update = (TextView) findViewById(R.id.update_note);
        add_category = (TextView) findViewById(R.id.add_category);

        add_category.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                addcategory();
                Log.e("ctaegory", "clicked");
            }
        });

        if (add_category.getText().toString().equalsIgnoreCase("Add Category")){
            new Tooltip.Builder(add_category)
                    .setText("Categorize your Note")
                    .setCancelable(true)
                    .setBackgroundColor(getResources().getColor(R.color.colorAccentTomato))
                    .setTextColor(getResources().getColor(R.color.colorWhite))
                    .show();
        }

        update.setVisibility(View.INVISIBLE);

        Intent intent = getIntent();
        Bundle bundle = intent.getExtras();
        if (bundle != null){
            if (bundle.containsKey("noteId")){
                editmode = true;
                update.setVisibility(View.VISIBLE);
                save.setVisibility(View.INVISIBLE);
                note_id = intent.getExtras().getLong("noteId");
                Note note1 = Note.findById(Note.class, note_id);
                title.setText(note1.getTitle());
                richEditor.setHtml(note1.getNote());
                if (note1.getCategory() != null){
                    setCategory(note1.getCategory());
                }
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
                    MDToast.makeText(getBaseContext(), "Only applicable to Note's body", MDToast.LENGTH_SHORT, MDToast.TYPE_WARNING).show();
                }else {
                    richEditor.setBold();
                }
            }
        });

        findViewById(R.id.editor_quote).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (title.hasFocus()){
                    MDToast.makeText(getBaseContext(), "Only applicable to Note's body", MDToast.LENGTH_SHORT, MDToast.TYPE_WARNING).show();
                }else {
                    richEditor.setBlockquote();
                }
            }
        });

        findViewById(R.id.editor_ordered).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (title.hasFocus()){
                    MDToast.makeText(getBaseContext(), "Only applicable to Note's body", MDToast.LENGTH_SHORT, MDToast.TYPE_WARNING).show();
                }else {
                    richEditor.setNumbers();
                }
            }
        });

        findViewById(R.id.editor_italic).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (title.hasFocus()){
                    MDToast.makeText(getBaseContext(), "Only applicable to Note's body", MDToast.LENGTH_SHORT, MDToast.TYPE_WARNING).show();
                }else {
                    richEditor.setItalic();
                }
            }
        });

        findViewById(R.id.editor_undo).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (title.hasFocus()){
                    MDToast.makeText(getBaseContext(), "Only applicable to Note's body", MDToast.LENGTH_SHORT, MDToast.TYPE_WARNING).show();
                }else {
                    richEditor.undo();
                }
            }
        });

        findViewById(R.id.editor_redo).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (title.hasFocus()){
                    MDToast.makeText(getBaseContext(), "Only applicable to Note's body", MDToast.LENGTH_SHORT, MDToast.TYPE_WARNING).show();
                }else {
                    richEditor.redo();
                }
            }
        });

        findViewById(R.id.editor_underline).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (title.hasFocus()){
                    MDToast.makeText(getBaseContext(), "Only applicable to Note's body", MDToast.LENGTH_SHORT, MDToast.TYPE_WARNING).show();
                }else {
                    richEditor.setUnderline();
                }
            }
        });

        findViewById(R.id.strike_through).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (title.hasFocus()){
                    MDToast.makeText(getBaseContext(), "Only applicable to Note's body", MDToast.LENGTH_SHORT, MDToast.TYPE_WARNING).show();
                }else {
                    richEditor.setStrikeThrough();
                }
            }
        });

        findViewById(R.id.editor_bullets).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (title.hasFocus()){
                    MDToast.makeText(getBaseContext(), "Only applicable to Note's body", MDToast.LENGTH_SHORT, MDToast.TYPE_WARNING).show();
                }else {
                    richEditor.setBullets();
                }
            }
        });

       /* findViewById(R.id.editor_numbers).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (title.hasFocus()){
                    MDToast.makeText(getBaseContext(), "Only applicable to Note's body", MDToast.LENGTH_SHORT, MDToast.TYPE_WARNING).show();
                }else {
                    richEditor.setNumbers();
                }
            }
        });*/

        title.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                editListener = true;
            }

            @Override
            public void afterTextChanged(Editable s) {

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

                editListener = true;
            }
        });

    }

    @Override
    protected void onDestroy() {
        if (categoryReceiver != null)
            unregisterReceiver(categoryReceiver);
        super.onDestroy();
    }

    private void addcategory() {
        AddCategory addCategory = new AddCategory();

        addCategory.show(getSupportFragmentManager(), "Dialog");
    }

    @Override
    protected void onResume() {
        super.onResume();
    }

    @Override
    public void onBackPressed() {
        //super.onBackPressed();
        closeActivity(view1);
        overridePendingTransition(R.anim.slide_out_down, R.anim.slide_in_up);
    }

    public void closeActivity(final View view){
        if (!editListener){
            AddNote.super.onBackPressed();
        }else {
            new AlertDialog.Builder(AddNote.this)
                    .setTitle("You made changes!")
                    .setMessage("Click on Save to avoid losing your Note")
                    .setPositiveButton("Save", new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialog, int which) {
                            if (editmode){
                                updateNote(view);
                            }else {
                                saveNote(view);
                            }
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
        richEditor= (RichEditor) findViewById(R.id.notes_editor);
        if (richEditor.getHtml() == null){
            noteData = null;
        }else {
            noteData = richEditor.getHtml();
        }

        if (imagePath != null){
            merged = android.text.TextUtils.join(",", array);
        }

        //get current date
        SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd HH:mm");
        Date date = new Date();
        //save locally
        Note note2 = new Note(
                null,
                title.getText().toString(),
                noteData,
                category,
                null,
                dateFormat.format(date),
                dateFormat.format(date),
                false,
                false,
                true,
                false,
                false,
                merged,
                null);
        note2.save();

        //alert upload service of new data
        /*Intent intent = new Intent("newUpload");
        sendBroadcast(intent);
*/
        //alert note activity to update UI
        String dt = new Gson().toJson(note2);
        Intent intent1 = new Intent();
        intent1.setAction(AddNote.ACTION_RESP);
        intent1.putExtra("note", dt);
        sendBroadcast(intent1);

        //close activity and open relevant one.
        int page = 1;
        Intent intent2 = new Intent(this, Navigation.class);
        intent2.putExtra("page", page);
        intent2.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
        startActivity(intent2);
    }

    public void updateNote(View view){
        String noteData;
        title = (EditText) findViewById(R.id.note_title);
        richEditor= (RichEditor) findViewById(R.id.notes_editor);
        if (richEditor.getHtml() == null){
            noteData = null;
        }else {
            noteData = richEditor.getHtml();
        }

        if (imagePath != null){
            merged = android.text.TextUtils.join(",", array);
        }

        //get current date
        SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd HH:mm");
        Date date = new Date();

        //save locally
        Note note1 = Note.findById(Note.class, note_id);
        note1.setTitle(title.getText().toString());
        note1.setNote(noteData);
        note1.setCategory(category);
        note1.setUpdated_at(dateFormat.format(date));
        //note1.setUpdateflag(true);
        note1.save();

       /* Intent intent = new Intent("Update");
        sendBroadcast(intent);*/

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

    private  void setCategory(Long categoryId){
        category = categoryId;
        Category category = Category.findById(Category.class, categoryId);
        String title = category.getTitle();
        add_category.setText(title);
    }

    public class CategoryReceiver extends BroadcastReceiver {
        public static final String ACTIION_REP = "getCategory";
        @Override
        public void onReceive(Context context, Intent intent) {
            //Log.e("Haa", "TaskReceiver Called");
            Bundle bundle = intent.getExtras();
            Long title = bundle.getLong("title");
            setCategory(title);
        }
    }
}
