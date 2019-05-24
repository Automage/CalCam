package com.automage.calcam.utility;

import android.content.ContentResolver;
import android.graphics.Bitmap;
import android.graphics.Matrix;
import android.net.Uri;
import android.provider.MediaStore;
import android.support.media.ExifInterface;
import android.util.Log;

import java.io.IOException;
import java.io.InputStream;

/**
 * ImageUtil is a static helper class for reading images from content URIs.
 */

public class ImageUtil {

    /**
     * Reads image from the URI provided. Rotates the image if necessary by reading Exif
     * data
     *
     * @param photoUri - URI to image file
     */
    public static Bitmap readImageFromStorage(Uri photoUri, ContentResolver contentResolver) {
        Bitmap image = null;
        Bitmap temp_bitmap = null;
        ExifInterface ei = null;

        try (InputStream inputStream = contentResolver.openInputStream(photoUri);) {
            temp_bitmap = MediaStore.Images.Media.getBitmap(contentResolver, photoUri);
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

        return image;
    }

    private static Bitmap rotateImage(Bitmap source, float angle) {
        Matrix matrix = new Matrix();
        matrix.postRotate(angle);
        return Bitmap.createBitmap(source, 0, 0, source.getWidth(), source.getHeight(),
                matrix, true);
    }

}
