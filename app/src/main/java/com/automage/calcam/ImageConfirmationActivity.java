package com.automage.calcam;

import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.Matrix;
import android.net.Uri;
import android.provider.MediaStore;
import android.support.annotation.NonNull;
import android.support.design.widget.FloatingActionButton;
import android.support.media.ExifInterface;
import android.support.v7.app.ActionBar;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;
import android.support.v7.widget.Toolbar;

import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.ml.vision.FirebaseVision;
import com.google.firebase.ml.vision.common.FirebaseVisionImage;
import com.google.firebase.ml.vision.document.FirebaseVisionDocumentText;
import com.google.firebase.ml.vision.document.FirebaseVisionDocumentTextRecognizer;

import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.util.List;

public class ImageConfirmationActivity extends AppCompatActivity {

    private ImageView displayedImageView;
    private TextView parsedTextView;

    private Bitmap image;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_image_confirmation);

        Toolbar toolbar = findViewById(R.id.image_confirmation_toolbar);
        setSupportActionBar(toolbar);

        //Enables Up (Back) button
        ActionBar ab = getSupportActionBar();
        ab.setDisplayHomeAsUpEnabled(true);

        displayedImageView = findViewById(R.id.displayed_image);
        parsedTextView = findViewById(R.id.parsed_text);

        Intent data = getIntent();
        Uri imageUri = Uri.parse(data.getStringExtra(MainActivity.EXTRA_IMAGE_URI));
        readImageFromStorage(imageUri);

        runTextRecognition();
    }


    private void launchEditEventActivity() {

    }

    /**
     * Reads image from the URI provided. Rotates the image if necessary by reading Exif
     * data
     *
     * @param photoUri - URI to image file
     */
    private void readImageFromStorage(Uri photoUri) {
        Bitmap temp_bitmap = null;
        ExifInterface ei = null;

        try (InputStream inputStream = this.getContentResolver().openInputStream(photoUri);) {
            temp_bitmap = MediaStore.Images.Media.getBitmap(this.getContentResolver(), photoUri);
            ei = new ExifInterface(inputStream);
        } catch (IOException e) {
            Log.e("Exception", e.toString());
        }

        int orientation = ei.getAttributeInt(ExifInterface.TAG_ORIENTATION,
                ExifInterface.ORIENTATION_UNDEFINED);

        switch(orientation) {
            case ExifInterface.ORIENTATION_ROTATE_90:
                image = rotateImage(temp_bitmap, 90);
                break;

            case ExifInterface.ORIENTATION_ROTATE_180:
                image = rotateImage(temp_bitmap, 180);
                break;

            case ExifInterface.ORIENTATION_ROTATE_270:
                image = rotateImage(temp_bitmap, 270);
                break;

            case ExifInterface.ORIENTATION_NORMAL:
            default:
                image = temp_bitmap;
        }

        displayedImageView.setImageBitmap(image);
    }

    private Bitmap rotateImage(Bitmap source, float angle) {
        Matrix matrix = new Matrix();
        matrix.postRotate(angle);
        return Bitmap.createBitmap(source, 0, 0, source.getWidth(), source.getHeight(),
                matrix, true);
    }

    private void runTextRecognition() {
        Log.v("pman", "runTextRecognition()");
        FirebaseVisionImage firebaseImage = FirebaseVisionImage.fromBitmap(image);
        FirebaseVisionDocumentTextRecognizer recognizer = FirebaseVision.getInstance()
                .getCloudDocumentTextRecognizer();
        recognizer.processImage(firebaseImage)
                .addOnSuccessListener(new OnSuccessListener<FirebaseVisionDocumentText>() {
                    @Override
                    public void onSuccess(FirebaseVisionDocumentText text) {
                        processTextRecognitionResult(text);
                    }
                })
                .addOnFailureListener(new OnFailureListener() {
                    @Override
                    public void onFailure(@NonNull Exception e) {
                        Log.e("Exception", e.toString());
                    }
                });
    }

    private void processTextRecognitionResult(FirebaseVisionDocumentText text) {
        Log.v("pman", "processTextRecognitionResult()");
        if (text == null) {
            Log.v("pman", "no text recognized");
            showToast("No text recognized!");
            return;
        }

        parsedTextView.setText("");
        String temp = "";
        List<FirebaseVisionDocumentText.Block> blocks = text.getBlocks();
        for (int i = 0; i < blocks.size(); i++) {
            List<FirebaseVisionDocumentText.Paragraph> paragraphs = blocks.get(i).getParagraphs();
            for (int j = 0; j < paragraphs.size(); j++) {
                List<FirebaseVisionDocumentText.Word> words = paragraphs.get(j).getWords();
                for (int l = 0; l < words.size(); l++) {
                    List<FirebaseVisionDocumentText.Symbol> symbols = words.get(l).getSymbols();
                    for (int k = 0; k < symbols.size(); k++) {
                        temp += symbols.get(k).getText();
                    }
                    temp += " ";
                }
                temp += "\n";
            }
        }
        parsedTextView.setText(temp);
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.Z
        getMenuInflater().inflate(R.menu.image_confirmation_menu, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        if (item.getItemId() == R.id.image_confirmation_confirm_action) {
            launchEditEventActivity();
            return true;
        } else {
            return super.onOptionsItemSelected(item);
        }
    }

    private void showToast(String message) {
        Toast.makeText(this, message, Toast.LENGTH_LONG);
    }

}
