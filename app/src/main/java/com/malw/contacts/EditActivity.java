package com.malw.contacts;

import androidx.appcompat.app.AppCompatActivity;

import android.os.Bundle;
import android.view.View;
import android.widget.TextView;

public class EditActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_edit);
        setTitle("Редактирование контакта");
    }

    public void edit(View view) {
        MainActivity.db.addContact(
            ((TextView)findViewById(R.id.name)).getText().toString(),
            ((TextView)findViewById(R.id.surname)).getText().toString(),
            ((TextView)findViewById(R.id.p_number)).getText().toString(),
            ((TextView)findViewById(R.id.mail)).getText().toString(),
            ((TextView)findViewById(R.id.address)).getText().toString()
        );
        finish();
    }
}