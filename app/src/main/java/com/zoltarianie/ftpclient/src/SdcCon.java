package com.zoltarianie.ftpclient.src;

import android.os.Environment;

import java.io.File;
import java.text.SimpleDateFormat;
import java.util.HashMap;

public class SdcCon {
    private static SdcCon ourInstance = new SdcCon();

    public static SdcCon getInstance() {
        return ourInstance;
    }

    private SdcCon() { }

    private String path;
    private Event event;

    public HashMap<String,String>[] aFiles = null;


    public String getPathToWindow() {
        String restPat = getPath();
        restPat += restPat.equals("")?"/":"";
        return("sdc:/"+restPat);
    }

    public String getPathToDownload() {
        return(""+Environment.getExternalStorageDirectory().toString() + getPath());
    }

    // ---------------------------------------------------------------------------
    public void getFilesFromSdc(Event event) {
        this.event = event;
        aFiles = getFilesFromServ();
        event.dispath("sdcGetFiles", "true");
    }

    public void getFilesFromSdc(Event event, String newPath) {
        addToArrayPath(newPath);
        this.event = event;
        aFiles = getFilesFromServ();
        event.dispath("sdcGetFiles", "true");
    }

    public HashMap<String,String>[] getFilesFromServ() {
        path = Environment.getExternalStorageDirectory().toString() + getPath();
        File f = new File(path);
        File file[] = f.listFiles();

        int addI = (getPath().equals(""))?0:1;
        HashMap<String,String>[] retData = new HashMap[file.length+addI];
        SimpleDateFormat sdf = new SimpleDateFormat("MM/dd/yyyy HH:mm:ss");

        if(addI==1){
            HashMap<String, String> poz = new HashMap<String, String>();
            poz.put("name", "..");
            poz.put("type", "");
            poz.put("mod_dat", sdf.format((new java.util.Date()).getTime()));
            retData[0] = poz;
        }

        for (int i=0; i < file.length; i++) {
            HashMap<String, String> poz = new HashMap<String, String>();
            poz.put("name", ""+file[i].getName());
            poz.put("type", ""+(file[i].isDirectory()?"dir":"file"));
            poz.put("mod_dat", ""+sdf.format(file[i].lastModified()));
            retData[i+addI] = poz;
        }
        return(retData);
    }

    // DIR PATH ----------------------
    String aPath[] = {};
    private void addToArrayPath(String url) {
        String newAPath[];

        if(url.equals(".")){
            newAPath = new String[0];
        } else if(url.equals("..")) {
            if(aPath.length <= 1){
                newAPath = new String[0];
            } else {
                newAPath = new String[aPath.length - 1];
                for (int j = 0; j < aPath.length - 1; j++) {
                    newAPath[j] = aPath[j];
                }
            }
        } else {
            File f = new File(path + "/" +url);
            if(f.isDirectory()) {
                newAPath = new String[aPath.length + 1];
                for (int j = 0; j < aPath.length; j++) {
                    newAPath[j] = aPath[j];
                }
                newAPath[aPath.length] = url;
            } else {
                newAPath = aPath;
            }
        }

        aPath = newAPath;
    }

    private String getPath() {
        String retPath = "";
        for (int j = 0; j < aPath.length; j++) {
            retPath += "/"+aPath[j];
        }
        return(retPath);
    }
}
