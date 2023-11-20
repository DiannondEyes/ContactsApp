package com.malw.contacts;

import android.app.AlertDialog;
import android.app.Dialog;
import android.net.Uri;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.TextView;

import androidx.activity.result.ActivityResultLauncher;
import androidx.activity.result.PickVisualMediaRequest;
import androidx.activity.result.contract.ActivityResultContracts;
import androidx.annotation.NonNull;
import androidx.fragment.app.DialogFragment;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;

public class EditDialogFragment extends DialogFragment {

    int selectedItem;
    ActivityResultLauncher<PickVisualMediaRequest> pickMedia;

    @NonNull
    public Dialog onCreateDialog(Bundle savedInstanceState) {
        AlertDialog.Builder builder=new AlertDialog.Builder(getActivity());
        return builder
                .setView(R.layout.dialog_fragment_edit)
                .create();
    }

    public void edit(View view) {
        if(((TextView)view.findViewById(R.id.name)).getText().toString().equals("")) {

        }
        else {
            if (selectedItem == -1) {
                // Создание нового контакта
                if (view.findViewById(R.id.avatar).getTag().toString().equals("1"))
                    // Если пользователь установил фото (тег у ImageVIew = 1), то копировать файл из кеша в данные.
                    // Имя файла = *последний сгенерированный айди AUTOINCREMENT + 1*.png
                    copy(new File(requireContext().getCacheDir(), "temp_photo.png"), new File(requireContext().getFilesDir(), (MainActivity.db.getLastId() + 1) + ".png"));
                MainActivity.db.addContact(
                        ((TextView) view.findViewById(R.id.name)).getText().toString(),
                        ((TextView) view.findViewById(R.id.surname)).getText().toString(),
                        ((TextView) view.findViewById(R.id.p_number)).getText().toString(),
                        ((TextView) view.findViewById(R.id.mail)).getText().toString(),
                        ((TextView) view.findViewById(R.id.address)).getText().toString()
                );
            } else {
                // Изменение существующего контакта
                if (view.findViewById(R.id.avatar).getTag().toString().equals("1")) {
                    // Если пользователь установил фото (тег у ImageVIew = 1), то копировать файл из кеша в данные.
                    // Имя файла = *выбраннный айди*.png
                    copy(new File(requireContext().getCacheDir(), "temp_photo.png"), new File(requireContext().getFilesDir(), selectedItem + ".png"));
                } else {
                    // Раньше была фотография, а теперь нет? Значит файл надо удалять.
                    new File(requireContext().getFilesDir(), selectedItem + ".png").delete();
                }
                MainActivity.db.updateContact(
                        selectedItem,
                        ((TextView)view.findViewById(R.id.name)).getText().toString(),
                        ((TextView) view.findViewById(R.id.surname)).getText().toString(),
                        ((TextView) view.findViewById(R.id.p_number)).getText().toString(),
                        ((TextView) view.findViewById(R.id.mail)).getText().toString(),
                        ((TextView) view.findViewById(R.id.address)).getText().toString()
                );
            }
        }
        dismiss();
    }

    // Запуск Photo Picker'а при нажатии на значок добавления фото
    public void selectPhoto(View view) {
        pickMedia.launch(new PickVisualMediaRequest.Builder()
                .setMediaType(ActivityResultContracts.PickVisualMedia.ImageOnly.INSTANCE)
                .build());
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