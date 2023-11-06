package com.malw.contacts;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;

public class InfoActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_info);

        int selectedItem = getIntent().getIntExtra("selectedItemKey", -1);
        String[] info =

    }

    public void edit(View view) {
        startActivity(new Intent(InfoActivity.this, EditActivity.class));
    }
}