package com.malw.contacts;

import androidx.activity.result.ActivityResultLauncher;
import androidx.activity.result.PickVisualMediaRequest;
import androidx.activity.result.contract.ActivityResultContracts;
import androidx.appcompat.app.AppCompatActivity;

import android.app.AlertDialog;
import android.net.Uri;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.util.HashMap;

public class EditActivity extends AppCompatActivity {

    int selectedItem;
    ActivityResultLauncher<PickVisualMediaRequest> pickMedia;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_edit);
        // Получаем выбранный элемент из intent'а. Если его нет, значит создается новый контакт, значения подставлять не нужно.
        selectedItem = getIntent().getIntExtra("selectedItem", -1);
        if(selectedItem != -1) {
            // Если же выбран какой-то ID, получаем информацию о контакте из БД и подставляем куда надо
            HashMap<String, String> info = MainActivity.db.getContactInfo(selectedItem);
            ((TextView) findViewById(R.id.name)).setText(info.get("name"));
            ((TextView) findViewById(R.id.surname)).setText(info.get("surname"));
            ((TextView) findViewById(R.id.p_number)).setText(info.get("phone"));
            ((TextView) findViewById(R.id.mail)).setText(info.get("email"));
            ((TextView) findViewById(R.id.address)).setText(info.get("address"));
            // Если файл с аватаркой существует, то он устанавливается в ImageView. Иначе в ImageView устанавливается стандартная аватарка.
            // Если изображение было изменено, но название файла осталось таким же - фотография не обновится.
            // Поэтому используем такой костыль: Заранее устанавливаем стандартную аватарку в любом случае.
            ((ImageView) findViewById(R.id.avatar)).setImageResource(R.drawable.photo);
            File avatar = new File(getFilesDir(), info.get("id")+".png");
            if (avatar.exists()) {
                ((ImageView)findViewById(R.id.avatar)).setImageURI(Uri.fromFile(avatar));
            }
        }
        // Используем Result Activity API для запуска встроенного Activity PickVisualMedia
        // https://developer.android.com/training/data-storage/shared/photopicker
        pickMedia = registerForActivityResult(new ActivityResultContracts.PickVisualMedia(), uri -> {
            ImageView avatar = findViewById(R.id.avatar);
            // Если пользователь выбрал аватарку (uri не null), то копируем файл в папку с кешем.
            if (uri != null) {
                // /data/data/com.malw.contacts/cache/temp_photo.png
                // либо на новых андроидах /data/user/0/com.malw.contacts/cache/temp_photo.png
                File img = new File(getCacheDir(), "temp_photo.png");
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
    }

    // Вызывается при нажатии на кнопку сохранения
    public void edit(View view) {
        // Если в поле ввода имени ничего нет, выдавать предупреждение. Иначе сохранять.
        if(((TextView)findViewById(R.id.name)).getText().toString().equals("")) {
            new AlertDialog.Builder(this).setTitle("Изменение контакта")
                    .setMessage("Имя не может быть пустым!")
                    .setPositiveButton(android.R.string.ok, null)
                    .setIcon(android.R.drawable.ic_dialog_alert).show();
        }
        else {
            if (selectedItem == -1) {
                // Создание нового контакта
                if (findViewById(R.id.avatar).getTag().toString().equals("1"))
                    // Если пользователь установил фото (тег у ImageVIew = 1), то копировать файл из кеша в данные.
                    // Имя файла = *последний сгенерированный айди AUTOINCREMENT + 1*.png
                    copy(new File(getCacheDir(), "temp_photo.png"), new File(getFilesDir(), (MainActivity.db.getLastId() + 1) + ".png"));
                MainActivity.db.addContact(
                        ((TextView) findViewById(R.id.name)).getText().toString(),
                        ((TextView) findViewById(R.id.surname)).getText().toString(),
                        ((TextView) findViewById(R.id.p_number)).getText().toString(),
                        ((TextView) findViewById(R.id.mail)).getText().toString(),
                        ((TextView) findViewById(R.id.address)).getText().toString()
                );
            } else {
                // Изменение существующего контакта
                if (findViewById(R.id.avatar).getTag().toString().equals("1")) {
                    // Если пользователь установил фото (тег у ImageVIew = 1), то копировать файл из кеша в данные.
                    // Имя файла = *выбраннный айди*.png
                    copy(new File(getCacheDir(), "temp_photo.png"), new File(getFilesDir(), selectedItem + ".png"));
                } else {
                    // Раньше была фотография, а теперь нет? Значит файл надо удалять.
                    new File(getFilesDir(), selectedItem + ".png").delete();
                }
                MainActivity.db.updateContact(
                        selectedItem,
                        ((TextView) findViewById(R.id.name)).getText().toString(),
                        ((TextView) findViewById(R.id.surname)).getText().toString(),
                        ((TextView) findViewById(R.id.p_number)).getText().toString(),
                        ((TextView) findViewById(R.id.mail)).getText().toString(),
                        ((TextView) findViewById(R.id.address)).getText().toString()
                );
            }
            finish();
        }

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
        try (OutputStream os = new FileOutputStream(to); InputStream is = getContentResolver().openInputStream(from)) {
            byte[] buffer = new byte[8192];
            int bytesRead;
            while ((bytesRead = is.read(buffer)) != -1) os.write(buffer, 0, bytesRead);
            os.flush();
        } catch (IOException e) {
            Log.e("IOException", e.getMessage());
        }
    }
}