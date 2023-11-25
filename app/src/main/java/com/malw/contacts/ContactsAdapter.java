package com.malw.contacts;

import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.view.LayoutInflater;
import android.view.ViewGroup;
import android.view.View;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.core.content.res.ResourcesCompat;

import java.io.File;
import java.util.HashMap;

public class ContactsAdapter extends BaseAdapter {
    private final Context context;
    private final HashMap<Integer, String> data;

    ContactsAdapter(Context context, HashMap<Integer, String> data) {
        this.context = context;
        this.data = data;
    }

    @Override
    public int getCount() {
        return data.size();
    }

    @Override
    public Object getItem(int position) {
        return data.get(position);
    }

    @Override
    public long getItemId(int position) {
        return position;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        // Реализуем разметку. В data хранится HashMap, где ключ - ID контакта, значение - имя и фамилия.
        // Устанавливаем tag с ID контакта в корневой элемент LinearLayout
        if (convertView == null)
            convertView = LayoutInflater.from(context).inflate(R.layout.contact_item, parent, false);
        Integer key = (Integer) data.keySet().toArray()[position];
        convertView.setTag(key);
        // Устанавливаем значение data (имя и фамилия) в TextView
        TextView textView = convertView.findViewById(R.id.name);
        textView.setText(data.get(key));
        // Если файл с аватаркой существует, то он устанавливается в ImageView. Иначе в ImageView устанавливается стандартная аватарка.

        // Ищем файл *id*.png в папке с данными приложения
        File avatar = new File(context.getFilesDir(), key + ".png");
        ImageView avatarImage = convertView.findViewById(R.id.avatar);
        if (avatar.exists()) {
            // Устанавливаем аватар из файла
            avatarImage.setImageURI(Uri.fromFile(avatar));
        } else {
            // Устанавливаем аватар из drawable
            avatarImage.setImageDrawable(ResourcesCompat.getDrawable(context.getResources(), R.drawable.user, null));
        }
        // Создаем listener для обработки нажатия на LinearLayout. Если телефон - Вызываем InfoActivity (и передаем туда ID выбранного контакта) при выборе контакта. Если планшет - изменяем фрагмент справа на InfoFragment.
        convertView.setOnClickListener(v -> {
            if (context.getResources().getConfiguration().smallestScreenWidthDp >= 600) {
                ((MainActivity) context).getSupportFragmentManager().beginTransaction().replace(R.id.infoFragment, InfoFragment.newInstance((Integer) v.getTag())).commit();
            } else {
                context.startActivity(new Intent(context, InfoActivity.class).putExtra("key", (Integer) v.getTag()));
        }});
        return convertView;
    }
}