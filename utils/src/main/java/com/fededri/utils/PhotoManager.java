package com.fededri.utils;

/**
 * Created by Federico Torres on 25/11/2016.
 */

import android.annotation.SuppressLint;
import android.content.Context;
import android.content.Intent;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.graphics.Matrix;
import android.media.ExifInterface;
import android.net.Uri;
import android.os.Environment;
import android.provider.MediaStore;

import java.io.File;
import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.Date;


public class PhotoManager {
    public final static String JPEG_FILE_PREFIX = "IMG_";
    public final static String VIDEO_FILE_PREFIX = "VID_";
    public final static String JPEG_FILE_SUFFIX = ".jpg";
    public final static String MP4_FILE_SUFFIX = ".mp4";
    public final static String F3GP_FILE_SUFFIX = ".3gp";
    public static String nameDir = "image_capture";

    /**
     * Generates a path where the photo will be saved temporally
     *
     * @param data
     * @param mContext  Activity context
     *
     * @return path for saving the image
     */
    public static String getPhotoPath(Intent data, Context mContext) {
        Uri selectedImage = data.getData();
        String[] filePathColumn = { MediaStore.Images.Media.DATA };
        Cursor cursor = mContext.getContentResolver().query(selectedImage, filePathColumn, null, null, null);
        cursor.moveToFirst();
        int columnIndex = cursor.getColumnIndex(filePathColumn[0]);
        String mCurrentPhotoPath = cursor.getString(columnIndex);
        cursor.close();

        return mCurrentPhotoPath;
    }

    @SuppressLint("SimpleDateFormat")
    public static File createImageFile() throws IOException {
        // Create an image file name
        String timeStamp = new SimpleDateFormat("yyyyMMdd_HHmmss").format(new Date());
        String imageFileName = JPEG_FILE_PREFIX + timeStamp + JPEG_FILE_SUFFIX;
        String nameAlbum = Environment.getExternalStorageDirectory().getAbsolutePath() + "/" + nameDir;// getString(R.string.app_name);
        File albumF = new File(nameAlbum);
        if (!albumF.exists()) {
            albumF.mkdirs();
            File noMedia = new File(albumF, ".nomedia");
            noMedia.createNewFile();
        }

        File imageF = new File(albumF, imageFileName);
        imageF.createNewFile();
        return imageF;
    }

    @SuppressLint("SimpleDateFormat")
    public static File createVideoFile() throws IOException {
        // Create an image file name
        String timeStamp = new SimpleDateFormat("yyyyMMdd_HHmmss").format(new Date());
        String imageFileName = VIDEO_FILE_PREFIX + timeStamp + MP4_FILE_SUFFIX;
        String nameAlbum = Environment.getExternalStorageDirectory().getAbsolutePath() + "/" + nameDir;// getString(R.string.app_name);
        File albumF = new File(nameAlbum);
        if (!albumF.exists()) {
            albumF.mkdirs();
            File noMedia = new File(albumF, ".nomedia");
            noMedia.createNewFile();
        }

        File imageF = new File(albumF, imageFileName);
        imageF.createNewFile();
        return imageF;
    }

    @SuppressLint("SimpleDateFormat")
    public static File createVideoFile3gp() throws IOException {
        // Create an image file name
        String timeStamp = new SimpleDateFormat("yyyyMMdd_HHmmss").format(new Date());
        String imageFileName = VIDEO_FILE_PREFIX + timeStamp + F3GP_FILE_SUFFIX;
        String nameAlbum = Environment.getExternalStorageDirectory().getAbsolutePath() + "/" + nameDir;// getString(R.string.app_name);
        File albumF = new File(nameAlbum);
        if (!albumF.exists()) {
            albumF.mkdirs();
            File noMedia = new File(albumF, ".nomedia");
            noMedia.createNewFile();
        }

        File imageF = new File(albumF, imageFileName);
        imageF.createNewFile();
        return imageF;
    }

    public static Bitmap imageOreintationValidator(Bitmap bitmap, String path) {

        ExifInterface ei;
        try {
            ei = new ExifInterface(path);
            int orientation = ei.getAttributeInt(ExifInterface.TAG_ORIENTATION, ExifInterface.ORIENTATION_NORMAL);
            switch (orientation) {
                case ExifInterface.ORIENTATION_ROTATE_90:
                    bitmap = rotateImage(bitmap, 90);
                    break;
                case ExifInterface.ORIENTATION_ROTATE_180:
                    bitmap = rotateImage(bitmap, 180);
                    break;
                case ExifInterface.ORIENTATION_ROTATE_270:
                    bitmap = rotateImage(bitmap, 270);
                    break;
            }
        } catch (IOException e) {
            e.printStackTrace();
        }

        return bitmap;
    }

    public static Bitmap rotateImage(Bitmap source, float angle) {
        Bitmap bitmap = null;
        Matrix matrix = new Matrix();
        matrix.postRotate(angle);
        try {
            bitmap = Bitmap.createBitmap(source, 0, 0, source.getWidth(), source.getHeight(), matrix, true);
        } catch (OutOfMemoryError err) {
            err.printStackTrace();
            return source;
        }
        return bitmap;
    }

    public static Bitmap getResizedBitmap(Bitmap bm, int newHeight, int newWidth) {
        return Bitmap.createScaledBitmap(bm,newWidth,newHeight,true);
    }

    public static long getFileSizeInMegas(String mCurrentPath) {

        long mb = 0;
        try {
            File file = new File(mCurrentPath);
            long length = file.length();
            long kb = length / 1024;
            mb = kb / 1024;
        } catch (Exception e) {
            e.printStackTrace();
        }

        return mb;
    }

}

