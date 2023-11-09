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
        setTitle("Редактирование контакта");
        selectedItem = getIntent().getIntExtra("selectedItem", -1);
        if(selectedItem != -1) {
            HashMap<String, String> info = MainActivity.db.getContactInfo(selectedItem);
            ((TextView) findViewById(R.id.name)).setText(info.get("name"));
            ((TextView) findViewById(R.id.surname)).setText(info.get("surname"));
            ((TextView) findViewById(R.id.p_number)).setText(info.get("phone"));
            ((TextView) findViewById(R.id.mail)).setText(info.get("email"));
            ((TextView) findViewById(R.id.address)).setText(info.get("address"));
            ((ImageView) findViewById(R.id.avatar)).setImageResource(R.drawable.photo);
            File avatar = new File(getFilesDir(), info.get("id")+".png");
            if (avatar.exists()) {
                ((ImageView)findViewById(R.id.avatar)).setImageURI(Uri.fromFile(avatar));
            }
        }
        pickMedia = registerForActivityResult(new ActivityResultContracts.PickVisualMedia(), uri -> {
            ImageView avatar = findViewById(R.id.avatar);
            if (uri != null) {
                File img = new File(getCacheDir(), "temp_photo.png");
                copy(uri, img);
                avatar.setImageURI(Uri.fromFile(img));
                avatar.setTag(1);
            }
            else {
                avatar.setImageResource(R.drawable.photo);
                avatar.setTag(0);
            }
        });
    }

    public void edit(View view) {
        if(((TextView)findViewById(R.id.name)).getText().toString().equals("")) {
            new AlertDialog.Builder(this).setTitle("Изменение контакта")
                    .setMessage("Имя не может быть пустым!")
                    .setPositiveButton(android.R.string.ok, null)
                    .setIcon(android.R.drawable.ic_dialog_alert).show();
        }
        else {
            if (selectedItem == -1) {
                if (((ImageView) findViewById(R.id.avatar)).getTag().toString().equals("1"))
                    copy(new File(getCacheDir(), "temp_photo.png"), new File(getFilesDir(), (MainActivity.db.getLastId() + 1) + ".png"));
                MainActivity.db.addContact(
                        ((TextView) findViewById(R.id.name)).getText().toString(),
                        ((TextView) findViewById(R.id.surname)).getText().toString(),
                        ((TextView) findViewById(R.id.p_number)).getText().toString(),
                        ((TextView) findViewById(R.id.mail)).getText().toString(),
                        ((TextView) findViewById(R.id.address)).getText().toString()
                );
            } else {
                if (((ImageView) findViewById(R.id.avatar)).getTag().toString().equals("1")) {
                    copy(new File(getCacheDir(), "temp_photo.png"), new File(getFilesDir(), selectedItem + ".png"));
                } else {
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

    public void selectPhoto(View view) {
        pickMedia.launch(new PickVisualMediaRequest.Builder()
            .setMediaType(ActivityResultContracts.PickVisualMedia.ImageOnly.INSTANCE)
            .build());
    }

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