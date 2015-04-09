package com.zoltarianie.ftpclient.src;

import android.content.ContentValues;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.content.Context;
import android.util.Log;
import java.util.HashMap;

public class FtpList extends SQLiteOpenHelper {

    private static FtpList ourInstance = null;

    public static FtpList getInstance(Context context) {
        if(ourInstance == null){
            synchronized (FtpList.class){
                ourInstance = new FtpList(context);
            }
        }
        return ourInstance;
    }

    public static FtpList getInstance() {
        return ourInstance;
    }

    private FtpList(Context context) {
        super(context, DATABASE_NAME , null, 25);
        _context = context;
    }

    public static final String DATABASE_NAME = "ftpclient.db";
    private Context _context;

    @Override
    public void onCreate(SQLiteDatabase db) {
        db.execSQL("CREATE TABLE IF NOT EXISTS ftplist (id INTEGER PRIMARY KEY AUTOINCREMENT, name text, url text, user text, pass text, rank int, count int);");
    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        Log.i("info", "DBHelper - reset");
        db.execSQL("DROP TABLE IF EXISTS ftplist;");
        onCreate(db);
    }

    public long editFtp(long iEditId, HashMap<String,String> aToAdd) {
        SQLiteDatabase db = this.getWritableDatabase();

        ContentValues values = new ContentValues();
        values.put("name", aToAdd.get("name"));
        values.put("url", aToAdd.get("url"));
        values.put("user", aToAdd.get("user"));
        values.put("pass", aToAdd.get("pass"));
        values.put("rank", 0);
        values.put("count", 0);

        int iRowChange = db.update("ftplist", values, "id ="+iEditId, null);
        if(iRowChange == 0){
            db.close();
            return(addFtp(aToAdd));
        }

        db.close();
        return(iEditId);
    }

    public long addFtp(HashMap<String,String> aToAdd) {
        SQLiteDatabase db = this.getWritableDatabase();

        ContentValues values = new ContentValues();
        values.put("name", aToAdd.get("name"));
        values.put("url", aToAdd.get("url"));
        values.put("user", aToAdd.get("user"));
        values.put("pass", aToAdd.get("pass"));
        values.put("rank", 0);
        values.put("count", 0);

        long ret = db.insert("ftplist", null, values);
        db.close();

        return(ret);
    }

    public HashMap<String,String>[] returnList() {
        SQLiteDatabase db = this.getWritableDatabase();

        Cursor c = db.rawQuery("SELECT * FROM ftplist;", null);
        c.moveToFirst();
        HashMap<String,String>[] retData = new HashMap[c.getCount()-1];
        int i = 0;
        while (c.moveToNext()) {
            retData[i] = splitCToArray(c);
            i++;
        }
        c.close();

        return(retData);
    }

    public HashMap<String,String> getOnePoz(long iId) {
        SQLiteDatabase db = this.getWritableDatabase();

        Cursor c = db.rawQuery("SELECT * FROM ftplist WHERE id="+iId+" LIMIT 1;", null);
        c.moveToFirst();
        HashMap<String,String> retData = splitCToArray(c);
        c.close();

        return(retData);
    }


    public Boolean removeFromList(int iId) {
        SQLiteDatabase db = this.getWritableDatabase();
        return((db.delete("ftplist", "id=" + iId, null) != 0));
    }

    private HashMap<String, String> splitCToArray(Cursor c) {
        HashMap<String, String> listUnder = new HashMap<String, String>();
        listUnder.put("id", ""+c.getString(c.getColumnIndex("id")));
        listUnder.put("name", ""+c.getString(c.getColumnIndex("name")));
        listUnder.put("url", ""+c.getString(c.getColumnIndex("url")));
        listUnder.put("user", ""+c.getString(c.getColumnIndex("user")));
        listUnder.put("pass", ""+c.getString(c.getColumnIndex("pass")));
        return(listUnder);
    }

}
