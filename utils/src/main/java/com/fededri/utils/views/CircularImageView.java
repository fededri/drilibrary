package com.fededri.utils.views;

/**
 * Created by Federico Torres on 7/10/2016.
 */


import android.Manifest;
import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.content.res.TypedArray;
import android.graphics.Bitmap;
import android.graphics.BitmapShader;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Matrix;
import android.graphics.Paint;
import android.graphics.RectF;
import android.graphics.Shader;
import android.graphics.drawable.BitmapDrawable;
import android.graphics.drawable.ColorDrawable;
import android.graphics.drawable.Drawable;
import android.net.Uri;
import android.os.Environment;
import android.provider.MediaStore;
import android.support.v4.app.ActivityCompat;
import android.support.v4.app.Fragment;
import android.support.v4.content.ContextCompat;
import android.support.v7.widget.AppCompatImageView;
import android.util.AttributeSet;
import android.view.View;

import com.fededri.utils.PhotoContactDialog;
import com.fededri.utils.R;

import java.io.File;
import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.Date;


@SuppressWarnings("deprecation")
public class CircularImageView extends AppCompatImageView {

    public static final int ACTION_TAKE_PHOTO = 1;
    public static final int ACTION_GALLERY_PHOTO = 2;
    private static final ScaleType SCALE_TYPE = ScaleType.CENTER_CROP;
    private static final Bitmap.Config BITMAP_CONFIG = Bitmap.Config.ARGB_8888;
    private static final int COLORDRAWABLE_DIMENSION = 1;
    private static final int DEFAULT_BORDER_WIDTH = 0;
    private static final int DEFAULT_BORDER_COLOR = Color.BLACK;
    private static final int REQUEST_READ_STORAGE_PERMISSION = 980;
    private static final String TAG = "PICTURE_PROFILE_COMPONENT";
    private static final String JPEG_FILE_PREFIX = "IMG_";
    private static final String JPEG_FILE_SUFFIX = ".jpg";
    private static final int REQUEST_PERMISSION_CAMERA = 800;
    private final RectF mDrawableRect = new RectF();
    private final RectF mBorderRect = new RectF();
    private final Matrix mShaderMatrix = new Matrix();
    private final Paint mBitmapPaint = new Paint();
    private final Paint mBorderPaint = new Paint();
    private int mBorderColor = DEFAULT_BORDER_COLOR;
    private int mBorderWidth = DEFAULT_BORDER_WIDTH;
    private Bitmap mBitmap;
    private BitmapShader mBitmapShader;
    private int mBitmapWidth;
    private int mBitmapHeight;
    private float mDrawableRadius;
    private float mBorderRadius;
    private boolean mReady;
    private boolean mSetupPending;
    private Activity activity;
    private Fragment fragment;
    private String mCurrentPhotoPath = "";
    private String nameDir = "";
    private PhotoContactDialog contactDialog;
    private File myPictureFile = null;

    public CircularImageView(Context context) {
        super(context);
        mBorderColor = context.getResources().getColor(R.color.gray_line_notification);

    }

    public CircularImageView(Context context, AttributeSet attrs) {
        this(context, attrs, 0);
        mBorderColor = context.getResources().getColor(R.color.gray_line_notification);

        TypedArray a = getContext().getTheme().obtainStyledAttributes(attrs,R.styleable.CircularImageView,0,0);

        try{
            mBorderColor =  a.getColor(R.styleable.CircularImageView_borderColor,context.getResources().getColor(R.color.gray_line_notification));
            mBorderWidth = a.getDimensionPixelSize(R.styleable.CircularImageView_borderWidth,0);
        }finally {
            a.recycle();
        }
    }

    public CircularImageView(Context context, AttributeSet attrs, int defStyle) {
        super(context, attrs, defStyle);
        super.setScaleType(SCALE_TYPE);
        mBorderColor = context.getResources().getColor(R.color.gray_line_notification);
        mReady = true;

        if (mSetupPending) {
            setup();
            mSetupPending = false;
        }
    }

    @Override
    public ScaleType getScaleType() {
        return SCALE_TYPE;
    }

    @Override
    public void setScaleType(ScaleType scaleType) {
        if (scaleType != SCALE_TYPE) {
            throw new IllegalArgumentException(String.format(
                    "ScaleType %s not supported.", scaleType));
        }
    }


    /////////////// ACTION CAMERA - GALLERY ////////////////////

    @Override
    protected void onDraw(Canvas canvas) {
        if (getDrawable() == null) {
            return;
        }

        canvas.drawCircle(getWidth() / 2, getHeight() / 2, mDrawableRadius,
                mBitmapPaint);
        if (mBorderWidth != 0) {
            canvas.drawCircle(getWidth() / 2, getHeight() / 2, mBorderRadius,
                    mBorderPaint);
        }
    }

    @Override
    protected void onSizeChanged(int w, int h, int oldw, int oldh) {
        super.onSizeChanged(w, h, oldw, oldh);
        setup();
    }

    public int getBorderColor() {
        return mBorderColor;
    }

    public void setBorderColor(int borderColor) {
        if (borderColor == mBorderColor) {
            return;
        }

        mBorderColor = borderColor;
        mBorderPaint.setColor(mBorderColor);
        invalidate();
    }

    public int getBorderWidth() {
        return mBorderWidth;
    }

    public void setBorderWidth(int borderWidth) {
        if (borderWidth == mBorderWidth) {
            return;
        }

        mBorderWidth = borderWidth;
        setup();
    }

    @Override
    public void setImageBitmap(Bitmap bm) {
        super.setImageBitmap(bm);
        mBitmap = bm;
        setup();
    }

    @Override
    public void setImageDrawable(Drawable drawable) {
        super.setImageDrawable(drawable);
        mBitmap = getBitmapFromDrawable(drawable);
        setup();
    }

    @Override
    public void setImageResource(int resId) {
        super.setImageResource(resId);
        mBitmap = getBitmapFromDrawable(getDrawable());
        setup();
    }

    private Bitmap getBitmapFromDrawable(Drawable drawable) {
        if (drawable == null) {
            return null;
        }

        if (drawable instanceof BitmapDrawable) {
            return ((BitmapDrawable) drawable).getBitmap();
        }

        try {
            Bitmap bitmap;

            if (drawable instanceof ColorDrawable) {
                bitmap = Bitmap.createBitmap(COLORDRAWABLE_DIMENSION,
                        COLORDRAWABLE_DIMENSION, BITMAP_CONFIG);
            } else {
                bitmap = Bitmap.createBitmap(drawable.getIntrinsicWidth(),
                        drawable.getIntrinsicHeight(), BITMAP_CONFIG);
            }

            Canvas canvas = new Canvas(bitmap);
            drawable.setBounds(0, 0, canvas.getWidth(), canvas.getHeight());
            drawable.draw(canvas);
            return bitmap;
        } catch (OutOfMemoryError e) {
            return null;
        }
    }

    private void setup() {
        if (!mReady) {
            mSetupPending = true;
            return;
        }

        if (mBitmap == null) {
            return;
        }

        mBitmapShader = new BitmapShader(mBitmap, Shader.TileMode.CLAMP,
                Shader.TileMode.CLAMP);

        mBitmapPaint.setAntiAlias(true);
        mBitmapPaint.setShader(mBitmapShader);

        mBorderPaint.setStyle(Paint.Style.STROKE);
        mBorderPaint.setAntiAlias(true);
        mBorderPaint.setColor(mBorderColor);
        mBorderPaint.setStrokeWidth(mBorderWidth);

        mBitmapHeight = mBitmap.getHeight();
        mBitmapWidth = mBitmap.getWidth();

        mBorderRect.set(0, 0, getWidth(), getHeight());
        mBorderRadius = Math.min((mBorderRect.height() - mBorderWidth) / 2,
                (mBorderRect.width() - mBorderWidth) / 2);

        mDrawableRect.set(mBorderWidth, mBorderWidth, mBorderRect.width()
                - mBorderWidth, mBorderRect.height() - mBorderWidth);
        mDrawableRadius = Math.min(mDrawableRect.height() / 2,
                mDrawableRect.width() / 2);

        updateShaderMatrix();
        invalidate();
    }

    private void updateShaderMatrix() {
        float scale;
        float dx = 0;
        float dy = 0;

        mShaderMatrix.set(null);

        if (mBitmapWidth * mDrawableRect.height() > mDrawableRect.width()
                * mBitmapHeight) {
            scale = mDrawableRect.height() / (float) mBitmapHeight;
            dx = (mDrawableRect.width() - mBitmapWidth * scale) * 0.5f;
        } else {
            scale = mDrawableRect.width() / (float) mBitmapWidth;
            dy = (mDrawableRect.height() - mBitmapHeight * scale) * 0.5f;
        }

        mShaderMatrix.setScale(scale, scale);
        mShaderMatrix.postTranslate((int) (dx + 0.5f) + mBorderWidth,
                (int) (dy + 0.5f) + mBorderWidth);

        mBitmapShader.setLocalMatrix(mShaderMatrix);
    }

    public void initActionPicture(Activity activity) {

        this.activity = activity;
        initActionPicture();
    }

    public void initActionPicture(Fragment fragment) {
        this.fragment = fragment;
        initActionPicture();
    }

    private void initActionPicture() {

        this.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View v) {

                if (fragment != null) {
                    if (ContextCompat.checkSelfPermission(getContext(), Manifest.permission.CAMERA) != PackageManager.PERMISSION_GRANTED) {
                        ActivityCompat.requestPermissions(fragment.getActivity(), new String[]{Manifest.permission.CAMERA}, REQUEST_PERMISSION_CAMERA);
                        return;
                    }

                    if (ContextCompat.checkSelfPermission(getContext(), Manifest.permission.READ_EXTERNAL_STORAGE) != PackageManager.PERMISSION_GRANTED)
                    {
                        ActivityCompat.requestPermissions(fragment.getActivity(), new String[]{Manifest.permission.READ_EXTERNAL_STORAGE}, REQUEST_READ_STORAGE_PERMISSION);
                        return;
                    }

                } else if (activity != null) {
                    if (ContextCompat.checkSelfPermission(getContext(), Manifest.permission.CAMERA) != PackageManager.PERMISSION_GRANTED) {
                        ActivityCompat.requestPermissions(activity, new String[]{Manifest.permission.CAMERA}, REQUEST_PERMISSION_CAMERA);
                        return;
                    }


                    if (ContextCompat.checkSelfPermission(getContext(), Manifest.permission.READ_EXTERNAL_STORAGE) != PackageManager.PERMISSION_GRANTED)
                    {
                        ActivityCompat.requestPermissions(activity, new String[]{Manifest.permission.READ_EXTERNAL_STORAGE}, REQUEST_READ_STORAGE_PERMISSION);
                        return;
                    }
                }


                if (fragment != null) {
                    contactDialog = new PhotoContactDialog(fragment
                            .getActivity());
                } else if (activity != null) {
                    contactDialog = new PhotoContactDialog(activity);
                } else {
                    contactDialog = null;
                    return;
                }

                contactDialog.getTitleTextView().setText(getContext().getString(R.string.photo_contact_title));
                contactDialog.setOnGalleryListener(new OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        dispatchGalleryIntent();
                        contactDialog.dismiss();
                    }
                });
                contactDialog.setOnTakePictureListener(new OnClickListener() {

                    @Override
                    public void onClick(View v) {
                        dispatchTakePictureIntent();
                        contactDialog.dismiss();

                    }
                });
                contactDialog.show();

            }
        });// onclick
    }

    private void dispatchGalleryIntent() {
        Intent i = new Intent(Intent.ACTION_PICK,
                MediaStore.Images.Media.INTERNAL_CONTENT_URI);
        if (activity != null)
            activity.startActivityForResult(i, ACTION_GALLERY_PHOTO);
        else if (fragment != null)
           /* fragment.getActivity().startActivityForResult(i,
                    ACTION_GALLERY_PHOTO);*/
            fragment.startActivityForResult(i, ACTION_GALLERY_PHOTO);
    }

    private void dispatchTakePictureIntent() {
        Intent takePictureIntent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);

        try {
            myPictureFile = createImageFile();
            mCurrentPhotoPath = myPictureFile.getAbsolutePath();
            takePictureIntent.putExtra(MediaStore.EXTRA_OUTPUT,
                    Uri.fromFile(myPictureFile));
        } catch (IOException e) {
            e.printStackTrace();
            myPictureFile = null;
            mCurrentPhotoPath = null;
        }
        if (activity != null)
            activity.startActivityForResult(takePictureIntent,
                    ACTION_TAKE_PHOTO);
        else if (fragment != null)
         /*   fragment.getActivity().startActivityForResult(takePictureIntent,
                    ACTION_TAKE_PHOTO);*/
            fragment.startActivityForResult(takePictureIntent,
                    ACTION_TAKE_PHOTO);
    }

    private File createImageFile() throws IOException {
        // Create an image file name
        String timeStamp = new SimpleDateFormat("yyyyMMdd_HHmmss")
                .format(new Date());
        String imageFileName = JPEG_FILE_PREFIX + timeStamp + JPEG_FILE_SUFFIX;
        String nameAlbum = Environment.getExternalStorageDirectory()
                .getAbsolutePath() + "/" + nameDir;// getString(R.string.app_name);
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

    public String getmCurrentPhotoPath() {
        return mCurrentPhotoPath;
    }

}