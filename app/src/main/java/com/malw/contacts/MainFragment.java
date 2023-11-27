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
    View root;
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
        root = inflater.inflate(R.layout.fragment_main, container, false);
        list = root.findViewById(R.id.listOfTitles);
        // Вызываем refresh для обновления информации о контактах и добавления их в список
        list.setVisibility(View.GONE);
        MainActivity.db.updateData(getContext());
        MainActivity.db.setUpdateTaskCallback(() -> getActivity().runOnUiThread(() -> {
            refresh();
            root.findViewById(R.id.progress).setVisibility(View.GONE);
            list.setVisibility(View.VISIBLE);
        }));
        root.findViewById(R.id.refreshButton).setOnClickListener(l -> {
            list.setVisibility(View.GONE);
            MainActivity.db.updateData(getContext());
            MainActivity.db.setUpdateTaskCallback(() -> getActivity().runOnUiThread(() -> {
                refresh();
                root.findViewById(R.id.progress).setVisibility(View.GONE);
                list.setVisibility(View.VISIBLE);
            }));
        });
        return root;
    }

    void refresh() {
        // Получаем список контактов, устанавливаем его в адаптер
        HashMap<Integer, String> data = MainActivity.db.getAllContacts();
        list.setAdapter(new ContactsAdapter(getActivity(), data));
    }
}