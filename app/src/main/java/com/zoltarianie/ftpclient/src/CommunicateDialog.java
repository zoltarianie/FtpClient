package com.zoltarianie.ftpclient.src;

import android.app.AlertDialog;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.DialogInterface;

public class CommunicateDialog {
    private static CommunicateDialog ourInstance = null;
    private Context context;

    public static CommunicateDialog getInstance(Context context) {
        if(ourInstance == null){
            synchronized (FtpList.class){
                ourInstance = new CommunicateDialog(context);
            }
        }
        return ourInstance;
    }

    public static CommunicateDialog getInstance() {
        return ourInstance;
    }

    private CommunicateDialog(Context context) {
        this.context = context;
    }

    public void setContext(Context context) {
        this.context = context;
    }

    public void showCommunicateOk(String comText) {
        AlertDialog.Builder alertDialogBuilder = new AlertDialog.Builder(context);

        // set title
        alertDialogBuilder.setTitle("Your Title");

        // set dialog message
        alertDialogBuilder
                .setMessage("Click yes to exit!")
                .setPositiveButton("Yes",new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog,int id) {
                        // if this button is clicked, close
                        // current activity

                    }
                });
                /*
                .setCancelable(false)
                .setNegativeButton("No",new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog,int id) {
                        // if this button is clicked, just close
                        // the dialog box and do nothing
                        dialog.cancel();
                    }
                });
                */

        // create alert dialog
        AlertDialog alertDialog = alertDialogBuilder.create();

        // show it
        alertDialog.show();
    }

    public void showAlert(String title, String text) {
        AlertDialog.Builder alertDialogBuilder = new AlertDialog.Builder(context);

        // set title
        alertDialogBuilder.setTitle(title);

        // set dialog message
        alertDialogBuilder
            .setMessage(text)
            .setNegativeButton("close", new DialogInterface.OnClickListener() {
                public void onClick(DialogInterface dialog,int id) {
                    dialog.cancel();
                }
            });

        // create alert dialog
        AlertDialog alertDialog = alertDialogBuilder.create();

        // show it
        alertDialog.show();
    }

    // ProgressDialog -------------------------------------------
    public ProgressDialog pd = null;
    public void showProgresDialog() {
        showProgresDialog(false);
    }
    public void showProgresDialog(boolean withCancel) {
        pd = new ProgressDialog(context);
        pd.setTitle("Processing...");
        pd.setMessage("Please wait.");
        pd.setCancelable(false);
        pd.setIndeterminate(true);
        if(withCancel){
            pd.setButton(DialogInterface.BUTTON_NEGATIVE, "Cancel", new DialogInterface.OnClickListener() {
                @Override
                public void onClick(DialogInterface dialog, int which) {
                    pd.dismiss();
                    pd = null;
                }
            });
        }
        pd.show();
    }

    public void ProgresDialogSetNewText(String newTxt) {
        if(pd!=null) {
            pd.setMessage(newTxt);
        }
    }

    public void hideProgresDialog() {
        if(pd!=null) {
            pd.dismiss();
        }
        pd = null;
    }
}
