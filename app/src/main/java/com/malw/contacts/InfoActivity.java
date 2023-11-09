package com.malw.contacts;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import java.io.File;
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
        ((ImageView) findViewById(R.id.avatar)).setImageResource(R.drawable.user);
        File avatar = new File(getFilesDir(), info.get("id")+".png");
        if (avatar.exists()) {
            ((ImageView)findViewById(R.id.avatar)).setImageURI(Uri.fromFile(avatar));
        }
    }

    public void call(View view){
        String phone = ((TextView)findViewById(R.id.phone_number)).getText().toString();
        if(!TextUtils.isEmpty(phone)) {
            startActivity(new Intent(Intent.ACTION_DIAL, Uri.parse("tel:" + phone)));
        }else {
            Toast.makeText(InfoActivity.this, "Номер телефона пустой!", Toast.LENGTH_SHORT).show();
        }
    }

    public void message(View view){
        String phone = ((TextView)findViewById(R.id.phone_number)).getText().toString();
        if (!TextUtils.isEmpty(phone)) {
            startActivity(new Intent(Intent.ACTION_SENDTO, Uri.parse("smsto:" + phone)).putExtra("sms_body", ""));
        }
        else {
            Toast.makeText(InfoActivity.this, "Номер телефона пустой!", Toast.LENGTH_SHORT).show();
        }
    }

    public void email(View view){
        String[] email = new String[]{((TextView)findViewById(R.id.email)).getText().toString()};
        if(!email[0].equals("")) {
            Intent intent = new Intent(Intent.ACTION_SEND);
            intent.setType("message/rfc822").putExtra(Intent.EXTRA_EMAIL, email);
            startActivity(Intent.createChooser(intent, "Открыть с помощью:"));
        }
        else{
            Toast.makeText(InfoActivity.this, "Email пустой!", Toast.LENGTH_SHORT).show();
        }
    }
}