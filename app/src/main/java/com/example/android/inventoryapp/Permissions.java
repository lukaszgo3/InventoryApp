package com.example.android.inventoryapp;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.support.v4.app.ActivityCompat;

class Permissions {

    static final int PERMISSIONS_IMAGE = 0;

    static void permissionsDialog(final String string, final Context context, final String permission) {

        AlertDialog.Builder alertBuilder = new AlertDialog.Builder(context);
        alertBuilder.setCancelable(true);
        alertBuilder.setTitle("Permissions required");
        alertBuilder.setMessage(string + "Permissions required");

        alertBuilder.setPositiveButton(android.R.string.yes,
                new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int which) {
                        ActivityCompat.requestPermissions((Activity) context,
                                new String[]{permission},
                                PERMISSIONS_IMAGE);
                    }
                });

        AlertDialog alertDialog = alertBuilder.create();
        alertDialog.show();
    }
}