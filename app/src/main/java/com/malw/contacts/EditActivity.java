package com.malw.contacts;

import androidx.activity.result.ActivityResultLauncher;
import androidx.activity.result.PickVisualMediaRequest;
import androidx.activity.result.contract.ActivityResultContracts;
import androidx.appcompat.app.AppCompatActivity;

import android.net.Uri;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

import java.io.File;
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
        }
        pickMedia = registerForActivityResult(new ActivityResultContracts.PickVisualMedia(), uri -> {
            if (uri != null) {
                try {
                    File file = new File(getCacheDir(), "temp_photo.png");
                    file.createNewFile();
                    OutputStream os = new FileOutputStream(file);
                    InputStream is = getContentResolver().openInputStream(uri);
                    byte[] buffer = new byte[8192];
                    int bytesRead;
                    while ((bytesRead = is.read(buffer)) != -1) {
                        os.write(buffer, 0, bytesRead);
                    }
                    os.flush();
                    is.close();
                    os.close();
                    ImageView avatar = findViewById(R.id.avatar);
                    avatar.setImageURI(Uri.fromFile(file));
                    avatar.setTag(1);
                }
                catch (IOException e) {
                    Log.e("IOException", e.getMessage());
                }

            }
        });
    }

    public void edit(View view) {
        if(selectedItem == -1){
//            if (((ImageView)findViewById(R.id.avatar)).getTag().toString().equals("1")) {
//
//            }
            MainActivity.db.addContact(
                ((TextView)findViewById(R.id.name)).getText().toString(),
                ((TextView)findViewById(R.id.surname)).getText().toString(),
                ((TextView)findViewById(R.id.p_number)).getText().toString(),
                ((TextView)findViewById(R.id.mail)).getText().toString(),
                ((TextView)findViewById(R.id.address)).getText().toString()
            );
        }
        else{
            MainActivity.db.updateContact(
                selectedItem,
                ((TextView)findViewById(R.id.name)).getText().toString(),
                ((TextView)findViewById(R.id.surname)).getText().toString(),
                ((TextView)findViewById(R.id.p_number)).getText().toString(),
                ((TextView)findViewById(R.id.mail)).getText().toString(),
                ((TextView)findViewById(R.id.address)).getText().toString()
            );
        }
        finish();
    }

    public void selectPhoto(View view) {
        pickMedia.launch(new PickVisualMediaRequest.Builder()
            .setMediaType(ActivityResultContracts.PickVisualMedia.ImageOnly.INSTANCE)
            .build());
    }
}