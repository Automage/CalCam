package com.automage.calcam;

import android.content.DialogInterface;
import android.content.Intent;
import android.icu.text.SimpleDateFormat;
import android.net.Uri;
import android.os.Bundle;
import android.os.Environment;
import android.provider.MediaStore;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.Snackbar;
import android.support.v4.content.FileProvider;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.View;
import android.view.Menu;
import android.view.MenuItem;

import java.io.File;
import java.io.IOException;
import java.util.Date;

public class MainActivity extends AppCompatActivity {

    static final int REQUEST_TAKE_PHOTO = 1;
    static final int REQUEST_CHOOSE_EXISTING_PHOTO = 0;

    private boolean chooseImageFromCamera;
    private String currentPhotoPath;

    private AlertDialog.Builder imageSourceDialogBuilder;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        Toolbar toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        FloatingActionButton fab = findViewById(R.id.fab);
        fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
//                Snackbar.make(view, "Replace with your own action", Snackbar.LENGTH_LONG)
//                        .setAction("Action", null).show();
                dispatchImageIntent();
            }
        });

        //Building dialog to pick from camera or gallery

        imageSourceDialogBuilder = new AlertDialog.Builder(this);
        imageSourceDialogBuilder.setTitle(R.string.image_dialog_title);
        imageSourceDialogBuilder.setMessage(R.string.image_dialog_message);
        imageSourceDialogBuilder.setPositiveButton(R.string.image_dialog_positive_text,
                new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                chooseImageFromCamera = true;
            }
        });
        imageSourceDialogBuilder.setNegativeButton(R.string.image_dialog_negative_text,
                new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                chooseImageFromCamera = false;
            }
        });

    }

    public void dispatchImageIntent() {
        Intent imageSourceIntent;

        //Show dialog to let user choose image source
        imageSourceDialogBuilder.show();

        //Handle image source
        if (chooseImageFromCamera) {
            imageSourceIntent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);

            File photoFile = null;
            try {
                photoFile = createImageFile();
            } catch (IOException e) {
                Log.e("Exception", e.getMessage());
            }

            if (photoFile != null) {
                Uri photoUri = FileProvider.getUriForFile(this,
                            "com.example.android.fileprovider", photoFile);
                imageSourceIntent.putExtra(MediaStore.EXTRA_OUTPUT, photoUri);
                startActivityForResult(imageSourceIntent, REQUEST_TAKE_PHOTO);
            }
        } else {
            //TODO: Gallery intent
        }
    }

    private File createImageFile() throws IOException {
        // Create an image file name
        String timeStamp = new SimpleDateFormat("yyyyMMdd_HHmmss").format(new Date());
        String imageFileName = "JPEG_" + timeStamp + "_";
        File storageDir = getExternalFilesDir(Environment.DIRECTORY_PICTURES);
        File image = File.createTempFile(
                imageFileName,  /* prefix */
                ".jpg",         /* suffix */
                storageDir      /* directory */
        );

        // Save a file: path for use with ACTION_VIEW intents
        currentPhotoPath = image.getAbsolutePath();
        return image;
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.Z
        getMenuInflater().inflate(R.menu.menu_main, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

        //noinspection SimplifiableIfStatement
        if (id == R.id.action_settings) {
            return true;
        }

        return super.onOptionsItemSelected(item);
    }
}
