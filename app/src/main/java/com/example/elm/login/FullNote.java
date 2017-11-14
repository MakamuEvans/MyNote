package com.example.elm.login;

import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Color;
import android.os.Bundle;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.Snackbar;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.example.elm.login.model.Note;
import com.google.gson.Gson;
import com.valdesekamdem.library.mdtoast.MDToast;

import jp.wasabeef.richeditor.RichEditor;
import layout.NotesFragment;

public class FullNote extends AppCompatActivity {
    private Long note_id = null;
    private TextView updated_on;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_full_note);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);

        Intent intent = getIntent();
        note_id = intent.getExtras().getLong("noteId");
        Note note1 = Note.findById(Note.class, note_id);

        //set data
        if (note1.getTitle() == null) {
            setTitle("Empty Title");
        } else {
            setTitle(note1.getTitle());
        }
        RichEditor richEditor = (RichEditor) findViewById(R.id.notes_editor);
        richEditor.setEditorBackgroundColor(Color.TRANSPARENT);
        richEditor.setInputEnabled(false);
        richEditor.setHtml(note1.getNote());

        updated_on = (TextView) findViewById(R.id.updated_on);
        updated_on.setText("last updated on: " + note1.getCreated_at());

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
        RichEditor richEditor = (RichEditor) findViewById(R.id.notes_editor);
        richEditor.setEditorBackgroundColor(Color.TRANSPARENT);
        richEditor.setInputEnabled(false);
        richEditor.setHtml(note1.getNote());
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
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
                    item.setIcon(R.mipmap.ic_action_favorite_white);
                    MDToast.makeText(getBaseContext(), note1.getTitle() + " added to favourite", MDToast.LENGTH_SHORT, MDToast.TYPE_SUCCESS).show();
                } else {
                    item.setIcon(R.mipmap.ic_action_favorite_border);
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
