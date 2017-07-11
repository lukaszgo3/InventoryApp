package com.example.android.inventoryapp;

import android.Manifest;
import android.app.Activity;
import android.app.AlertDialog;
import android.app.Dialog;
import android.app.DialogFragment;
import android.content.ContentValues;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.provider.MediaStore;
import android.support.annotation.NonNull;
import android.support.v4.content.ContextCompat;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import com.example.android.inventoryapp.data.ItemsContract;
import com.example.android.inventoryapp.data.ItemsContract.ItemsEntry;

public class AddItem extends DialogFragment {

    String imageUri;

    @Override
    public Dialog onCreateDialog(Bundle savedInstanceState) {

        AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());

        LayoutInflater layoutInflater = getActivity().getLayoutInflater();
        final View addView = layoutInflater.inflate(R.layout.add_item, null);

        Button selectImage = (Button) addView.findViewById(R.id.chooseImageId);

        selectImage.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (permissionChecker(getActivity())) {
                    startActivityForResult(new Intent(Intent.ACTION_PICK,
                                    MediaStore.Images.Media.INTERNAL_CONTENT_URI),
                            Permissions.PERMISSIONS_IMAGE);
                }
            }
        });

        final Dialog addDialog = builder.setView(addView)
                .setPositiveButton("Add item", null)
                .setNegativeButton("Cancel", new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int id) {
                        AddItem.this.getDialog().cancel();
                    }
                })
                .create();

        addDialog.setOnShowListener(new DialogInterface.OnShowListener() {
            @Override
            public void onShow(DialogInterface dialog) {

                Button positiveButton = ((AlertDialog) dialog).getButton(AlertDialog.BUTTON_POSITIVE);

                positiveButton.setOnClickListener(new View.OnClickListener() {

                    @Override
                    public void onClick(View view) {

                        Boolean closeDialog = false;

                        EditText editName = (EditText) addView.findViewById(R.id.nameId);
                        EditText editQuantity = (EditText) addView.findViewById(R.id.quantityId);
                        EditText editPrice = (EditText) addView.findViewById(R.id.priceId);

                        String name = editName.getText().toString().trim();
                        String quantityString = editQuantity.getText().toString().trim();
                        String priceString = editPrice.getText().toString().trim();


                        if (TextUtils.isEmpty(name) || TextUtils.isEmpty(quantityString) || TextUtils.isEmpty(priceString) || imageUri == null) {

                            Toast.makeText(getActivity(), "Please fill out the empty fields and select image", Toast.LENGTH_SHORT).show();

                        }

                        else {
                            Integer quantity = Integer.parseInt(editQuantity.getText().toString().trim());
                            Float price = Float.parseFloat(editPrice.getText().toString().trim());
                            addItem(name, quantity, price, imageUri);
                            closeDialog = true;
                        }

                        if (closeDialog)
                            addDialog.dismiss();
                    }
                });
            }
        });

        return addDialog;
    }

    private void addItem(String name, Integer quantity, Float price, String image) {

        ContentValues values = new ContentValues();
        values.put(ItemsEntry.COLUMN_ITEM_NAME, name);
        values.put(ItemsEntry.COLUMN_ITEM_QUANTITY, quantity);
        values.put(ItemsEntry.COLUMN_ITEM_PRICE, price);

        if (!image.isEmpty()) {
            values.put(ItemsEntry.COLUMN_ITEM_IMAGE, image);
        }

        getActivity().getContentResolver().insert(ItemsContract.ItemsEntry.CONTENT_URI, values);
    }


    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        if (requestCode == Permissions.PERMISSIONS_IMAGE && resultCode == Activity.RESULT_OK) {
            Uri image = data.getData();
            imageUri = image.toString();

        }
    }

    public boolean permissionChecker(final Context context) {

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {

            if (ContextCompat.checkSelfPermission(context, Manifest.permission.READ_EXTERNAL_STORAGE) != PackageManager.PERMISSION_GRANTED) {

                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {

                    if (shouldShowRequestPermissionRationale(Manifest.permission.READ_EXTERNAL_STORAGE)) {

                        Permissions.permissionsDialog("External storage", context, Manifest.permission.READ_EXTERNAL_STORAGE);

                    } else {

                        requestPermissions(new String[]{
                                Manifest.permission.READ_EXTERNAL_STORAGE}, Permissions.PERMISSIONS_IMAGE);
                    }
                }
                return false;
            } else {
                return true;
            }

        } else

        {
            return true;
        }
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {

        switch (requestCode) {

            case Permissions.PERMISSIONS_IMAGE: {

                if (grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {

                    startActivityForResult(new Intent(Intent.ACTION_PICK, MediaStore.Images.Media.INTERNAL_CONTENT_URI),
                            Permissions.PERMISSIONS_IMAGE);
                } else {

                    Toast.makeText(getActivity(), "Permission Denied", Toast.LENGTH_SHORT).show();
                }
            }
        }

        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
    }

}