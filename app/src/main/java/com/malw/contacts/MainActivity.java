package com.malw.contacts;

import androidx.appcompat.app.AppCompatActivity;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;

import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.google.gson.JsonParser;

import java.io.IOException;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.Map;
import java.util.Scanner;


public class MainActivity extends AppCompatActivity {
    // Активити со списком контактов, только на телефонах
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
        if (getResources().getConfiguration().smallestScreenWidthDp >= 600) {
            getSupportFragmentManager().beginTransaction().replace(R.id.infoFragment, EditPopupFragment.newInstance(-1)).commit();
        } else {
            startActivity(new Intent(MainActivity.this, EditActivity.class).putExtra("key",-1));
        }
    }
}