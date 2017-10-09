package com.fededri.utils;

import android.app.Dialog;
import android.content.Context;
import android.os.Handler;
import android.os.Looper;
import android.view.View;
import android.widget.TextView;


public class PhotoContactDialog extends Dialog {

    private Context context;
    // Shared Preferences
    private Handler handler = new Handler(Looper.getMainLooper());
    private View btnTakePhoto;
    private View btnGallery;
    private TextView titleTextView;
    private static final int TAKE_PHOTO = 0;
    private static final int GALLERY = 1;

    public PhotoContactDialog(Context context) {
        super(context, R.style.CustomDialogTheme);
        this.context = context;
        initDialoag();
    }

    @Override
    protected void onStart() {
        super.onStart();
    }

    private void initDialoag() {
        setContentView(R.layout.photo_contact_dialog);

        titleTextView = (TextView) findViewById(R.id.tv_title_photo_contact_dialog);
        btnTakePhoto = findViewById(R.id.tv_take_photo_contact_dialog);
        btnGallery = findViewById(R.id.tv_gallery_photo_contact_dialog);

    }// INIT DIALOG

    public void setOnTakePictureListener(View.OnClickListener onClickListener) {
        if (btnTakePhoto != null) {
            btnTakePhoto.setOnClickListener(onClickListener);
        }
    }

    public void setOnGalleryListener(View.OnClickListener onClickListener) {
        if (btnGallery != null) {
            btnGallery.setOnClickListener(onClickListener);
        }
    }

    public TextView getTitleTextView() {
        return titleTextView;
    }

    public void setTitleTextView(TextView titleTextView) {
        this.titleTextView = titleTextView;
    }

}
