package com.example.elm.login;

import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
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

public class FullNote extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_full_note);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);

        Intent intent = getIntent();
        final Long note_id = intent.getExtras().getLong("noteId");
        Note note1 = Note.findById(Note.class, note_id);

        //set data
        setTitle(note1.getTitle());

        TextView noteDetails = (TextView) findViewById(R.id.note_details);
        noteDetails.setText(note1.getNote());
        ImageView imageView = (ImageView) findViewById(R.id.details_image);
        String imagePath = note1.getImage();
        if (imagePath != null){
            int nh = (int) (BitmapFactory.decodeFile(note1.getImage()).getHeight() * (512.0 / BitmapFactory.decodeFile(note1.getImage()).getWidth()));
            imageView.setImageBitmap(Bitmap.createScaledBitmap(BitmapFactory.decodeFile(note1.getImage()), 512, nh, true));
        }

        FloatingActionButton fab = (FloatingActionButton) findViewById(R.id.fab);
        fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent1 = new Intent(FullNote.this, AddNote.class);
                intent1.putExtra("noteId", note_id);
                startActivity(intent1);
            }
        });
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item){
        switch (item.getItemId()) {
            case android.R.id.home:
                //Write your logic here
                this.finish();
                return true;
            default:
                return super.onOptionsItemSelected(item);
        }
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.menu_full_note, menu);
        return true;
    }
}
