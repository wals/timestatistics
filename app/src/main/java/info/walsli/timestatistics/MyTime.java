package info.walsli.timestatistics;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;

import android.app.Activity;
import android.content.SharedPreferences;
import android.database.Cursor;;

public class MyTime{
    private static SharedPreferences mySharedPreferences=MyApplication.getInstance().getSharedPreferences("info.walsli.timestatistics",Activity.MODE_MULTI_PROCESS);;
    private static SharedPreferences.Editor editor= mySharedPreferences.edit();
    private static DBHelper helper= new DBHelper(MyApplication.getInstance());

    public static DBHelper getDBHelper()
    {
        return helper;
    }
    public static void setBeginTime(String begintime)
    {
        editor.putString("begintime",begintime);
        editor.apply();
    }
    public static void increaseScreenonFrequency()
    {
        editor.putInt("screenon_frgequency",getScreenonFrequency()+1);
        editor.apply();
    }
    public static int getScreenonFrequency()
    {
        return mySharedPreferences.getInt("screenon_frequency",1);
    }
    public static long getTodaySeconds()
    {
        return mySharedPreferences.getLong("todayseconds",0);
    }

    public static int getDayNum(Date endDate) {
        Calendar fromCalendar = Calendar.getInstance();
        fromCalendar.set(Calendar.YEAR, 2014);
        fromCalendar.set(Calendar.MONTH, 6);
        fromCalendar.set(Calendar.DATE,15);
        fromCalendar.set(Calendar.HOUR_OF_DAY, 0);
        fromCalendar.set(Calendar.MINUTE, 0);
        fromCalendar.set(Calendar.SECOND, 0);
        fromCalendar.set(Calendar.MILLISECOND, 0);
        Calendar toCalendar = Calendar.getInstance();
        toCalendar.setTime(endDate);
        toCalendar.set(Calendar.HOUR_OF_DAY, 0);
        toCalendar.set(Calendar.MINUTE, 0);
        toCalendar.set(Calendar.SECOND, 0);
        toCalendar.set(Calendar.MILLISECOND, 0);
        return (int) ((toCalendar.getTime().getTime() - fromCalendar.getTime().getTime()) / (1000 * 60 * 60 * 24));
    }
    public static int getDayNum() {
        Calendar fromCalendar = Calendar.getInstance();
        fromCalendar.set(Calendar.YEAR, 2014);
        fromCalendar.set(Calendar.MONTH, 6);
        fromCalendar.set(Calendar.DAY_OF_MONTH, 15);
        fromCalendar.set(Calendar.HOUR_OF_DAY, 0);
        fromCalendar.set(Calendar.MINUTE, 0);
        fromCalendar.set(Calendar.SECOND, 0);
        fromCalendar.set(Calendar.MILLISECOND, 0);
        Calendar toCalendar = Calendar.getInstance();
        toCalendar.set(Calendar.HOUR_OF_DAY, 0);
        toCalendar.set(Calendar.MINUTE, 0);
        toCalendar.set(Calendar.SECOND, 0);
        toCalendar.set(Calendar.MILLISECOND, 0);
        return (int) ((toCalendar.getTime().getTime() - fromCalendar.getTime().getTime()) / (1000 * 60 * 60 * 24));
    }
    private static boolean checkdb()
    {
        Cursor c=helper.query("select name from sqlite_master where type='table';");
        while(c.moveToNext())
        {
            if(c.getString(0).equals("timeofapps"))
            {
                return true;
            }
        }
        return false;
    }
    private static void aNewDay(int date)
    {
        editor.putInt("screenon_frequency",1);
        editor.putBoolean("todayremind", false);
        editor.commit();
        if(!checkdb())
        {
            helper.exec("create table timeofapps(_id integer primary key autoincrement,datenum integer,appname text,appseconds integer)");
        }
        HashMap<String, Integer> appSecondsPerDay = MyApplication.getAppSecondsPerDay();
        Iterator iter = appSecondsPerDay.entrySet().iterator();
        while (iter.hasNext()) {
            Map.Entry entry = (Map.Entry) iter.next();
            String key = (String) entry.getKey();
            int val = (int) entry.getValue();
            helper.insertIntoTimeOfApps(date,key,val);
        }
        MyApplication.clearAppSecondsPerDay();
    }
    public static boolean processTime(String end)
    {
        String begin = mySharedPreferences.getString("begintime", "");
        if (!begin.equals(""))
        {
            SimpleDateFormat dfs = new SimpleDateFormat("yyyy:MM:dd:HH:mm:ss");
            try
            {
                java.util.Date begins = dfs.parse(begin);
                java.util.Date ends = dfs.parse(end);
                if(((ends.getTime() - begins.getTime()) / 1000)>86400)
                {
                    editor.putString("begintime", "");
                    editor.commit();
                    return false;
                }
                if((begins.getYear()<114)||(ends.getYear()<114))
                {
                    editor.putString("begintime", "");
                    editor.commit();
                    return false;
                }
                if(ends.getTime() - begins.getTime()>=0)
                {
                    if (begins.getDate() != ends.getDate())
                    {
                        helper.insertIntoTimeinfo(getDayNum(begins), begins.getHours()*3600+begins.getMinutes()*60+begins.getSeconds(),86399);
                        helper.insertIntoTimeinfo(getDayNum(ends),0, ends.getHours()*3600+ends.getMinutes()*60+ends.getSeconds());
                        helper.settlement(getDayNum(begins));
                        long todayseconds = ends.getHours() * 3600 + ends.getMinutes()* 60 + ends.getSeconds();
                        editor.putLong("todayseconds",todayseconds);
                        long allseconds = mySharedPreferences.getLong("allseconds",0);
                        allseconds+= (ends.getTime() - begins.getTime()) / 1000;
                        editor.putLong("allseconds", allseconds);
                        editor.putInt("date",ends.getDate());
                        editor.putString("begintime", "");
                        editor.commit();
                        aNewDay(ends.getDate()-1);
                    }
                    else
                    {
                        helper.insertIntoTimeinfo(getDayNum(ends), begins.getHours()*3600+begins.getMinutes()*60+begins.getSeconds(), ends.getHours()*3600+ends.getMinutes()*60+ends.getSeconds());
                        int date=mySharedPreferences.getInt("date",0);
                        long timepass=ends.getHours()*3600+ends.getMinutes()*60+ends.getSeconds()-begins.getHours()*3600-begins.getMinutes()*60-begins.getSeconds();
                        if(date!=ends.getDate())
                        {
                            editor.putLong("todayseconds",timepass);
                            editor.putInt("date",ends.getDate());
                            helper.settlement(getDayNum(begins)-1);
                            aNewDay(ends.getDate()-1);
                        }
                        else
                        {
                            long todayseconds= mySharedPreferences.getLong("todayseconds",0);
                            todayseconds+=timepass;
                            editor.putLong("todayseconds",todayseconds);
                        }
                        long allseconds = mySharedPreferences.getLong("allseconds",0);
                        allseconds +=timepass;
                        editor.putLong("allseconds", allseconds);
                        editor.putString("begintime", "");
                        editor.commit();

                    }
                }
            }
            catch (ParseException e)
            {
                e.printStackTrace();
            }
        }
        editor.putString("begintime", "");
        editor.commit();
        return true;
    }

}