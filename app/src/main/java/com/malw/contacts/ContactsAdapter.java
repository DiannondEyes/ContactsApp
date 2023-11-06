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
        if (convertView == null) convertView = LayoutInflater.from(context).inflate(R.layout.contact_item, parent, false);
        Integer key = (Integer) data.keySet().toArray()[position];
        convertView.setTag(key);
        TextView textView = convertView.findViewById(R.id.name);
        textView.setText(data.get(key));
        ((ImageView) convertView.findViewById(R.id.avatar)).setImageURI(Uri.fromFile(new File(context.getFilesDir(), key +".png")));
        convertView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Integer selectedItemKey = (Integer) v.getTag();
                Intent intent = new Intent(context, InfoActivity.class);
                intent.putExtra("selectedItemKey", selectedItemKey);
                context.startActivity(intent);
            }
        });
        return convertView;
    }
}