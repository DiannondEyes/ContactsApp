package com.malw.contacts;

import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;

import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;

import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import java.io.File;
import java.util.HashMap;

public class InfoFragment extends Fragment {
    // Фрагмент информации о контакте, только на телефонах
    View root;
    int selectedItem;

    public static InfoFragment newInstance(int key) {
        InfoFragment fragment = new InfoFragment();
        Bundle args = new Bundle();
        // Передаем айди выбранного контакта в аргументы
        args.putInt("key", key);
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        selectedItem = getArguments().getInt("key");
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        root = inflater.inflate(R.layout.fragment_info, container, false);
        refresh();
        // Устанавливаю listener для кнопки звонка, смс, почты
        root.findViewById(R.id.call).setOnClickListener(v -> {
            String phone = ((TextView)root.findViewById(R.id.phone_number)).getText().toString();
            if (!TextUtils.isEmpty(phone)) {
                startActivity(new Intent(Intent.ACTION_DIAL, Uri.parse("tel:" + phone)));
            } else {
                Toast.makeText(getContext(), "Номер телефона пустой!", Toast.LENGTH_SHORT).show();
            }
        });
        root.findViewById(R.id.message).setOnClickListener(v -> {
            String phone = ((TextView)root.findViewById(R.id.phone_number)).getText().toString();
            if (!TextUtils.isEmpty(phone)) {
                startActivity(new Intent(Intent.ACTION_SENDTO, Uri.parse("smsto:" + phone)).putExtra("sms_body", ""));
            }
            else {
                Toast.makeText(getContext(), "Номер телефона пустой!", Toast.LENGTH_SHORT).show();
            }
        });
        root.findViewById(R.id.emailb).setOnClickListener(v -> {
            String[] email = new String[]{((TextView)root.findViewById(R.id.email)).getText().toString()};
            if (!email[0].equals("")) {
                Intent intent = new Intent(Intent.ACTION_SEND);
                intent.setType("message/rfc822").putExtra(Intent.EXTRA_EMAIL, email);
                startActivity(Intent.createChooser(intent, "Открыть с помощью:"));
            }
            else {
                Toast.makeText(getContext(), "Email пустой!", Toast.LENGTH_SHORT).show();
            }
        });
        root.findViewById(R.id.editButton).setOnClickListener(l -> {
            FragmentManager fm = ((MainActivity) getContext()).getSupportFragmentManager();
            fm.beginTransaction().replace(R.id.infoFragment, EditPopupFragment.newInstance(selectedItem)).commit();
        });
        return root;
    }

    @Override
    public void onResume() {
        super.onResume();
        // Вызываем refresh для обновления информации о контактах и добавления их в список
        refresh();
    }

    void refresh() {
        // Получаем информацию о контакте из БД и подставляем куда надо
        HashMap<String, String> info = MainActivity.db.getContactInfo(selectedItem);
        ((TextView) root.findViewById(R.id.name_surname)).setText(String.format("%s %s", info.get("name"), info.get("surname")));
        ((TextView) root.findViewById(R.id.phone_number)).setText(info.get("phone"));
        ((TextView) root.findViewById(R.id.email)).setText(info.get("email"));
        ((TextView) root.findViewById(R.id.address)).setText(info.get("address"));
        // Если файл с аватаркой существует, то он устанавливается в ImageView. Иначе в ImageView устанавливается стандартная аватарка.
        // Если изображение было изменено, но название файла осталось таким же - фотография не обновится.
        // Поэтому используем такой костыль: Заранее устанавливаем стандартную аватарку в любом случае.
        ((ImageView) root.findViewById(R.id.avatar)).setImageResource(R.drawable.user);
        File avatar = new File(getContext().getFilesDir(), info.get("id") + ".png");
        if (avatar.exists()) {
            ((ImageView) root.findViewById(R.id.avatar)).setImageURI(Uri.fromFile(avatar));
        }
    }
}