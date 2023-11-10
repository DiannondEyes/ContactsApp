package com.malw.contacts;

import android.content.Context;
import android.database.Cursor;
import android.database.CursorIndexOutOfBoundsException;
import android.database.sqlite.SQLiteDatabase;

import java.util.HashMap;

public class DataBase {

    final SQLiteDatabase baseContacts;

    // При создании объекта класса DataBase инициализируется БД и создается таблица, если она не существует
    public DataBase(Context context) {
        baseContacts = context.openOrCreateDatabase("contacts.db", Context.MODE_PRIVATE, null);
        baseContacts.execSQL("CREATE TABLE IF NOT EXISTS contacts (id INTEGER PRIMARY KEY AUTOINCREMENT UNIQUE, name TEXT, surname TEXT, phone TEXT, email TEXT, address TEXT)");
    }

    // Создание нового контакта
    public void addContact(String name, String surname, String phone, String email, String address) {
        baseContacts.execSQL("INSERT INTO contacts (name, surname, phone, email, address) VALUES (?, ?, ?, ?, ?)", new String[]{name, surname, phone, email, address});
    }

    // Обновление существующего контакта
    public void updateContact(int id, String name, String surname, String phone, String email, String address) {
        baseContacts.execSQL("UPDATE contacts SET name = ?, surname = ? ,phone = ?, email = ?, address = ? WHERE id = ?", new String[]{name, surname, phone, email, address, String.valueOf(id)});
    }

    // Получение информации об определенном контакте. Возвращается HashMap, где ключи - поля базы данных, значения - значения соответственно.
    public HashMap<String, String> getContactInfo(int id) {
        HashMap<String, String> info = new HashMap<>();
        try (Cursor cursor = baseContacts.rawQuery("SELECT id, name, surname, phone, email, address FROM contacts WHERE id=" + id, null)) {
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
        try (Cursor cursor = baseContacts.rawQuery("SELECT id, name, surname FROM contacts", null)) {
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
        try (Cursor cursor = baseContacts.rawQuery("select seq from sqlite_sequence where name=\"contacts\"", null)) {
            cursor.moveToFirst();
            try {
                return cursor.getInt(0);
            }
            catch (CursorIndexOutOfBoundsException e) {
                return 0; // Если ни одного ID не существет, возвращаем 0
            }

        }
    }
}
