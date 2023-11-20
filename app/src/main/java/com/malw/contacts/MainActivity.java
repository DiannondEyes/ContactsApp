package com.malw.contacts;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.ListView;

import java.util.HashMap;

public class MainActivity extends AppCompatActivity {
    static DataBase db;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        // Создаем объект DataBase для взаимодействия с ним.
        db = new DataBase(this);
    }

    // Вызывается при нажатии на кнопку создания нового контакта
    public void edit(View view) {
        // Запускаем activity для редактирования, ничего туда не передаем, чтобы поля были пустые
        startActivity(new Intent(MainActivity.this, EditActivity.class));
    }
}