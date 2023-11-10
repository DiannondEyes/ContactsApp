package com.malw.contacts;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.ListView;

import java.util.HashMap;

public class MainActivity extends AppCompatActivity {
    static DataBase db;
    ListView list;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        // Создаем объект DataBase для взаимодействия с ним.
        db = new DataBase(this);
        list = findViewById(R.id.listOfTitles);
        // Вызываем refresh для обновления информации о контактах и добавления их в список
        refresh();
    }

    @Override
    protected void onResume() {
        super.onResume();
        // Вызываем refresh для обновления информации о контактах и добавления их в список
        refresh();
    }

    // Вызывается при нажатии на кнопку создания нового контакта
    public void edit(View view) {
        // Запускаем activity для редактирования, ничего туда не передаем, чтобы поля были пустые
        startActivity(new Intent(MainActivity.this, EditActivity.class));
    }

    void refresh() {
        // Получаем список контактов, устанавливаем его в адаптер
        HashMap<Integer, String> data = db.getAllContacts();
        list.setAdapter(new ContactsAdapter(this, data));
    }
}