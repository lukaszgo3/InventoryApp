package com.example.android.inventoryapp;

import android.app.Activity;
import android.app.AlertDialog;
import android.app.Dialog;
import android.app.DialogFragment;
import android.content.DialogInterface;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.EditText;

/**
 * Created by Lach on 09.07.2017.
 */

public class QuantityDialog extends DialogFragment {

    int mQuantity;

    public static interface QuantityListener {
        void finishQuantityDialog(String quantity);
    }

    private QuantityListener mListener;

    @Override
    public void onAttach(Activity activity) {

        super.onAttach(activity);
        try {
            mListener = (QuantityListener) activity;
        } catch (ClassCastException e) {
            throw new ClassCastException(activity.toString()
                    + " must implement QuantityListener");
        }
    }

    static QuantityDialog newInstance(int quantity) {

        QuantityDialog quantityDialog = new QuantityDialog();
        Bundle args = new Bundle();
        args.putInt("quantity", quantity);
        quantityDialog.setArguments(args);
        return quantityDialog;
    }

    @Override
    public Dialog onCreateDialog(Bundle savedInstanceState) {

        mQuantity = getArguments().getInt("quantity");
        AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());
        LayoutInflater inflater = getActivity().getLayoutInflater();
        final View view = inflater.inflate(R.layout.dialog_quantity, null);
        final EditText QuantityEditText = (EditText) view.findViewById(R.id.edit_text_quantity);

        QuantityEditText.setText(Integer.toString(mQuantity));

        builder.setView(view)
                .setPositiveButton("Agree", new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int id) {
                        String quantity = "0";
                        if (!TextUtils.isEmpty(QuantityEditText.getText().toString().trim()))
                            quantity = QuantityEditText.getText().toString().trim();
                        mListener.finishQuantityDialog(quantity);
                    }
                })

                .setNegativeButton("Exit", new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int id) {
                        QuantityDialog.this.getDialog().cancel();
                    }
                });

        AlertDialog alertDialog = builder.create();
        alertDialog.show();

        return alertDialog;
    }
}
