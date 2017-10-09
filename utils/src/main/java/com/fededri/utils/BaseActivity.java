package com.fededri.utils;

import android.app.ProgressDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.view.View;



/**
 * Created by Federico Torres on 27/9/2017.
 */

public class BaseActivity extends AppCompatActivity {

    private final String TAG = "BaseActivity";


    private ProgressDialog progressDialog = null;


    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

    }

    public void showProgressDialog(String message) {
        progressDialog = ProgressDialog.show(this, getString(R.string.app_name), message);
    }


    public void showProgressDialog() {
        if (progressDialog == null) {
            progressDialog = ProgressDialog.show(this,
                    getString(R.string.app_name), getString(R.string.loading));
        }
        progressDialog.show();
    }

    public void dismissProgressDialog() {
        if (progressDialog != null) {
            progressDialog.dismiss();
        }
    }

    @Override
    public void onBackPressed() {
        super.onBackPressed();
    }




    protected void showAlert(String heading, String body) {
        // Instantiate an AlertDialog.Builder with its constructor
        AlertDialog.Builder builder = new AlertDialog.Builder(this);

        // Chain together various setter methods to set the dialog characteristics
        builder.setMessage(body)
                .setTitle(heading);

        // Get the AlertDialog from create() and then show() it
        AlertDialog dialog = builder.create();
        dialog.setButton(DialogInterface.BUTTON_POSITIVE, "Ok", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                dialog.dismiss();
            }
        });

        dialog.show();
    }


    protected void showDialogMessage(int stringMessage, Context context,
                                     @Nullable final View.OnClickListener clickListener) {
        if (context == null) {
            return;
        }
        AlertDialog.Builder builder = new AlertDialog.Builder(context);
        builder.setMessage(stringMessage)
                .setTitle(R.string.app_name)
                .setCancelable(false)
                .setNeutralButton("Ok",
                        new DialogInterface.OnClickListener() {
                            public void onClick(DialogInterface dialog, int id) {

                                if (clickListener != null) {
                                    clickListener.onClick(null);

                                } else {
                                    dialog.cancel();
                                }
                            }
                        });
        AlertDialog alert = builder.create();
        alert.show();
    }


    public void showDialog(String message, Context context,
                           @Nullable final DialogInterface.OnClickListener clickListener) {
        if (context == null) {
            return;
        }
        AlertDialog.Builder builder = new AlertDialog.Builder(context);
        builder.setMessage(message)
                .setTitle(R.string.app_name)
                .setCancelable(false)
                .setNeutralButton("Ok",
                        new DialogInterface.OnClickListener() {
                            public void onClick(DialogInterface dialog, int id) {

                                if (clickListener != null) {
                                    clickListener.onClick(dialog, id);

                                } else {
                                    dialog.cancel();
                                }
                            }
                        });
        AlertDialog alert = builder.create();
        alert.show();
    }

    public void showDialog(String message, Context context,
                           final DialogInterface.OnClickListener okListener, DialogInterface.OnClickListener negativeListener) {
        if (context == null) {
            return;
        }
        AlertDialog.Builder builder = new AlertDialog.Builder(context);
        builder.setMessage(message)
                .setTitle(R.string.app_name)
                .setCancelable(false)
                .setNeutralButton("Ok",
                        new DialogInterface.OnClickListener() {
                            public void onClick(DialogInterface dialog, int id) {

                                okListener.onClick(dialog, id);
                            }
                        })
                .setNegativeButton("No", negativeListener);

        AlertDialog alert = builder.create();
        alert.show();
    }


}
