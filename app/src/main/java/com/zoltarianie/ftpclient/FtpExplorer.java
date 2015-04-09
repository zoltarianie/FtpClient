package com.zoltarianie.ftpclient;

import android.app.Activity;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.os.Bundle;
import android.os.Handler;
import android.util.Log;
import android.view.View;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.ScrollView;
import android.widget.Spinner;
import android.widget.TextView;

import com.zoltarianie.ftpclient.src.CommunicateDialog;
import com.zoltarianie.ftpclient.src.Event;
import com.zoltarianie.ftpclient.src.ActionMenu;
import com.zoltarianie.ftpclient.src.FtpCon;
import com.zoltarianie.ftpclient.src.SdcCon;

import java.util.HashMap;

public class FtpExplorer extends Activity implements Event {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_ftp_explorer);

        CommunicateDialog.getInstance().setContext(this);
        SdcCon.getInstance().getFilesFromSdc(this, ".");
        FtpCon.getInstance().getFilesFromServ(this);

        ActionMenu actionMenuRight = new ActionMenu("right", (Spinner) findViewById(R.id.spinner_action_right), this, this);
        actionMenuRight.setNewMenu(new String[]{"Copy", "Delete", "New dir"});

        ActionMenu actionMenuLeft = new ActionMenu("left", (Spinner) findViewById(R.id.spinner_action_left), this, this);
        actionMenuLeft.setNewMenu(new String[]{"Copy", "Delete", "New dir"});

        Log.i("ftp", "onCreate");
    }

    public void dispath(String eName, String res) {
        Log.i("ftp", "E: " + eName + " " + res);

        if (eName.equals("act_left")) {
            if (res.equals("Copy")) {
                copyFromSdcToFtp();
            }
            return;
        }

        if (eName.equals("act_right")) {
            if (res.equals("Copy")) {
                copyFromFtpToSdc();
            }
            return;
        }

        if (eName.equals("ftpGetFiles")) {
            if (res.equals("true")) {
                fillRightColumn();
            } else {
                CommunicateDialog.getInstance().showAlert("FTP connection", res);
            }
            return;
        }

        if (eName.equals("sdcGetFiles") && res.equals("true")) {
            fileLeftColumn();
            return;
        }

        if(eName.equals("ftpDownload")) {
            SdcCon.getInstance().getFilesFromSdc(this);
            return;
        }

        if(eName.equals("ftpUpload")) {
            FtpCon.getInstance().getFilesFromServ(this);
            return;
        }
    }

    private void copyFromFtpToSdc() {
        LinearLayout ll_right_column = (LinearLayout) findViewById(R.id.right_column);
        HashMap<String,String>[] aSelectedFiles = getSelected(ll_right_column);

        FtpCon.getInstance().downloadFromFtp(
                ""+ SdcCon.getInstance().getPathToDownload(),
                ""+ FtpCon.getInstance().getPathToDownload(),
                aSelectedFiles,
                this);
    }

    private void copyFromSdcToFtp() {
        LinearLayout ll_left_column = (LinearLayout) findViewById(R.id.left_column);
        HashMap<String,String>[] aSelectedFiles = getSelected(ll_left_column);

        FtpCon.getInstance().uploadToFtp(
                ""+ SdcCon.getInstance().getPathToDownload(),
                ""+ FtpCon.getInstance().getPathToDownload(),
                aSelectedFiles,
                this);
    }

    private HashMap<String,String>[] getSelected(LinearLayout ll_right_column) {
        int numOfSelectedFiles = 0;
        for(int i=0; i<ll_right_column.getChildCount(); ++i) {
            View nextChild = ll_right_column.getChildAt(i);
            if(isSelected(nextChild)) {
                numOfSelectedFiles++;
            }
        }
        HashMap<String,String>[] aSelectedFiles =  new HashMap[numOfSelectedFiles];
        numOfSelectedFiles = 0;
        for(int i=0; i<ll_right_column.getChildCount(); ++i) {
            View nextChild = ll_right_column.getChildAt(i);
            if(isSelected(nextChild)) {
                HashMap<String, String> poz = new HashMap<String, String>();
                poz.put("name", "" + ((TextView) nextChild.findViewById(R.id.dir_list_filename)).getText());
                poz.put("type", "" + nextChild.findViewById(R.id.dir_list_filename).getTag());
                aSelectedFiles[numOfSelectedFiles] = poz;
                numOfSelectedFiles++;
            }
        }

        return(aSelectedFiles);
    }

    protected void fillRightColumn() {
        LinearLayout ll_right_column = (LinearLayout) findViewById(R.id.right_column);
        fillFilesColumn(ll_right_column, FtpCon.getInstance().aFiles);

        TextView tv_right_window_path = (TextView) findViewById(R.id.right_window_path);
        tv_right_window_path.clearFocus();
        tv_right_window_path.setTextKeepState(FtpCon.getInstance().getPathToWindow());

        ScrollView sv_right_column =  (ScrollView) findViewById(R.id.right_column_scroll);
        sv_right_column.fullScroll(ScrollView.FOCUS_UP);
    }

    protected void fileLeftColumn() {
        LinearLayout ll_left_column = (LinearLayout) findViewById(R.id.left_column);
        fillFilesColumn(ll_left_column, SdcCon.getInstance().aFiles);

        TextView tv_left_window_path = (TextView) findViewById(R.id.left_window_path);
        tv_left_window_path.clearFocus();
        tv_left_window_path.setTextKeepState(SdcCon.getInstance().getPathToWindow());

        ScrollView sv_left_column =  (ScrollView) findViewById(R.id.left_column_scroll);
        sv_left_column.fullScroll(ScrollView.FOCUS_UP);
    }

    private void fillFilesColumn(LinearLayout ll_column, HashMap<String,String>[] aFiles) {
        if(aFiles==null) {
            return;
        }
        ll_column.removeAllViews();
        for (int i=0; i < aFiles.length; i++) {
            RelativeLayout rlListPoz = (RelativeLayout) getLayoutInflater().inflate(R.layout.dir_list, null);
            rlListPoz.setId(i);

            ImageView ivIcon = (ImageView) rlListPoz.findViewById(R.id.dir_list_icon);
            if (aFiles[i].get("type").equals("dir")) {
                ivIcon.setImageResource(R.drawable.dir);
            }
            if (aFiles[i].get("name").equals(".") || aFiles[i].get("name").equals("..")) {
                ivIcon.setVisibility(View.INVISIBLE);
            }

            TextView tvFileName = (TextView) rlListPoz.findViewById(R.id.dir_list_filename);
            tvFileName.setText(aFiles[i].get("name"));
            tvFileName.setTag(aFiles[i].get("type"));

            TextView tvFileData = (TextView) rlListPoz.findViewById(R.id.dir_list_filedate);
            tvFileData.setText(aFiles[i].get("mod_dat"));

            ll_column.addView(rlListPoz);
        }
    }

    private Boolean isSelected(View view) {
        if(
            !(view.getBackground() instanceof ColorDrawable) ||
            ((ColorDrawable) view.getBackground()).getColor()!=bgSelectedColor)
        {
            return(false);
        } else {
            return(true);
        }
    }

    private int bgSelectedColor = Color.argb(255, 255, 255, 255);
    private int bgDeSelectedColor = Color.argb(000, 255, 255, 255);
    private int i = 0;
    public void onClickPos(View view) {
        i++;

        Handler handler = new Handler();
        Runnable r = new Runnable() {
            @Override
            public void run() {
                i = 0;
            }
        };

        if (i == 1) { //Single click
            TextView tvFileName = (TextView) view.findViewById(R.id.dir_list_filename);
            if(!tvFileName.getText().equals("..")) {
                if (isSelected(view)) {
                    view.setBackgroundColor(bgDeSelectedColor);
                } else {
                    view.setBackgroundColor(bgSelectedColor);
                }
            }

            handler.postDelayed(r, 250);
        } else if (i == 2) { //Double click
            TextView tvFileName = (TextView) view.findViewById(R.id.dir_list_filename);
            if(((View) view.getParent()).getId() == R.id.right_column){
                FtpCon.getInstance().getFilesFromServ(this, ""+tvFileName.getText());
            } else {
                SdcCon.getInstance().getFilesFromSdc(this, ""+tvFileName.getText());
            }

            i = 0;
        }
    }

    public void efFtpBtnAction(View view) {
        this.finish();
    }
}
