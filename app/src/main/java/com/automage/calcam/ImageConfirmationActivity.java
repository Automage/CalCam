package com.automage.calcam;

import android.content.Intent;
import android.graphics.Bitmap;
import android.net.Uri;
import android.support.design.widget.FloatingActionButton;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.ImageView;

import com.google.firebase.ml.vision.text.FirebaseVisionText;

public class ImageConfirmationActivity extends AppCompatActivity {

    private FloatingActionButton fab;
    private ImageView displayedPicture;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_image_confirmation);

        fab = findViewById(R.id.confirmation_fab);
        fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                launchEditEventActivity();
            }
        });
        displayedPicture = findViewById(R.id.displayed_image);

        Intent data = getIntent();
        Uri imageUri = Uri.parse(data.getStringExtra(MainActivity.EXTRA_IMAGE_URI));
        readImageFromStorage(imageUri);

        runTextRecognition();
    }


    private void launchEditEventActivity() {

    }

    private void readImageFromStorage(Uri photoUri) {
        Log.v("pman", photoUri == null ? "null" : photoUri.toString());
    }

    private void runTextRecognition() {

    }

    private void processTextRecognitionResult() {

    }

}
