package com.malw.contacts;

import androidx.appcompat.app.AppCompatActivity;

import android.os.Bundle;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.ListView;

public class MainActivity extends AppCompatActivity {
    private static DataBase db;

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

    }
    void refresh() {
        ListView list = findViewById(R.id.listOfTitles);
        String[] titles = new String[]{};
        list.setAdapter(new ArrayAdapter<>(this, android.R.layout.simple_list_item_1, titles));
    }
}