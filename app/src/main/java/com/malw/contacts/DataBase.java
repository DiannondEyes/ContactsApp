package com.malw.contacts;

import android.annotation.SuppressLint;
import android.content.Context;
import android.database.Cursor;
import android.database.CursorIndexOutOfBoundsException;
import android.database.sqlite.SQLiteConstraintException;
import android.database.sqlite.SQLiteDatabase;
import android.os.Handler;
import android.os.Looper;
import android.util.Log;

import java.io.DataOutputStream;
import java.io.IOException;

import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.google.gson.JsonParser;

import java.net.HttpURLConnection;
import java.net.URL;
import java.nio.charset.StandardCharsets;
import java.util.HashMap;
import java.util.Scanner;
@SuppressWarnings("deprecation")
public class DataBase {

    final SQLiteDatabase db;
    private Runnable updateTaskCallback;

    // При создании объекта класса DataBase инициализируется БД и создается таблица, если она не существует
    public DataBase(Context context) {
        db = context.openOrCreateDatabase("contacts.db", Context.MODE_PRIVATE, null);
        db.execSQL("CREATE TABLE IF NOT EXISTS contacts (id INTEGER PRIMARY KEY UNIQUE, name TEXT, surname TEXT, phone TEXT, email TEXT, address TEXT)");
    }

    // Создание нового контакта
    public void addContact(String name, String surname, String phone, String email, String address, Context context) {
        db.execSQL("INSERT INTO contacts (name, surname, phone, email, address) VALUES (?, ?, ?, ?, ?)", new String[]{name, surname, phone, email, address});
        new Thread(() -> {
            HttpURLConnection connection = null;
            try {
                connection = (HttpURLConnection) new URL("http://37.77.105.18/api/Contacts").openConnection();
                connection.setRequestMethod("POST");
                connection.setRequestProperty("Content-Type", "application/json");
                connection.setDoOutput(true);
                String jsonInputString = String.format("{\"name\": \"%s\", \"surname\": \"%s\", \"phoneNumber\": \"%s\", \"address\": \"%s\", \"email\": \"%s\"}",
                        name, surname, phone, address, email);
                try (DataOutputStream wr = new DataOutputStream(connection.getOutputStream())) {
                    byte[] input = jsonInputString.getBytes(StandardCharsets.UTF_8);
                    wr.write(input, 0, input.length);
                }
                if (connection.getResponseCode() != 200) {
                    new Handler(Looper.getMainLooper()).post(() -> {
                        new android.app.AlertDialog.Builder(context).setTitle("Не удалось отправить данные на сервер!")
                                .setMessage("Контакт добавлен, но не был отправлен на сервер.")
                                .setPositiveButton("OK", (dialog, which) -> dialog.dismiss())
                                .show();
                    });
                }
            } catch (IOException | SQLiteConstraintException e) {
                Log.e("IOException", e.getMessage());
                new android.app.AlertDialog.Builder(context).setTitle("Не удалось отправить данные на сервер!")
                        .setMessage("Контакт добавлен, но не был отправлен на сервер.")
                        .setPositiveButton("OK", (dialog, which) -> dialog.dismiss())
                        .show();

            } finally {
                if (connection != null) connection.disconnect();
                if (updateTaskCallback != null) {
                    updateTaskCallback.run();
                }
            }
        }).start();
    }

    // Обновление существующего контакта
    public void updateContact(int id, String name, String surname, String phone, String email, String address, Context context) {
        db.execSQL("UPDATE contacts SET name = ?, surname = ? ,phone = ?, email = ?, address = ? WHERE id = ?", new String[]{name, surname, phone, email, address, String.valueOf(id)});
        new Thread(() -> {
            HttpURLConnection connection = null;
            try {
                connection = (HttpURLConnection) new URL("http://37.77.105.18/api/Contacts/"+id).openConnection();
                connection.setRequestMethod("PUT");
                connection.setRequestProperty("Content-Type", "application/json");
                connection.setDoOutput(true);
                @SuppressLint("DefaultLocale")
                String jsonInputString = String.format("{\"id\": %d, \"name\": \"%s\", \"surname\": \"%s\", \"phoneNumber\": \"%s\", \"address\": \"%s\", \"email\": \"%s\"}",
                        id, name, surname, phone, address, email);
                try (DataOutputStream wr = new DataOutputStream(connection.getOutputStream())) {
                    byte[] input = jsonInputString.getBytes(StandardCharsets.UTF_8);
                    wr.write(input, 0, input.length);
                }
                if (connection.getResponseCode() != 200) {
                    new Handler(Looper.getMainLooper()).post(() -> {
                        new android.app.AlertDialog.Builder(context).setTitle("Не удалось отправить данные на сервер!")
                                .setMessage("Контакт отредактирован, но не был отправлен на сервер.")
                                .setPositiveButton("OK", (dialog, which) -> dialog.dismiss())
                                .show();
                    });
                }
            } catch (IOException | SQLiteConstraintException e) {
                Log.e("IOException", e.getMessage());
                new Handler(Looper.getMainLooper()).post(() -> {
                    new android.app.AlertDialog.Builder(context).setTitle("Не удалось отправить данные на сервер!")
                            .setMessage("Контакт отредактирован, но не был отправлен на сервер.")
                            .setPositiveButton("OK", (dialog, which) -> dialog.dismiss())
                            .show();
                });
            } finally {
                if (connection != null) connection.disconnect();
                if (updateTaskCallback != null) {
                    updateTaskCallback.run();
                }
            }
        }).start();
    }


    // Получение информации об определенном контакте. Возвращается HashMap, где ключи - поля базы данных, значения - значения соответственно.
    public HashMap<String, String> getContactInfo(int id) {
        HashMap<String, String> info = new HashMap<>();
        try (Cursor cursor = db.rawQuery("SELECT id, name, surname, phone, email, address FROM contacts WHERE id=" + id, null)) {
            if (cursor.moveToFirst()) {
                info.put("id", cursor.getString(0));
                info.put("name", cursor.getString(1));
                info.put("surname", cursor.getString(2));
                info.put("phone", cursor.getString(3));
                info.put("email", cursor.getString(4));
                info.put("address", cursor.getString(5));
            }
        }
        return info;
    }

    // Получение списка всех контактов. Возвращается HashMap, где ключи - ID контактов, значения - имя + фамилия
    public HashMap<Integer, String> getAllContacts() {
        HashMap<Integer, String> contacts = new HashMap<>();
        try (Cursor cursor = db.rawQuery("SELECT id, name, surname FROM contacts", null)) {
            if (cursor.moveToFirst()) {
                do contacts.put(cursor.getInt(0), cursor.getString(1) + " " + cursor.getString(2)); // Заносим в значения имя и фамилию с пробелом
                while (cursor.moveToNext());
            }
        }
        return contacts;
    }

    // Получение последнего сгенерированного ID, который генерируется автоматически с помощью AUTOINCREMENT
    public int getLastId() {
        // Выполняется запрос в служебную таблицу
        try (Cursor cursor = db.rawQuery("select seq from sqlite_sequence where name=\"contacts\"", null)) {
            cursor.moveToFirst();
            try {
                return cursor.getInt(0);
            } catch (CursorIndexOutOfBoundsException e) {
                return 0; // Если ни одного ID не существет, возвращаем 0
            }

        }
    }

    public void setUpdateTaskCallback(Runnable callback) {
        this.updateTaskCallback = callback;
    }

    // Получение данных с сервера
    public void updateData(Context context) {
        new Thread(() -> {
            HttpURLConnection connection = null;
            try {
                connection = (HttpURLConnection) new URL("http://37.77.105.18/api/Contacts").openConnection();
                connection.setRequestMethod("GET");
                connection.getResponseCode();
                if (connection.getResponseCode() == 200) {
                    JsonObject contacts = new JsonParser().parse(new Scanner(connection.getInputStream(), "UTF-8").useDelimiter("\\A").next()).getAsJsonObject();
                    db.execSQL("DELETE FROM contacts");
                    for (JsonElement contact : contacts.get("contacts").getAsJsonArray()) {
                        JsonObject ctct = contact.getAsJsonObject();
                        Log.d("contact", String.valueOf(ctct.get("id")));
                        db.execSQL("INSERT INTO contacts (id, name, surname, phone, email, address) VALUES (?, ?, ?, ?, ?, ?)", new String[]{ctct.get("id").getAsString(), ctct.get("name").getAsString(), ctct.get("surname").getAsString(), ctct.get("phoneNumber").getAsString(), ctct.get("email").getAsString(), ctct.get("address").getAsString()});
                    }
                }
                else {
                    new Handler(Looper.getMainLooper()).post(() -> {
                        new android.app.AlertDialog.Builder(context).setTitle("Не удалось получить данные с сервера!")
                                .setMessage("Контакты не были синхронизированы с сервером!")
                                .setPositiveButton("OK", (dialog, which) -> dialog.dismiss())
                                .show();
                    });
                }
            } catch (IOException | SQLiteConstraintException e) {
                Log.e("IOException", e.getMessage());
                new Handler(Looper.getMainLooper()).post(() -> {
                    new android.app.AlertDialog.Builder(context).setTitle("Не удалось получить данные с сервера!")
                            .setMessage("Контакты не были синхронизированы с сервером!")
                            .setPositiveButton("OK", (dialog, which) -> dialog.dismiss())
                            .show();
                });
            } finally {
                if (connection != null) connection.disconnect();
                if (updateTaskCallback != null) {
                    updateTaskCallback.run();
                }
            }
        }).start();
    }
}