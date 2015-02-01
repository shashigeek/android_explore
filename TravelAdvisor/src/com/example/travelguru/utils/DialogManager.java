package com.example.travelguru.utils;

import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;

import com.example.travelguru.R;

public class DialogManager {

    public void showAlertDialog(Context context, String title, String message,
            Boolean status) {
        AlertDialog.Builder builder = new AlertDialog.Builder(context);

        builder.setTitle(title).setMessage(message).setCancelable(false)
                .setIcon((status) ? R.drawable.success : R.drawable.fail)
                .setPositiveButton("OK", new DialogInterface.OnClickListener() {

                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                    }
                });

        AlertDialog alertDialog = builder.create();

        alertDialog.show();
    }
}
