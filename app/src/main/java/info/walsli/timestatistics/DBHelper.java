package info.walsli.timestatistics;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

public class DBHelper extends SQLiteOpenHelper {
    public static boolean lock=false;
    public DBHelper(Context c) {
        super(c, "time.db", null, 2);
    }
    @Override
    public void onCreate(SQLiteDatabase db) {
        db.execSQL("create table timeinfo(_id integer primary key autoincrement,datenum integer,opentime integer,closetime integer)");
        db.execSQL("create table timeofdays(_id integer primary key autoincrement,datenum integer,todaytime integer)");
        db.execSQL("create table timeofapps(_id integer primary key autoincrement,datenum integer,appname text,appseconds integer)");
    }
    public void exec(String s)
    {
        SQLiteDatabase db = getWritableDatabase();
        db.execSQL(s);
    }
    public void cleandb()
    {
        SQLiteDatabase db = getWritableDatabase();
        Cursor c=query("select name from sqlite_master where type='table';");
        while(c.moveToNext())
        {
            db.execSQL("delete from "+c.getString(0));
        }
    }
    public void settlement(int a)
    {
        SQLiteDatabase db = getWritableDatabase();
        Cursor temp=db.rawQuery("select * from timeofdays where datenum="+String.valueOf(a), null);
        if(temp.getCount()!=0)
        {
            db.execSQL("delete from timeofdays where datenum="+String.valueOf(a));
        }
        Cursor c=db.rawQuery("select * from timeinfo where datenum="+String.valueOf(a), null);
        if(c.getCount()!=0)
        {
            int time=0;
            while(c.moveToNext())
            {
                time+=c.getInt(3)-c.getInt(2);
            }
            ContentValues values = new ContentValues();
            values.put("datenum", a);
            values.put("todaytime", time);
            db.insert("timeofdays", null, values);
        }

    }
    public void insertIntoTimeinfo(int a,int b,int c) {
        SQLiteDatabase db = getWritableDatabase();
        ContentValues values = new ContentValues();
        values.put("datenum", a);
        values.put("opentime", b);
        values.put("closetime", c);
        db.insert("timeinfo", null, values);
    }
    public void insertIntoTimeOfApps(int a,String b,int c)
    {
        SQLiteDatabase db = getWritableDatabase();
        ContentValues values = new ContentValues();
        values.put("datenum", a);
        values.put("appname", b);
        values.put("appseconds", c);
        db.insert("timeofapps", null, values);
    }
    public Cursor query(String s) {
        SQLiteDatabase db = getWritableDatabase();
        Cursor c = db.rawQuery(s, null);
        return c;
    }
    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {

    }
}