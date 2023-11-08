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
        if (convertView == null) convertView = LayoutInflater.from(context).inflate(R.layout.contact_item, parent, false);
        Integer key = (Integer) data.keySet().toArray()[position];
        convertView.setTag(key);
        TextView textView = convertView.findViewById(R.id.name);
        textView.setText(data.get(key));
        File avatar = new File(context.getFilesDir(), key+".png");
        ImageView avatarImage = convertView.findViewById(R.id.avatar);
        if (avatar.exists()) {
            avatarImage.setImageURI(Uri.fromFile(avatar));
        } else {
            avatarImage.setImageDrawable(ResourcesCompat.getDrawable(context.getResources(), R.drawable.user, null));
        }
        convertView.setOnClickListener(v -> context.startActivity(new Intent(context, InfoActivity.class).putExtra("key", (Integer) v.getTag())));
        return convertView;
    }
}