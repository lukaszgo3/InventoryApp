package com.example.android.inventoryapp;

import android.Manifest;
import android.app.Activity;
import android.app.AlertDialog;
import android.app.LoaderManager;
import android.content.ContentValues;
import android.content.Context;
import android.content.CursorLoader;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.Loader;
import android.content.pm.PackageManager;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.drawable.BitmapDrawable;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.ActivityCompat;
import android.support.v4.app.NavUtils;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AppCompatActivity;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.example.android.inventoryapp.data.ItemsContract.ItemsEntry;

import java.nio.ByteBuffer;
import java.util.Arrays;

public class EditorActivity extends AppCompatActivity implements LoaderManager.LoaderCallbacks<Cursor>, QuantityDialog.QuantityListener {

    private static final int LOADER = 0;
    private Uri currentUri;
    private ImageView mImageView;
    private Uri imageUri;
    private EditText mName;
    private EditText mPrice;
    private TextView mQuantity;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.edit_items);

        Intent i = getIntent();
        currentUri = i.getData();
        setTitle("Edit");
        getLoaderManager().initLoader(LOADER, null, this);

        mImageView = (ImageView) findViewById(R.id.imageId);
        mName = (EditText) findViewById(R.id.nameEditId);
        mPrice = (EditText) findViewById(R.id.priceEditId);
        mQuantity = (TextView) findViewById(R.id.quantityTextId);

        mImageView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                if (checkPermission(EditorActivity.this)) {

                    startActivityForResult(new Intent(Intent.ACTION_PICK, android.provider.MediaStore.Images.Media.INTERNAL_CONTENT_URI),
                            Permissions.PERMISSIONS_IMAGE);
                }
            }
        });

        Button quantityButton = (Button) findViewById(R.id.buttonQuantity);
        quantityButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Integer quantity = Integer.parseInt(mQuantity.getText().toString().trim());
                QuantityDialog quantityFragment = QuantityDialog.newInstance(quantity);
                quantityFragment.show(getFragmentManager(), "dialog");
            }
        });
    }

    public boolean checkPermission(final Context context) {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            if (ContextCompat.checkSelfPermission(context, Manifest.permission.READ_EXTERNAL_STORAGE) != PackageManager.PERMISSION_GRANTED) {

                if (ActivityCompat.shouldShowRequestPermissionRationale((Activity) context, Manifest.permission.READ_EXTERNAL_STORAGE)) {

                    Permissions.permissionsDialog("External storage", context, Manifest.permission.READ_EXTERNAL_STORAGE);

                } else {

                    ActivityCompat.requestPermissions((Activity) context, new String[]{Manifest.permission.READ_EXTERNAL_STORAGE}, Permissions.PERMISSIONS_IMAGE);
                }
                return false;
            } else {
                return true;
            }

        } else {
            return true;
        }
    }

    public Loader<Cursor> onCreateLoader(int id, Bundle args) {

        String[] projection = {
                ItemsEntry._ID,
                ItemsEntry.COLUMN_ITEM_NAME,
                ItemsEntry.COLUMN_ITEM_QUANTITY,
                ItemsEntry.COLUMN_ITEM_PRICE,
                ItemsEntry.COLUMN_ITEM_IMAGE,};

        return new CursorLoader(
                this,
                currentUri,
                projection,
                null,
                null,
                null);
    }

    @Override
    public void onLoadFinished(Loader<Cursor> loader, Cursor data) {

        if (data == null || data.getCount() < 1) {
            return;
        }

        if (data.moveToFirst()) {

            int imageColumn = data.getColumnIndex(ItemsEntry.COLUMN_ITEM_IMAGE);
            int nameColumn = data.getColumnIndex(ItemsEntry.COLUMN_ITEM_NAME);
            int priceColumn = data.getColumnIndex(ItemsEntry.COLUMN_ITEM_PRICE);
            int quantityColumn = data.getColumnIndex(ItemsEntry.COLUMN_ITEM_QUANTITY);

            String image = data.getString(imageColumn);
            String name = data.getString(nameColumn);
            Float price = data.getFloat(priceColumn);
            Integer quantity = data.getInt(quantityColumn);

            mName.setText(name);
            mPrice.setText(Float.toString(price));
            mQuantity.setText(Integer.toString(quantity));

            if (image != null) {

                mImageView.setImageURI(Uri.parse(image));
            }
        }
    }

    @Override
    public void onLoaderReset(Loader<Cursor> loader) {

        mImageView.setImageDrawable(null);
        mName.setText("");
        mPrice.setText(Float.toString(0));
        mQuantity.setText(Integer.toString(0));
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {

        switch (item.getItemId()) {

            case android.R.id.home:
                NavUtils.navigateUpFromSameTask(EditorActivity.this);
                return true;
        }
        return super.onOptionsItemSelected(item);
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        if (requestCode == Permissions.PERMISSIONS_IMAGE && requestCode == Activity.RESULT_OK) {

            Uri image = data.getData();
            imageUri = Uri.parse(image.toString());
            mImageView.setImageURI(image);
        }
    }

    @Override
    public void finishQuantityDialog(String quantity) {
        mQuantity.setText(quantity);
    }

    public void onOrder(View view) {

        String name = mName.getText().toString().trim();
        String message = orderText(name);

        Intent intent = new Intent(Intent.ACTION_SENDTO);
        intent.setData(Uri.parse("mailto:"));
        intent.putExtra(Intent.EXTRA_SUBJECT, "Order for " + name);
        intent.putExtra(Intent.EXTRA_TEXT, message);

        if (intent.resolveActivity(getPackageManager()) != null) {
            startActivity(intent);
        }
    }

    public String orderText(String name) {
        String message = "";
        message += "I want order " + name;
        message += "\n" + "Thank you";
        return message;
    }

    public void onSave(View view) {
        saveItems();
        finish();
    }

    private void saveItems() {

        String name = mName.getText().toString().trim();
        Integer quantity = Integer.parseInt(mQuantity.getText().toString().trim());
        Float price = 0.0f;

        if (!"".equals(mPrice.getText().toString().trim()))
            price = Float.parseFloat(mPrice.getText().toString().trim());

        ContentValues contentValues = new ContentValues();
        contentValues.put(ItemsEntry.COLUMN_ITEM_NAME, name);


        Bitmap icLauncher = BitmapFactory.decodeResource(getResources(), R.mipmap.ic_launcher);
        Bitmap bitmap = ((BitmapDrawable) mImageView.getDrawable()).getBitmap();
        if (!equals(icLauncher, bitmap) && imageUri != null) {
            contentValues.put(ItemsEntry.COLUMN_ITEM_IMAGE, imageUri.toString());
        }

        contentValues.put(ItemsEntry.COLUMN_ITEM_QUANTITY, quantity);

        contentValues.put(ItemsEntry.COLUMN_ITEM_PRICE, price);

        int rowsAffected = getContentResolver().update(currentUri, contentValues, null, null);

        if (rowsAffected == 0) {
            Toast.makeText(this, "Updating Error", Toast.LENGTH_SHORT).show();
        } else {
            Toast.makeText(this, "Updated successful", Toast.LENGTH_SHORT).show();
        }
    }

    private boolean equals(Bitmap icLauncher, Bitmap bitmap) {

        ByteBuffer buffer1 = ByteBuffer.allocate(icLauncher.getHeight() * icLauncher.getRowBytes());
        icLauncher.copyPixelsToBuffer(buffer1);

        ByteBuffer buffer2 = ByteBuffer.allocate(bitmap.getHeight() * bitmap.getRowBytes());
        bitmap.copyPixelsToBuffer(buffer2);

        return Arrays.equals(buffer1.array(), buffer2.array());
    }

    public void onDelete(View view) {
        deleteDialog();
    }

    private void deleteDialog() {

        AlertDialog.Builder builder = new AlertDialog.Builder(this);

        builder.setMessage("Do you want delete this?");
        builder.setPositiveButton("Delete", new DialogInterface.OnClickListener() {
            public void onClick(DialogInterface dialog, int id) {
                deleteProduct();
            }
        });

        builder.setNegativeButton("Cancel", new DialogInterface.OnClickListener() {
            public void onClick(DialogInterface dialog, int id) {
            }
        });

        AlertDialog alertDialog = builder.create();
        alertDialog.show();
    }

    private void deleteProduct() {

        int rowsDeleted = getContentResolver().delete(currentUri, null, null);

        if (rowsDeleted == 0) {
            Toast.makeText(this, "Delete error", Toast.LENGTH_SHORT).show();
        } else {
            Toast.makeText(this, "Deleted successful", Toast.LENGTH_SHORT).show();
        }
        finish();
    }

}