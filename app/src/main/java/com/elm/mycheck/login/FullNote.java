package com.elm.mycheck.login;

import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Color;
import android.media.Image;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.CardView;
import android.support.v7.widget.Toolbar;
import android.text.Html;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.MotionEvent;
import android.view.ScaleGestureDetector;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

import com.elm.mycheck.login.model.Category;
import com.elm.mycheck.login.model.Note;
import com.github.irshulx.Editor;
import com.github.irshulx.models.EditorContent;
import com.google.gson.Gson;
import com.valdesekamdem.library.mdtoast.MDToast;

import layout.NotesFragment;

public class FullNote extends AppCompatActivity {
    private Long note_id = null;
    private TextView updated_on, full_category;
   // private RichEditor richEditor;
    private Editor editor;
    Note note1;
    int fontSize = 15;
    private ImageView in,out,fav,edit,delete;

    //pinch
    private ScaleGestureDetector scaleGestureDetector;
    private float scaleFactor = 1.0f;
    private CardView cardView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        SharedPreferences preferences = getSharedPreferences("myPref", 0);
        //Boolean fk = getSharedPreferences("myPref", 0).getBoolean("loggedIn", false);
        String theme = getSharedPreferences("myPref", 0).getString("theme", "Default");
        Log.e("Theme", theme);
        setTheme(R.style.AppTheme_NoActionBar_Primary);

        setContentView(R.layout.activity_full_note);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
      //  toolbar.setElevation(0);
        setSupportActionBar(toolbar);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setElevation(8);

        Intent intent = getIntent();
        note_id = intent.getExtras().getLong("noteId");
        note1 = Note.findById(Note.class, note_id);



        //init
        init();
        //set data
        if (note1.getTitle() == null) {
            setTitle("Empty Title");
        } else {
            setTitle(note1.getTitle());
        }

        editor = findViewById(R.id.editor);
        EditorContent editorContent = editor.getContentDeserialized(note1.getNote());
        editor.render(editorContent);

        updated_on = (TextView) findViewById(R.id.updated_on);
        full_category = (TextView) findViewById(R.id.full_category);
        updated_on.setText("last updated on: " + note1.getUpdated_at());
        if (note1.getCategory() != null) {
            Category category = Category.findById(Category.class, note1.getCategory());
            full_category.setText(category.getTitle());
        }


    }

    private void init() {
        in = (ImageView) findViewById(R.id.action_zoomin);
        out = (ImageView) findViewById(R.id.action_zoomout);
        fav = (ImageView) findViewById(R.id.action_favourite);
        edit = (ImageView) findViewById(R.id.action_edit);
        delete = (ImageView) findViewById(R.id.action_delete);

        if (note1.getFavaouriteflag()){
            fav.setImageResource(R.drawable.full_fav);
        }else {
            fav.setImageResource(R.drawable.full_unfav);
        }

        in.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (fontSize >= 10)
                    fontSize--;
                editor.setH1TextSize(fontSize);
                //richEditor.setEditorFontSize(fontSize);
            }
        });
        out.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (fontSize <= 25)
                    fontSize++;
                editor.setH1TextSize(fontSize);
                //richEditor.setEditorFontSize(fontSize);
            }
        });
        fav.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Note note1 = Note.findById(Note.class, note_id);
                note1.setFavaouriteflag(true);
                Boolean status = !note1.getFavourite() ? true : false;
                note1.setFavourite(status);
                note1.save();

                if (status) {
                    fav.setImageResource(R.drawable.full_fav);
                    MDToast.makeText(getBaseContext(), note1.getTitle() + " added to favourite", MDToast.LENGTH_SHORT, MDToast.TYPE_SUCCESS).show();
                } else {
                    fav.setImageResource(R.drawable.full_unfav);
                    MDToast.makeText(getBaseContext(), note1.getTitle() + " removed from favourite", MDToast.LENGTH_SHORT, MDToast.TYPE_WARNING).show();
                }

                String dt = new Gson().toJson(note1);
                Intent intent2 = new Intent();
                intent2.setAction(NotesFragment.SyncReceiver.SYNC_ACTION);
                intent2.putExtra("note", dt);
                sendBroadcast(intent2);

                Intent intent = new Intent();
                intent.setAction("favourite");
                getBaseContext().sendBroadcast(intent);

            }
        });
        edit.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent1 = new Intent(FullNote.this, AddNote.class);
                intent1.putExtra("noteId", note_id);
                startActivity(intent1);
            }
        });
        delete.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Note note = Note.findById(Note.class, note_id);

                new AlertDialog.Builder(view.getContext())
                        .setTitle("Are you sure?")
                        .setMessage("You are about to delete Note " + note.getTitle())
                        .setPositiveButton("Cancel", new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialog, int which) {

                            }
                        })
                        .setNegativeButton("Delete", new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialog, int which) {
                                Note note = Note.findById(Note.class, note_id);
                                note.setDeleteflag(true);
                                note.save();

                                String data = new Gson().toJson(note);
                                Intent intent4 = new Intent();
                                intent4.setAction(NotesFragment.DeleteReceiver.SYNC_ACTION);
                                intent4.putExtra("note", data);
                                sendBroadcast(intent4);

                                Intent intent3 = new Intent();
                                intent3.setAction("delete");
                                getBaseContext().sendBroadcast(intent3);

                                finish();


                            }
                        })
                        .create()
                        .show();
            }
        });
    }


    @Override
    protected void onResume() {
        super.onResume();
        Note note1 = Note.findById(Note.class, note_id);

        //set data
        if (note1.getTitle() == null) {
            setTitle("Empty Title");
        } else {
            setTitle(note1.getTitle());
        }

        EditorContent editorContent = editor.getContentDeserialized(note1.getNote());
        editor.render(editorContent);
       /* RichEditor richEditor = (RichEditor) findViewById(R.id.notes_editor);
        richEditor.setEditorBackgroundColor(Color.TRANSPARENT);
        richEditor.setInputEnabled(false);
        richEditor.setHtml(note1.getNote());*/
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case R.id.action_share:
                final Intent emailIntent = new Intent(android.content.Intent.ACTION_SEND);
                emailIntent.setType("text/html");
                emailIntent.putExtra(android.content.Intent.EXTRA_SUBJECT, "HAHA");
                emailIntent.putExtra(android.content.Intent.EXTRA_TEXT, Html.fromHtml(note1.getNote()));
                startActivity(Intent.createChooser(emailIntent, "Send to:"));
            case android.R.id.home:
                //Write your logic here
                this.finish();
                return true;
            case R.id.action_favourite:
                Note note1 = Note.findById(Note.class, note_id);
                note1.setFavaouriteflag(true);
                Boolean status = !note1.getFavourite() ? true : false;
                note1.setFavourite(status);
                note1.save();

                if (status) {
                    item.setIcon(R.drawable.full_fav);
                    MDToast.makeText(getBaseContext(), note1.getTitle() + " added to favourite", MDToast.LENGTH_SHORT, MDToast.TYPE_SUCCESS).show();
                } else {
                    item.setIcon(R.drawable.full_unfav);
                    MDToast.makeText(getBaseContext(), note1.getTitle() + " removed from favourite", MDToast.LENGTH_SHORT, MDToast.TYPE_WARNING).show();
                }

                String dt = new Gson().toJson(note1);
                Intent intent2 = new Intent();
                intent2.setAction(NotesFragment.SyncReceiver.SYNC_ACTION);
                intent2.putExtra("note", dt);
                sendBroadcast(intent2);

                Intent intent = new Intent();
                intent.setAction("favourite");
                getBaseContext().sendBroadcast(intent);

                return true;
            case R.id.action_delete:
                Note note = Note.findById(Note.class, note_id);

                new AlertDialog.Builder(this)
                        .setTitle("Are you sure?")
                        .setMessage("You are about to delete Note " + note.getTitle())
                        .setPositiveButton("Cancel", new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialog, int which) {

                            }
                        })
                        .setNegativeButton("Delete", new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialog, int which) {
                                Note note = Note.findById(Note.class, note_id);
                                note.setDeleteflag(true);
                                note.save();

                                String data = new Gson().toJson(note);
                                Intent intent4 = new Intent();
                                intent4.setAction(NotesFragment.DeleteReceiver.SYNC_ACTION);
                                intent4.putExtra("note", data);
                                sendBroadcast(intent4);

                                Intent intent3 = new Intent();
                                intent3.setAction("delete");
                                getBaseContext().sendBroadcast(intent3);

                                finish();


                            }
                        })
                        .create()
                        .show();
                return true;
            case R.id.action_edit:
                Intent intent1 = new Intent(FullNote.this, AddNote.class);
                intent1.putExtra("noteId", note_id);
                startActivity(intent1);
                return true;
            case R.id.action_out:
                if (fontSize <= 25)
                    fontSize++;
                //richEditor.setEditorFontSize(fontSize);
                return true;
            case R.id.action_in:
                if (fontSize >= 10)
                    fontSize--;
                //richEditor.setEditorFontSize(fontSize);
                return true;
            default:
                return super.onOptionsItemSelected(item);
        }
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.menu_full_note, menu);
        Note note1 = Note.findById(Note.class, note_id);

        if (note1.getFavourite()) {
            menu.findItem(R.id.action_favourite).setIcon(R.mipmap.ic_action_favorite_white);
        } else {
            menu.findItem(R.id.action_favourite).setIcon(R.mipmap.ic_action_favorite_border);
        }
        return true;
    }
}
