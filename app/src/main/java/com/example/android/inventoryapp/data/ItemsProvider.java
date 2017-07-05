package com.example.android.inventoryapp.data;


import android.content.ContentProvider;
import android.content.ContentUris;
import android.content.ContentValues;
import android.content.UriMatcher;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.net.Uri;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.util.Log;

import com.example.android.inventoryapp.data.ItemsContract.ItemsEntry;

public class ItemsProvider extends ContentProvider {


    static final int ITEMS = 100;
    static final int ITEM_ID = 110;
    private ItemsDbHelper mItemsDbHelper;

    public static final String LOG_TAG = ItemsDbHelper.class.getSimpleName();

    private static final UriMatcher sUriMatcher = new UriMatcher(UriMatcher.NO_MATCH);

    static {
        sUriMatcher.addURI(ItemsContract.CONTENT_AUTHORITY, ItemsContract.PATH_ITEMS, ITEMS);
        sUriMatcher.addURI(ItemsContract.CONTENT_AUTHORITY, ItemsContract.PATH_ITEMS + "/#", ITEM_ID);
    }

    @Override
    public boolean onCreate() {
        mItemsDbHelper = new ItemsDbHelper(getContext());
        return false;
    }

    @Nullable
    @Override
    public Cursor query(@NonNull Uri uri, @Nullable String[] projection, @Nullable String selection, @Nullable String[] selectionArgs, @Nullable String sortOrder) {

        SQLiteDatabase db = mItemsDbHelper.getReadableDatabase();
        Cursor cursor;
        int match = sUriMatcher.match(uri);

        switch (match) {
            case ITEMS:
                cursor = db.query(ItemsEntry.TABLE_NAME, projection, selection, selectionArgs, null, null, sortOrder);
                break;

            case ITEM_ID:
                selection = ItemsEntry._ID + "=?";
                selectionArgs = new String[]{
                        String.valueOf(ContentUris.parseId(uri))
                };
                cursor = db.query(ItemsEntry.TABLE_NAME, projection, selection, selectionArgs, null, null, sortOrder);
                break;

            default:
                throw new IllegalArgumentException("Error: Query" + uri);
        }

        cursor.setNotificationUri(getContext().getContentResolver(), uri);

        return cursor;
    }

    @Nullable
    @Override
    public String getType(@NonNull Uri uri) {

        final int match = sUriMatcher.match(uri);

        switch (match) {

            case ITEMS:
                return ItemsEntry.CONTENT_LIST;
            case ITEM_ID:
                return ItemsEntry.CONTENT_ITEM;
            default:
                throw new IllegalArgumentException("Error URI" + uri + "match" + match);
        }
    }

    @Nullable
    @Override
    public Uri insert(@NonNull Uri uri, @Nullable ContentValues values) {

        final int match = sUriMatcher.match(uri);

        switch (match) {
            case ITEMS:
                return insertItem(uri, values);
            default:
                throw new IllegalArgumentException("It is not supported" + uri);
        }
    }

    private Uri insertItem(Uri uri, ContentValues values) {

        String name = values.getAsString(ItemsEntry.COLUMN_ITEM_NAME);

        if (name == null) {
            throw new IllegalArgumentException("Name required");
        }

        Integer quantity = values.getAsInteger(ItemsEntry.COLUMN_ITEM_QUANTITY);
        if (quantity != null && quantity < 0) {
            throw new IllegalArgumentException("Required quantity");
        }

        Float price = values.getAsFloat(ItemsEntry.COLUMN_ITEM_PRICE);

        if (price != null && price < 0) {
            throw new IllegalArgumentException("Required price");
        }

        SQLiteDatabase database = mItemsDbHelper.getReadableDatabase();
        long id = database.insert(ItemsEntry.TABLE_NAME, null, values);
        if (id == -1) {
            Log.e(LOG_TAG, "Row Error " + uri);
            return null;
        }

        getContext().getContentResolver().notifyChange(uri, null);

        return ContentUris.withAppendedId(uri, id);
    }

    @Override
    public int delete(@NonNull Uri uri, @Nullable String selection, @Nullable String[] selectionArgs) {

        SQLiteDatabase db = mItemsDbHelper.getWritableDatabase();

        int deleteItems;
        final int match = sUriMatcher.match(uri);

        switch (match) {
            case ITEMS:
                deleteItems = db.delete(ItemsEntry.TABLE_NAME, selection, selectionArgs);
                break;
            case ITEM_ID:
                selection = ItemsEntry._ID + "=?";
                selectionArgs = new String[]{String.valueOf(ContentUris.parseId(uri))};
                deleteItems = db.delete(ItemsEntry.TABLE_NAME, selection, selectionArgs);
                break;
            default:
                throw new IllegalArgumentException("Delete Error " + uri);
        }

        if (deleteItems != 0) {
            getContext().getContentResolver().notifyChange(uri, null);
        }

        return deleteItems;
    }

    @Override
    public int update(@NonNull Uri uri, @Nullable ContentValues values, @Nullable String selection, @Nullable String[] selectionArgs) {

        final int match = sUriMatcher.match(uri);

        switch (match) {

            case ITEMS:
                return upItems(uri, values, selection, selectionArgs);
            case ITEM_ID:
                selection = ItemsEntry._ID + "=?";
                selectionArgs = new String[]{String.valueOf(ContentUris.parseId(uri))};
                return upItems(uri, values, selection, selectionArgs);
            default:
                throw new IllegalArgumentException("Update Error " + uri);
        }
    }

    private int upItems(Uri uri, ContentValues values, String selection, String[] selectionArgs) {

        if (values.containsKey(ItemsEntry.COLUMN_ITEM_NAME)) {

            String name = values.getAsString(ItemsEntry.COLUMN_ITEM_NAME);
            if (name == null) {
                throw new IllegalArgumentException("Name Error");
            }
        }

        if (values.containsKey(ItemsEntry.COLUMN_ITEM_QUANTITY)) {

            Integer quantity = values.getAsInteger(ItemsEntry.COLUMN_ITEM_QUANTITY);
            if (quantity != null && quantity < 0) {
                throw new IllegalArgumentException("Quantity Error");
            }
        }

        if (values.containsKey(ItemsEntry.COLUMN_ITEM_PRICE)) {

            Float price = values.getAsFloat(ItemsEntry.COLUMN_ITEM_PRICE);
            if (price != null && price < 0) {
                throw new IllegalArgumentException("Price Error");
            }
        }

        if (values.size() == 0) {
            return 0;
        }

        SQLiteDatabase database = mItemsDbHelper.getReadableDatabase();

        int rowsUpdated = database.update(ItemsEntry.TABLE_NAME, values, selection, selectionArgs);

        if (rowsUpdated == -1) {
            Log.e(LOG_TAG, "Failed update" + uri);
            return 0;
        }
        if (rowsUpdated != 0) {
            getContext().getContentResolver().notifyChange(uri, null);
        }
        return rowsUpdated;
    }
}