package com.elm.mycheck.login;

import android.content.SharedPreferences;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.MenuItem;

import com.elm.mycheck.login.adapter.CategoriesAdapter;
import com.elm.mycheck.login.model.Category;

import java.util.ArrayList;
import java.util.List;

public class ManageCategories extends AppCompatActivity {

    private RecyclerView recyclerView;
    private CategoriesAdapter adapter;
    public List<Category> allCategories = new ArrayList<>();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        SharedPreferences preferences = getSharedPreferences("myPref", 0);
        //Boolean fk = getSharedPreferences("myPref", 0).getBoolean("loggedIn", false);
        String theme = getSharedPreferences("myPref", 0).getString("theme", "Default");
        setTheme(R.style.AppTheme_Primary);

        setContentView(R.layout.activity_manage_categories);
        setTitle("Manage Categories");
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);

        recyclerView = (RecyclerView) findViewById(R.id.categories_recycler);
        allCategories = Category.listAll(Category.class);

        adapter = new CategoriesAdapter(allCategories);

        recyclerView.setLayoutManager(new LinearLayoutManager(this));
        recyclerView.setAdapter(adapter);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case android.R.id.home:
                this.finish();
                return true;
            default:
                return super.onOptionsItemSelected(item);
        }
    }
}
