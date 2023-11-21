package com.malw.contacts;

import android.os.Bundle;
import androidx.fragment.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ListView;

import java.util.HashMap;

public class MainFragment extends Fragment {
    ListView list;
    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Override
    public void onResume() {
        super.onResume();
        // Вызываем refresh для обновления информации о контактах и добавления их в список
        refresh();
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View root = inflater.inflate(R.layout.fragment_main, container, false);
        list = root.findViewById(R.id.listOfTitles);
        // Вызываем refresh для обновления информации о контактах и добавления их в список
        refresh();
        return root;
    }

    void refresh() {
        // Получаем список контактов, устанавливаем его в адаптер
        HashMap<Integer, String> data = MainActivity.db.getAllContacts();
        list.setAdapter(new ContactsAdapter(getActivity(), data));
    }
}