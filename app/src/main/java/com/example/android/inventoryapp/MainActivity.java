package com.example.android.inventoryapp;

import android.app.LoaderManager;
import android.content.ContentUris;
import android.content.CursorLoader;
import android.content.Intent;
import android.content.Loader;
import android.database.Cursor;
import android.net.Uri;
import android.os.Bundle;
import android.support.design.widget.FloatingActionButton;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ListView;

import com.example.android.inventoryapp.data.ItemsContract.ItemsEntry;

public class MainActivity extends AppCompatActivity implements LoaderManager.LoaderCallbacks<Cursor> {

    private static final int LOADER = 0;
    CursorAdapterItems cursorAdapterItems;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        ListView itemsList = (ListView) findViewById(R.id.listViewId);

        FloatingActionButton floatingActionButton = (FloatingActionButton) findViewById(R.id.fabId);
        floatingActionButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                addItem();
            }
        });

        View emptyInventory = findViewById(R.id.emptyInventoryId);
        itemsList.setEmptyView(emptyInventory);

        cursorAdapterItems = new CursorAdapterItems(this, null);
        itemsList.setAdapter(cursorAdapterItems);
        itemsList.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {

                Intent i = new Intent(MainActivity.this, EditorActivity.class);
                Uri currentUri = ContentUris.withAppendedId(ItemsEntry.CONTENT_URI, id);
                i.setData(currentUri);
                startActivity(i);
            }
        });

        getLoaderManager().initLoader(LOADER, null, this);
    }

    private void addItem() {

        AddItem fragment = new AddItem();
        fragment.show(getFragmentManager(), "Add Item");
    }

    @Override
    public Loader<Cursor> onCreateLoader(int id, Bundle args) {
        String[] projection = {
                ItemsEntry._ID,
                ItemsEntry.COLUMN_ITEM_NAME,
                ItemsEntry.COLUMN_ITEM_QUANTITY,
                ItemsEntry.COLUMN_ITEM_PRICE,
                ItemsEntry.COLUMN_ITEM_IMAGE
                };

        return new CursorLoader(this,
                ItemsEntry.CONTENT_URI,
                projection,
                null,
                null,
                null);
    }

    @Override
    public void onLoadFinished(Loader<Cursor> loader, Cursor data) {
        cursorAdapterItems.swapCursor(data);
    }

    @Override
    public void onLoaderReset(Loader<Cursor> loader) {
        cursorAdapterItems.swapCursor(null);
    }
}
