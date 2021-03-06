package com.automage.calcam;

import android.content.Intent;
import android.icu.text.SimpleDateFormat;
import android.net.Uri;
import android.os.Bundle;
import android.os.Environment;
import android.provider.MediaStore;
import android.support.annotation.Nullable;
import android.support.design.widget.FloatingActionButton;
import android.support.v4.content.FileProvider;
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

    public static final String EXTRA_IMAGE_URI =
            "com.automage.calcam.extra.IMAGE_URI";
    static final int REQUEST_TAKE_PHOTO = 1;
    static final int REQUEST_EXISTING_PHOTO = 0;

    private Uri currentPhotoUri = null;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        Toolbar toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        FloatingActionButton cameraFab = findViewById(R.id.camera_fab);
        cameraFab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                dispatchImageIntent(REQUEST_TAKE_PHOTO);
            }
        });

        FloatingActionButton galleryFab = findViewById(R.id.gallery_fab);
        galleryFab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                dispatchImageIntent(REQUEST_EXISTING_PHOTO);
            }
        });

    }

    /**
     * Launches an ImageConfirmationActivity
     */
    private void launchImageConfirmationActivity() {
        Log.v("pman", "ImageConfirmationActivity Intent started");
        Intent launchActivityIntent = new Intent(this,
                ImageConfirmationActivity.class);
        //Log.v("pman", currentPhotoUri == null ? "null" : currentPhotoUri.toString());
        launchActivityIntent.putExtra(EXTRA_IMAGE_URI, currentPhotoUri.toString());
        startActivity(launchActivityIntent);
    }

    /**
     * Creates an intent to retrieve an image URI through the camera or gallery, depending on
     * the value of sourceRequest
     *
     * @param sourceRequest - Photo source code (e.g. REQUEST_TAKE_PHOTO)
     */
    private void dispatchImageIntent(int sourceRequest) {
        Log.v("pman", "dispatchImageIntent()");
        Intent imageSourceIntent;

        //Handle image source
        if (sourceRequest == REQUEST_TAKE_PHOTO) {
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
                currentPhotoUri = photoUri;
                imageSourceIntent.putExtra(MediaStore.EXTRA_OUTPUT, photoUri);
                startActivityForResult(imageSourceIntent, REQUEST_TAKE_PHOTO);
            }
        } else {
            imageSourceIntent = new Intent(Intent.ACTION_PICK, MediaStore.Images.Media.EXTERNAL_CONTENT_URI);
            startActivityForResult(imageSourceIntent, REQUEST_EXISTING_PHOTO);
        }
    }

    /**
     * Creates a file object within the private app directory, intended to be used for .jpeg files.
     *
     * @return File object for a .jpeg file
     * @throws IOException
     */
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

        image.deleteOnExit();

        return image;
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        Log.v("pman", "onActivityResult()");

        //Check if result was OK
        if (resultCode == RESULT_OK) {
            switch (requestCode) {
                case REQUEST_TAKE_PHOTO:
                    Log.v("pman", "REQUEST_TAKE_PHOTO Intent returned");
                    launchImageConfirmationActivity();
                    break;
                case REQUEST_EXISTING_PHOTO:
                    Log.v("pman", "REQUEST_EXISTING_PHOTO Intent returned");
                    currentPhotoUri = data.getData();
                    //currentPhotoPath = photoUri.getPath();
                    launchImageConfirmationActivity();
                    break;
                default:
                    Log.e("Exception", "Unhandled Intent");
            }
        } else {
            Log.e("Exception", "Intent resultCode returned non-OK");
        }
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
