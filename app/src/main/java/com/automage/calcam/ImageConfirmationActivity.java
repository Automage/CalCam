package com.automage.calcam;

import android.content.Intent;
import android.graphics.Bitmap;
import android.net.Uri;
import android.support.v7.app.ActionBar;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.ImageView;
import android.support.v7.widget.Toolbar;

import com.automage.calcam.utility.ImageUtil;

public class ImageConfirmationActivity extends AppCompatActivity {

    private ImageView displayedImageView;

    private Bitmap image;
    private Uri imageUri;

    public static final String EXTRA_IMAGE_URI =
            "com.automage.calcam.extra.IMAGE_URI";

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

        Intent data = getIntent();
        imageUri = Uri.parse(data.getStringExtra(MainActivity.EXTRA_IMAGE_URI));
        readImage(imageUri);
    }

    private void readImage(Uri imageUri) {
        image = ImageUtil.readImageFromStorage(imageUri, this.getContentResolver());
        displayedImageView.setImageBitmap(image);
    }


    private void launchEditEventActivity() {
        Intent launchActivityIntent = new Intent(ImageConfirmationActivity.this, EditCalendarEventActivity.class);
        launchActivityIntent.putExtra(EXTRA_IMAGE_URI, imageUri.toString());
        startActivity(launchActivityIntent);
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

}
