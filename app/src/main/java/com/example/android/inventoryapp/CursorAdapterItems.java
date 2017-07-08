package com.example.android.inventoryapp;

import android.content.ContentUris;
import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.net.Uri;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.CursorAdapter;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.example.android.inventoryapp.data.ItemsContract.ItemsEntry;

/**
 * Created by Lach on 08.07.2017.
 */

public class CursorAdapterItems extends CursorAdapter {

    private Context mContext;

    public CursorAdapterItems(Context context, Cursor cursor){
        super(context, cursor, 0);
    }

    @Override
    public View newView(Context context, Cursor cursor, ViewGroup parent) {
        return LayoutInflater.from(context).inflate(R.layout.ItemDetail, parent, false);
    }

    @Override
    public void bindView(View view, Context context, Cursor cursor) {

        mContext = context;

        ImageView image = (ImageView) view.findViewById(R.id.imageId);
        TextView name = (TextView) view.findViewById(R.id.nameId);
        TextView price = (TextView) view.findViewById(R.id.priceTextId);
        TextView quantity = (TextView) view.findViewById(R.id.quantityTextId);

        final String xImage = cursor.getString(cursor.getColumnIndexOrThrow(ItemsEntry.COLUMN_ITEM_IMAGE));
        final String xName = cursor.getString(cursor.getColumnIndexOrThrow(ItemsEntry.COLUMN_ITEM_NAME));
        final Float xPrice = cursor.getFloat(cursor.getColumnIndexOrThrow(ItemsEntry.COLUMN_ITEM_PRICE));
        final Integer xQuantity = cursor.getInt(cursor.getColumnIndexOrThrow(ItemsEntry.COLUMN_ITEM_QUANTITY));

        name.setText(xName);
        price.setText(Float.toString(xPrice));
        quantity.setText(Integer.toString(xQuantity));
        if(xImage != null){
            image.setVisibility(View.VISIBLE);
            image.setImageURI(Uri.parse(xImage));
        }else{
            image.setVisibility(View.GONE);
        }

        Button button = (Button) view.findViewById(R.id.buttonSellId);
        button.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                if (v != null){

                    Object object = v.getTag();
                    String string = object.toString();

                    ContentValues contentValues = new ContentValues();
                    contentValues.put(ItemsEntry.COLUMN_ITEM_IMAGE, xImage);
                    contentValues.put(ItemsEntry.COLUMN_ITEM_NAME, xName);
                    contentValues.put(ItemsEntry.COLUMN_ITEM_PRICE, xPrice);
                    contentValues.put(ItemsEntry.COLUMN_ITEM_QUANTITY, xQuantity >= 1? xQuantity - 1: 0);

                    Uri currentUri = ContentUris.withAppendedId(ItemsEntry.CONTENT_URI, Integer.parseInt(string));
                    int rows = mContext.getContentResolver().update(currentUri, contentValues, null, null);

                    if (rows == 0 || xQuantity == 0){

                        Toast.makeText(mContext, "Error: can't sell product", Toast.LENGTH_SHORT).show();
                    }
                }
            }
        });

        Object object = cursor.getInt(cursor.getColumnIndex(ItemsEntry._ID));
        button.setTag(object);
    }
}
