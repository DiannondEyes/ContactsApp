package com.malw.contacts;

import androidx.appcompat.app.AppCompatActivity;

import android.os.Bundle;
import android.view.View;
import android.widget.TextView;

import java.util.HashMap;

public class EditActivity extends AppCompatActivity {

    int selectedItem;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_edit);
        setTitle("Редактирование контакта");

        selectedItem = getIntent().getIntExtra("selectedItem", -1);

        if(selectedItem != -1) {
            HashMap<String, String> info = MainActivity.db.getContactInfo(selectedItem);

            ((TextView) findViewById(R.id.name)).setText(info.get("name"));
            ((TextView) findViewById(R.id.surname)).setText(info.get("surname"));
            ((TextView) findViewById(R.id.p_number)).setText(info.get("phone"));
            ((TextView) findViewById(R.id.mail)).setText(info.get("email"));
            ((TextView) findViewById(R.id.address)).setText(info.get("address"));
        }
    }

    public void edit(View view) {
        if(selectedItem == -1){
            MainActivity.db.addContact(
                    ((TextView)findViewById(R.id.name)).getText().toString(),
                    ((TextView)findViewById(R.id.surname)).getText().toString(),
                    ((TextView)findViewById(R.id.p_number)).getText().toString(),
                    ((TextView)findViewById(R.id.mail)).getText().toString(),
                    ((TextView)findViewById(R.id.address)).getText().toString()
            );
        }
        else{
            MainActivity.db.updateContact(
                    selectedItem,
                    ((TextView)findViewById(R.id.name)).getText().toString(),
                    ((TextView)findViewById(R.id.surname)).getText().toString(),
                    ((TextView)findViewById(R.id.p_number)).getText().toString(),
                    ((TextView)findViewById(R.id.mail)).getText().toString(),
                    ((TextView)findViewById(R.id.address)).getText().toString()
            );
        }
        finish();
    }
}