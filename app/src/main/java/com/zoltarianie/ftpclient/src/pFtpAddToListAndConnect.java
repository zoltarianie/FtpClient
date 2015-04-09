package com.zoltarianie.ftpclient.src;

import android.content.Context;
import android.content.Intent;
import com.zoltarianie.ftpclient.FtpExplorer;
import java.util.HashMap;

public class pFtpAddToListAndConnect implements pCommand, Event {

    HashMap<String, String> ftpDataConnection = new HashMap<String, String>();
    Context context;

    public pFtpAddToListAndConnect(HashMap<String, String> ftpDataConnection, Context context){
        this.context = context;
        this.ftpDataConnection = ftpDataConnection;
    }

    public void dispath(String eName, String res) {
        if(eName.equals("ftpCon")) {
            if (res.equals("true")) {
                Intent intent = new Intent(context, FtpExplorer.class);
                context.startActivity(intent);
            } else {
                CommunicateDialog.getInstance().showAlert("FTP connection", res);
            }
        }
    }

    public void execute() {
        HashMap<String, String> ftpDataConn = new HashMap<String, String>();
        ftpDataConn.put("url", "animagic.pl");
        ftpDataConn.put("user", "animagicwww");
        ftpDataConn.put("pass", "zpY0ONB4");

        FtpCon.getInstance().conectToFtp(ftpDataConn, this);
    }

    public void exit() {

    }
}