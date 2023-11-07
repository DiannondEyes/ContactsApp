package com.malw.contacts;

import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;

import java.util.HashMap;

public class DataBase {

    final SQLiteDatabase baseContacts;

    public DataBase(Context context){
        baseContacts = context.openOrCreateDatabase("contacts.db", Context.MODE_PRIVATE, null);
        baseContacts.execSQL("CREATE TABLE IF NOT EXISTS contacts (id INTEGER PRIMARY KEY AUTOINCREMENT UNIQUE, name TEXT, surname TEXT, phone TEXT, email TEXT, address TEXT)");
    }

    public void addContact(String name, String surname, String phone, String email, String address){
        baseContacts.execSQL("INSERT INTO contacts (name, surname, phone, email, address) VALUES (?, ?, ?, ?, ?)", new String[]{name, surname, phone, email, address});
    }

    public void updateContact(int id, String name, String surname, String phone, String email, String address){
        baseContacts.execSQL("UPDATE contacts SET name = ?, surname = ? ,phone = ?, email = ?, address = ? WHERE id = " + id, new String[]{name, surname, phone, email, address});
    }

    public HashMap<String, String> getContactInfo(int id){
        HashMap<String, String> info = new HashMap<>();
        try (Cursor cursor = baseContacts.rawQuery("SELECT name, surname, phone, email, address  FROM contacts WHERE id=" + id, null)) {
            if (cursor.moveToFirst()) {
                info.put("name", cursor.getString(0));
                info.put("surname", cursor.getString(1));
                info.put("phone", cursor.getString(2));
                info.put("email", cursor.getString(3));
                info.put("address", cursor.getString(4));
            }
        }
        return info;
    }

//    public String[] getContact(int id){
//        Cursor query = baseContacts.rawQuery("SELECT " + id + " FROM contacts;", null);
//        return new String[]{query.getString(0), query.}
//    }

    public HashMap<Integer, String> getAllContacts() {
        HashMap<Integer, String> contacts = new HashMap<>();
        try (Cursor cursor = baseContacts.rawQuery("SELECT id, name, surname FROM contacts", null)) {
            if (cursor.moveToFirst()) {
                do contacts.put(cursor.getInt(0), cursor.getString(1) + cursor.getString(2));
                while (cursor.moveToNext());
            }
        }
        return contacts;
    }
}
