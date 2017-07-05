package com.example.android.inventoryapp.data;

import android.content.ContentResolver;
import android.net.Uri;
import android.provider.BaseColumns;

class ItemsContract {

    private ItemsContract() {
    }

    static final String CONTENT_AUTHORITY = "com.example.android.inventoryapp";
    static final Uri BASE_CONTENT_URI = Uri.parse("content://" + CONTENT_AUTHORITY);
    static final String PATH_ITEMS = "items";

    public static final class ItemsEntry implements BaseColumns {

        static final String CONTENT_LIST = ContentResolver.CURSOR_DIR_BASE_TYPE + "/" + CONTENT_AUTHORITY + "/" + PATH_ITEMS;
        static final String CONTENT_ITEM = ContentResolver.CURSOR_ITEM_BASE_TYPE + "/" + CONTENT_AUTHORITY + "/" + PATH_ITEMS;
        static final Uri CONTENT_URI = Uri.withAppendedPath(BASE_CONTENT_URI, PATH_ITEMS);

        final static String TABLE_NAME = "items";
        final static String _ID = BaseColumns._ID;
        final static String COLUMN_ITEM_NAME = "name";
        final static String COLUMN_ITEM_IMAGE = "image";
        final static String COLUMN_ITEM_PRICE = "price";
        final static String COLUMN_ITEM_QUANTITY = "quantity";

    }
}