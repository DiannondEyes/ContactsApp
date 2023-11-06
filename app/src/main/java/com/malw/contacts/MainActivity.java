package com.malw.contacts;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.ListView;

import java.util.HashMap;

public class MainActivity extends AppCompatActivity {
    static DataBase db;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        db = new DataBase(this);
        refresh();
    }

    @Override
    protected void onResume() {
        super.onResume();
        refresh();
    }

    public void edit(View view) {
        startActivity(new Intent(MainActivity.this, EditActivity.class));
    }
    void refresh() {
        ListView list = findViewById(R.id.listOfTitles);
        HashMap<Integer, String> data = db.getAllContacts();
        list.setAdapter(new ContactsAdapter(this, data));
    }
}