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


    int selectedItem;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_info);
        // Получаем ID выбранного контакта из intent'а
        selectedItem = getIntent().getIntExtra("key", 0);
        // Вызываем refresh для обновления информации о контактах и добавления их в список
        refresh();
    }

    // При нажатии на кнопку редактирования, вызывается этот метод, в intent передается ID выбранного фото.
    public void edit(View view) {
        EditDialogFragment dialog = new EditDialogFragment();dialog.show(getSupportFragmentManager(), "custom");
    }

    @Override
    protected void onResume() {
        super.onResume();
        refresh();
    }

    void refresh() {

    }

    // Вызывается при нажатии на кнопку звонка
    public void call(View view){
        String phone = ((TextView)findViewById(R.id.phone_number)).getText().toString();
        if(!TextUtils.isEmpty(phone)) {
            startActivity(new Intent(Intent.ACTION_DIAL, Uri.parse("tel:" + phone)));
        }else {
            Toast.makeText(InfoActivity.this, "Номер телефона пустой!", Toast.LENGTH_SHORT).show();
        }
    }

    // Вызывается при нажатии на кнопку SMS
    public void message(View view){
        String phone = ((TextView)findViewById(R.id.phone_number)).getText().toString();
        if (!TextUtils.isEmpty(phone)) {
            startActivity(new Intent(Intent.ACTION_SENDTO, Uri.parse("smsto:" + phone)).putExtra("sms_body", ""));
        }
        else {
            Toast.makeText(InfoActivity.this, "Номер телефона пустой!", Toast.LENGTH_SHORT).show();
        }
    }

    // Вызывается при нажатии на кнопку Email
    public void email(View view){
        String[] email = new String[]{((TextView)findViewById(R.id.email)).getText().toString()};
        if (!email[0].equals("")) {
            Intent intent = new Intent(Intent.ACTION_SEND);
            intent.setType("message/rfc822").putExtra(Intent.EXTRA_EMAIL, email);
            startActivity(Intent.createChooser(intent, "Открыть с помощью:"));
        }
        else {
            Toast.makeText(InfoActivity.this, "Email пустой!", Toast.LENGTH_SHORT).show();
        }
    }
}