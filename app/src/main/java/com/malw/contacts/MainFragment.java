package com.malw.contacts;

import android.os.Bundle;
import androidx.fragment.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ListView;

import java.util.HashMap;

public class MainFragment extends Fragment {
    // Фрагмент со списком контактов, подставляется слева, только на планшетах
    ListView list;
    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Override
    public void onResume() {
        super.onResume();
        refresh();
        // Вызываем refresh для обновления информации о контактах и добавления их в список
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View root = inflater.inflate(R.layout.fragment_main, container, false);
        list = root.findViewById(R.id.listOfTitles);
        // Вызываем refresh для обновления информации о контактах и добавления их в список
        MainActivity.db.updateData();
        MainActivity.db.setUpdateTaskCallback(() -> getActivity().runOnUiThread(() -> {
            refresh();
            root.findViewById(R.id.progress).setVisibility(View.GONE);
        }));
        return root;
    }

    void refresh() {
        // Получаем список контактов, устанавливаем его в адаптер
        HashMap<Integer, String> data = MainActivity.db.getAllContacts();
        list.setAdapter(new ContactsAdapter(getActivity(), data));
    }
}