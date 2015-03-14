package info.walsli.timestatistics;


import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;

public class DBManager {
    private DBHelper helper;
    private SQLiteDatabase db;

    public DBManager(Context context)
    {
        helper=new DBHelper(context);
        db=helper.getWritableDatabase();
    }
    public SQLiteDatabase getDB()
    {
        return db;
    }
    public void upgradeDB()
    {
        if(!isDBExists("timedetails"))
        {
            db.execSQL(ConstantField.CREATE_TABLE_TIMEDETAILS);
        }
    }
    public void insertIntoTimeDetails(int day,String packageName,int start,int end)
    {
        if(!isDBExists("detail"+day))
        {
            db.execSQL("create table detail"+day+"(_id integer primary key autoincrement,appname text,opentime integer,closetime integer)");
            insertIntoTimeDetails(day);
        }
        ContentValues values=new ContentValues();
        values.put("appname",packageName);
        values.put("opentime",start);
        values.put("closetime",end);
        db.insert("detail"+day,null,values);
    }
    public Cursor query(String s)
    {
        return db.rawQuery(s,null);
    }
    public boolean isDBExists(String mDBName)
    {
        Cursor c=db.rawQuery("select name from sqlite_master where type='table';",null);
        while(c.moveToNext())
        {
            if(c.getString(0).equals(mDBName))
            {
                c.close();
                return true;
            }
        }
        c.close();
        return false;
    }
    public void cleanDB()
    {
        Cursor c=db.rawQuery("select name from sqlite_master where type='table';", null);
        while(c.moveToNext())
        {
            db.execSQL("delete from "+c.getString(0));
        }
        c.close();
    }
    public void calculateTimeOfDays(int datenum)
    {
        Cursor temp=db.rawQuery("select * from timeofdays where datenum="+String.valueOf(datenum), null);
        if(temp.getCount()!=0)
        {
            db.execSQL("delete from timeofdays where datenum="+String.valueOf(datenum));
        }
        temp.close();
        Cursor c=db.rawQuery("select * from timeinfo where datenum="+String.valueOf(datenum), null);
        if(c.getCount()!=0)
        {
            int time=0;
            while(c.moveToNext())
            {
                time+=c.getInt(3)-c.getInt(2);
            }
            ContentValues values = new ContentValues();
            values.put("datenum", datenum);
            values.put("todaytime", time);
            db.insert("timeofdays", null, values);
        }
        c.close();

    }
    public void insertIntoTimeDetails(int day)
    {
        ContentValues values=new ContentValues();
        values.put("datenum",day);
        db.insert("timedetails",null,values);
    }
    public void insertIntoTimeinfo(int datenum,int opentime,int closetime) {
        ContentValues values = new ContentValues();
        values.put("datenum", datenum);
        values.put("opentime", opentime);
        values.put("closetime", closetime);
        db.insert("timeinfo", null, values);
    }
    public void closeDB()
    {
        if(null!=db)
        {
            db.close();
        }
    }
}
