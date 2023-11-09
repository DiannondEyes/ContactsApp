package com.malw.contacts;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.View;
import android.widget.TextView;
import android.widget.Toast;

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

    public void call(View view){
        String phone = ((TextView)findViewById(R.id.phone_number)).getText().toString();
        if(!TextUtils.isEmpty(phone)) {
            String dial = "tel:" + phone;
            startActivity(new Intent(Intent.ACTION_DIAL, Uri.parse(dial)));
        }else {
            Toast.makeText(InfoActivity.this, "Enter a phone number", Toast.LENGTH_SHORT).show();
        }
    }

    public void message(View view){
        String phone = ((TextView)findViewById(R.id.phone_number)).getText().toString();
        if(!TextUtils.isEmpty(phone)) {
            Intent smsIntent = new Intent(Intent.ACTION_SENDTO, Uri.parse("smsto:" + phone));
            smsIntent.putExtra("sms_body", "");
            startActivity(smsIntent);
    }
    }

    public void email(View view){
        Intent intent = new Intent(Intent.ACTION_SEND);
        intent.setType("message/rfc822");
        intent.putExtra(Intent.EXTRA_EMAIL, new String[]{((TextView)findViewById(R.id.email)).getText().toString()});
        startActivity(Intent.createChooser(intent, "Открыть с помощью:"));
    }
}