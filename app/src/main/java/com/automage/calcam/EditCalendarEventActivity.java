package com.automage.calcam;

import android.content.Intent;
import android.graphics.Bitmap;
import android.net.Uri;
import android.support.annotation.NonNull;
import android.support.v7.app.ActionBar;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.Toast;

import com.automage.calcam.utility.ImageUtil;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.ml.vision.FirebaseVision;
import com.google.firebase.ml.vision.common.FirebaseVisionImage;
import com.google.firebase.ml.vision.document.FirebaseVisionDocumentText;
import com.google.firebase.ml.vision.document.FirebaseVisionDocumentTextRecognizer;

import java.util.List;

public class EditCalendarEventActivity extends AppCompatActivity {

    private Bitmap image;
    private FirebaseVisionDocumentText parsedText;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_edit_calendar_event);

        Toolbar toolbar = findViewById(R.id.image_confirmation_toolbar);
        setSupportActionBar(toolbar);

        //Enables Up (Back) button
        ActionBar ab = getSupportActionBar();
        ab.setDisplayHomeAsUpEnabled(true);

        Intent data = getIntent();
        Uri imageUri = Uri.parse(data.getStringExtra(ImageConfirmationActivity.EXTRA_IMAGE_URI));
        image = ImageUtil.readImageFromStorage(imageUri, this.getContentResolver());

        runTextRecognition();
    }


    private void uploadToCalendar() {
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
        parsedText = text;
        if (text == null) {
            Log.v("pman", "no text recognized");
            showToast("No text recognized!");
            return;
        }

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
        showToast("DONE");
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.Z
        getMenuInflater().inflate(R.menu.edit_calendar_event_menu, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        if (item.getItemId() == R.id.edit_calendar_event_confirma) {
            uploadToCalendar();
            return true;
        } else {
            return super.onOptionsItemSelected(item);
        }
    }

    private void showToast(String message) {
        Toast.makeText(this, message, Toast.LENGTH_LONG);
    }

}
