package com.malw.contacts;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.TextView;

import java.util.HashMap;

public class InfoActivity extends AppCompatActivity {

    HashMap<String, String> info;
    int selectedItem;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_info);
        selectedItem = getIntent().getIntExtra("key", 0);
        refresh();
    }

    public void edit(View view) {
        startActivity(new Intent(this, EditActivity.class).putExtra("selectedItem", selectedItem));
    }

    @Override
    protected void onResume() {
        super.onResume();
        refresh();
    }

    void refresh() {
        info = MainActivity.db.getContactInfo(selectedItem);
        ((TextView)findViewById(R.id.name_surname)).setText(String.format("%s %s", info.get("name"), info.get("surname")));
        ((TextView)findViewById(R.id.phone_number)).setText(info.get("phone"));
        ((TextView)findViewById(R.id.email)).setText(info.get("email"));
        ((TextView)findViewById(R.id.address)).setText(info.get("address"));
    }
}