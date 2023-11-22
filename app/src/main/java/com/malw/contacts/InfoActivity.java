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
    // Активити просмотра контакта, только на телефонах
    HashMap<String, String> info;
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
        if (getResources().getConfiguration().smallestScreenWidthDp >= 600) {
            getSupportFragmentManager().beginTransaction().replace(R.id.infoFragment, EditPopupFragment.newInstance(-1)).commit();
        } else {
            startActivity(new Intent(InfoActivity.this, EditActivity.class).putExtra("selectedItem", selectedItem));
        }
    }

    @Override
    protected void onResume() {
        super.onResume();
        refresh();
    }

    void refresh() {
        // Получаем информацию о контакте из БД и подставляем куда надо
        info = MainActivity.db.getContactInfo(selectedItem);
        ((TextView)findViewById(R.id.name_surname)).setText(String.format("%s %s", info.get("name"), info.get("surname")));
        ((TextView)findViewById(R.id.phone_number)).setText(info.get("phone"));
        ((TextView)findViewById(R.id.email)).setText(info.get("email"));
        ((TextView)findViewById(R.id.address)).setText(info.get("address"));
        // Если файл с аватаркой существует, то он устанавливается в ImageView. Иначе в ImageView устанавливается стандартная аватарка.
        // Если изображение было изменено, но название файла осталось таким же - фотография не обновится.
        // Поэтому используем такой костыль: Заранее устанавливаем стандартную аватарку в любом случае.
        ((ImageView) findViewById(R.id.avatar)).setImageResource(R.drawable.user);
        File avatar = new File(getFilesDir(), info.get("id")+".png");
        if (avatar.exists()) {
            ((ImageView)findViewById(R.id.avatar)).setImageURI(Uri.fromFile(avatar));
        }
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