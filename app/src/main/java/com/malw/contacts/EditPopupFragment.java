package com.malw.contacts;

import android.app.AlertDialog;
import android.net.Uri;
import android.os.Bundle;

import androidx.activity.result.ActivityResultLauncher;
import androidx.activity.result.PickVisualMediaRequest;
import androidx.activity.result.contract.ActivityResultContracts;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;

import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.util.HashMap;

public class EditPopupFragment extends Fragment {
    // Фрагмент редактирования контакта, помещается справа, только на планшетах
    int selectedItem;
    ActivityResultLauncher<PickVisualMediaRequest> pickMedia;
    public static EditPopupFragment newInstance(int selectedItem) {
        EditPopupFragment fragment = new EditPopupFragment();
        Bundle args = new Bundle();
        // Передаем айди выбранного контакта в аргументы
        args.putInt("selectedItem", selectedItem);
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        selectedItem = getArguments().getInt("selectedItem");
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View root = inflater.inflate(R.layout.fragment_edit_popup, container, false);
        // Получаем выбранный элемент из аргументов. Если его нет, значит создается новый контакт, значения подставлять не нужно.
        selectedItem = getArguments().getInt("selectedItem", -1);
        if(selectedItem != -1) {
            // Если же выбран какой-то ID, получаем информацию о контакте из БД и подставляем куда надо
            HashMap<String, String> info = MainActivity.db.getContactInfo(selectedItem);
            ((TextView) root.findViewById(R.id.name)).setText(info.get("name"));
            ((TextView) root.findViewById(R.id.surname)).setText(info.get("surname"));
            ((TextView) root.findViewById(R.id.p_number)).setText(info.get("phone"));
            ((TextView) root.findViewById(R.id.mail)).setText(info.get("email"));
            ((TextView) root.findViewById(R.id.address)).setText(info.get("address"));
            // Если файл с аватаркой существует, то он устанавливается в ImageView. Иначе в ImageView устанавливается стандартная аватарка.
            // Если изображение было изменено, но название файла осталось таким же - фотография не обновится.
            // Поэтому используем такой костыль: Заранее устанавливаем стандартную аватарку в любом случае.
            ((ImageView) root.findViewById(R.id.avatar)).setImageResource(R.drawable.photo);
            File avatar = new File(getContext().getFilesDir(), info.get("id")+".png");
            if (avatar.exists()) {
                ((ImageView) root.findViewById(R.id.avatar)).setImageURI(Uri.fromFile(avatar));
            }
        }
        // Используем Result Activity API для запуска встроенного Activity PickVisualMedia
        // https://developer.android.com/training/data-storage/shared/photopicker
        pickMedia = registerForActivityResult(new ActivityResultContracts.PickVisualMedia(), uri -> {
            ImageView avatar = root.findViewById(R.id.avatar);
            // Если пользователь выбрал аватарку (uri не null), то копируем файл в папку с кешем.
            if (uri != null) {
                // /data/data/com.malw.contacts/cache/temp_photo.png
                // либо на новых андроидах /data/user/0/com.malw.contacts/cache/temp_photo.png
                File img = new File(getContext().getCacheDir(), "temp_photo.png");
                copy(uri, img);
                avatar.setImageURI(Uri.fromFile(img));
                // Когда установалено фото - у ImageView тег 1
                // Когда установлена стандартная аватарка - у ImageView тег 0
                avatar.setTag(1);
            }
            else {
                avatar.setImageResource(R.drawable.photo);
                avatar.setTag(0);
            }
        });
        root.findViewById(R.id.save).setOnClickListener(v -> {
            // Если в поле ввода имени ничего нет, выдавать предупреждение. Иначе сохранять.
            if(((TextView) root.findViewById(R.id.name)).getText().toString().equals("")) {
                new AlertDialog.Builder(getActivity()).setTitle("Изменение контакта")
                        .setMessage("Имя не может быть пустым!")
                        .setPositiveButton(android.R.string.ok, null)
                        .setIcon(android.R.drawable.ic_dialog_alert).show();
            }
            else {
                FragmentManager fm = ((MainActivity) getContext()).getSupportFragmentManager();
                if (selectedItem == -1) {
                    // Создание нового контакта
                    if (root.findViewById(R.id.avatar).getTag().toString().equals("1"))
                        // Если пользователь установил фото (тег у ImageVIew = 1), то копировать файл из кеша в данные.
                        // Имя файла = *последний сгенерированный айди AUTOINCREMENT + 1*.png
                        copy(new File(requireContext().getCacheDir(), "temp_photo.png"), new File(requireContext().getFilesDir(), (MainActivity.db.getLastId() + 1) + ".png"));
                    MainActivity.db.addContact(
                            ((TextView) root.findViewById(R.id.name)).getText().toString(),
                            ((TextView) root.findViewById(R.id.surname)).getText().toString(),
                            ((TextView) root.findViewById(R.id.p_number)).getText().toString(),
                            ((TextView) root.findViewById(R.id.mail)).getText().toString(),
                            ((TextView) root.findViewById(R.id.address)).getText().toString(),
                            getContext()
                    );
                    ((MainFragment)(fm.findFragmentById(R.id.mainFragment))).refresh();
                    fm.beginTransaction().replace(R.id.infoFragment, new ChooseContactFragment()).commit();
                } else {
                    // Изменение существующего контакта
                    if (root.findViewById(R.id.avatar).getTag().toString().equals("1")) {
                        // Если пользователь установил фото (тег у ImageVIew = 1), то копировать файл из кеша в данные.
                        // Имя файла = *выбраннный айди*.png
                        copy(new File(requireContext().getCacheDir(), "temp_photo.png"), new File(requireContext().getFilesDir(), selectedItem + ".png"));
                    } else {
                        // Раньше была фотография, а теперь нет? Значит файл надо удалять.
                        new File(requireContext().getFilesDir(), selectedItem + ".png").delete();
                    }
                    MainActivity.db.updateContact(
                            selectedItem,
                            ((TextView) root.findViewById(R.id.name)).getText().toString(),
                            ((TextView) root.findViewById(R.id.surname)).getText().toString(),
                            ((TextView) root.findViewById(R.id.p_number)).getText().toString(),
                            ((TextView) root.findViewById(R.id.mail)).getText().toString(),
                            ((TextView) root.findViewById(R.id.address)).getText().toString(),
                            getContext()
                    );
                    ((MainFragment)(fm.findFragmentById(R.id.mainFragment))).refresh();
                    fm.beginTransaction().replace(R.id.infoFragment, InfoFragment.newInstance(selectedItem)).commit();
                }
            }
        });
        // Кнопка выбора фото
        root.findViewById(R.id.avatar).setOnClickListener(l -> {
            pickMedia.launch(new PickVisualMediaRequest.Builder()
                    .setMediaType(ActivityResultContracts.PickVisualMedia.ImageOnly.INSTANCE)
                    .build());
        });
        return root;
    }
    // Две перегрузки метода для копирования файлов
    private void copy(File from, File to) {
        try (OutputStream os = new FileOutputStream(to); InputStream is = new FileInputStream(from)) {
            byte[] buffer = new byte[8192];
            int bytesRead;
            while ((bytesRead = is.read(buffer)) != -1) os.write(buffer, 0, bytesRead);
            os.flush();
        } catch (IOException e) {
            Log.e("IOException", e.getMessage());
        }
    }

    private void copy(Uri from, File to) {
        try (OutputStream os = new FileOutputStream(to); InputStream is = requireContext().getContentResolver().openInputStream(from)) {
            byte[] buffer = new byte[8192];
            int bytesRead;
            while ((bytesRead = is.read(buffer)) != -1) os.write(buffer, 0, bytesRead);
            os.flush();
        } catch (IOException e) {
            Log.e("IOException", e.getMessage());
        }
    }
}