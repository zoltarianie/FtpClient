package com.zoltarianie.ftpclient;

import android.support.v7.app.ActionBarActivity;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;
import java.util.HashMap;
import com.zoltarianie.ftpclient.src.*;

public class MainActivityFtpClient extends ActionBarActivity  {

    private long iEditId = 0;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main_activity_ftp_client);

        FtpList.getInstance(this);
        CommunicateDialog.getInstance(this);
    }

    @Override
    public void onResume() {
        super.onResume();
        CommunicateDialog.getInstance().setContext(this);

        LinearLayout llList = (LinearLayout) findViewById(R.id.btnMenagerList);
        llList.setVisibility(View.INVISIBLE);

        Button btnVieW = (Button) findViewById(R.id.btnAddFirst);
        btnVieW.setVisibility(View.VISIBLE);

        ((EditText) findViewById(R.id.ftp_add_name)).setText("");
        ((EditText) findViewById(R.id.ftp_add_url)).setText("");
        ((EditText) findViewById(R.id.ftp_add_user)).setText("");
        ((EditText) findViewById(R.id.ftp_add_pass)).setText("");

        refreshFtpList();
    }

    public void efFtpListName(View view) {
        iEditId = ((View) view.getParent()).getId();
        HashMap<String, String> aOnePoz = FtpList.getInstance().getOnePoz(iEditId);

        // @@@ powoduje jakis warning
        ((EditText) findViewById(R.id.ftp_add_name)).setText(aOnePoz.get("name"));
        ((EditText) findViewById(R.id.ftp_add_url)).setText(aOnePoz.get("url"));
        ((EditText) findViewById(R.id.ftp_add_user)).setText(aOnePoz.get("user"));
        ((EditText) findViewById(R.id.ftp_add_pass)).setText(aOnePoz.get("pass"));

        LinearLayout llList = (LinearLayout) findViewById(R.id.btnMenagerList);
        llList.setVisibility(View.VISIBLE);

        Button btnVieW = (Button) findViewById(R.id.btnAddFirst);
        btnVieW.setVisibility(View.INVISIBLE);
    }

    public void efFtpListDel(View view) {
        if(FtpList.getInstance().removeFromList(((View) view.getParent()).getId())) {
            refreshFtpList();
        }
    }

    public void efBtnAddOrSaveAndConnect(View view) {
        String sName = ((EditText) findViewById(R.id.ftp_add_name)).getText().toString();
        String sUrl = ((EditText) findViewById(R.id.ftp_add_url)).getText().toString();
        String sUser = ((EditText) findViewById(R.id.ftp_add_user)).getText().toString();
        String sPass = ((EditText) findViewById(R.id.ftp_add_pass)).getText().toString();

        HashMap<String, String> dataToLog = new HashMap<String, String>();
        dataToLog.put("name", sName);
        dataToLog.put("url", sUrl);
        dataToLog.put("user", sUser);
        dataToLog.put("pass", sPass);

        long pozToConn = 0;
        if(view.getId() == R.id.btnSaveAndConn){
            pozToConn = FtpList.getInstance().editFtp(iEditId, dataToLog);
        } else {
            pozToConn = FtpList.getInstance().addFtp(dataToLog);
        }

        pFtpAddToListAndConnect opFtpAddToListAndConnect = new pFtpAddToListAndConnect(FtpList.getInstance().getOnePoz(pozToConn), this);
        pCaller.getInstance().command_execute(opFtpAddToListAndConnect);
    }

    private void refreshFtpList() {
        LinearLayout llList = (LinearLayout) findViewById(R.id.ftplist_right_colum);
        llList.removeAllViews();
        HashMap<String,String>[] listToPrint = FtpList.getInstance().returnList();
        for (int i=0; i<listToPrint.length; i++ ) {
            RelativeLayout rlListPoz = (RelativeLayout) getLayoutInflater().inflate(R.layout.ftp_list_poz, null);
            TextView box_num = (TextView) rlListPoz.findViewById(R.id.ftp_list_num);
            box_num.setText(i+".");
            TextView box_txt = (TextView) rlListPoz.findViewById(R.id.ftp_list_name);
            box_txt.setText(""+listToPrint[i].get("name"));
            rlListPoz.setId(Integer.parseInt(listToPrint[i].get("id")));
            llList.addView(rlListPoz);
        }
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_main_activity_ftp_client, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        int id = item.getItemId();

        //noinspection SimplifiableIfStatement
        if (id == R.id.action_settings) {
            return true;
        }

        return super.onOptionsItemSelected(item);
    }
}
