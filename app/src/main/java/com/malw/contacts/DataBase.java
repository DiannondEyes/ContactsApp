package com.malw.contacts;

import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;

public class DataBase {

    SQLiteDatabase baseContacts;

    public DataBase(Context context){
        baseContacts = context.openOrCreateDatabase("contacts.db", Context.MODE_PRIVATE, null);
        baseContacts.execSQL("CREATE TABLE IF NOT EXISTS contacts (id INTEGER PRIMARY KEY AUTOINCREMENT UNIQUE , name TEXT, surname TEXT, phone TEXT, email TEXT, address TEXT, photo TEXT) ");
    }

    public void addContact(String name, String surname, String phone, String email, String address, String photo){
        baseContacts.execSQL("INSERT INTO contacts (name, surname, phone, email, address) VALUES (\"" + name + "\", \"" + surname + "\", \"" + email + "\", \"" + address + "\", \"" + photo + "\")");
    }

    public String[] getContact(int id){
        Cursor query = baseContacts.rawQuery("SELECT " + id + " FROM contacts;", null);
        // TODO: return new String[]{query.getString(0), query.}
        return new String[0];
    }


}
