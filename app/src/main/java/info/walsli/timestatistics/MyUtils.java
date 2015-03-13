package info.walsli.timestatistics;

import android.app.Activity;
import android.app.ActivityManager;
import android.content.Context;
import android.content.SharedPreferences;
import android.text.format.Time;

import java.util.ArrayList;
import java.util.Calendar;

public class MyUtils {
    private static SharedPreferences mySharedPreferences=MyApplication.getInstance().getSharedPreferences(ConstantField.PACKAGE_NAME, Activity.MODE_MULTI_PROCESS);
    private static SharedPreferences.Editor editor =mySharedPreferences.edit();

    public static void initSharedPreferences()
    {
        if(mySharedPreferences.getBoolean(ConstantField.SPITEM_INIT,true))
        {
            Time t=new Time();
            t.setToNow();
            editor.putLong(ConstantField.SPITEM_BEGINTIME, t.toMillis(false) / 1000);
            editor.putLong(ConstantField.SPITEM_TODAYSECONDS,0);
            editor.putLong(ConstantField.SPITEM_ALLSECONDS,0);
            editor.putInt(ConstantField.SPITEM_DATE, getDaysFrom20140715());
            editor.putBoolean(ConstantField.SPITEM_INIT, false);
            editor.putBoolean(ConstantField.SPITEM_TODAYREMIND, false);
            editor.apply();
        }
    }

    public static boolean isServiceWorked(Activity activity)
    {
        ActivityManager myManager=(ActivityManager)activity.getSystemService(Context.ACTIVITY_SERVICE);
        ArrayList<ActivityManager.RunningServiceInfo> runningService = (ArrayList<ActivityManager.RunningServiceInfo>) myManager.getRunningServices(100);
        for(int i = 0 ; i<runningService.size();i++)
        {
            if(runningService.get(i).service.getClassName().toString().equals(ConstantField.SERVICE_NAME))
            {
                return true;
            }
        }
        return false;
    }
    public static void setReboot(boolean whetherreboot)//当参数为true时候应用会开机自启动
    {
        editor.putBoolean(ConstantField.SPITEM_REBOOT,whetherreboot);
        editor.commit();
    }
    public static int getDaysFrom20140715()
    {
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
    public static int getDaysFromTime(long time)
    {
        Calendar fromCalendar = Calendar.getInstance();
        fromCalendar.set(Calendar.YEAR, 2014);
        fromCalendar.set(Calendar.MONTH, 6);
        fromCalendar.set(Calendar.DATE,15);
        fromCalendar.set(Calendar.HOUR_OF_DAY, 0);
        fromCalendar.set(Calendar.MINUTE, 0);
        fromCalendar.set(Calendar.SECOND, 0);
        fromCalendar.set(Calendar.MILLISECOND, 0);
        Calendar toCalendar = Calendar.getInstance();
        toCalendar.setTimeInMillis(time*1000);
        toCalendar.set(Calendar.HOUR_OF_DAY, 0);
        toCalendar.set(Calendar.MINUTE, 0);
        toCalendar.set(Calendar.SECOND, 0);
        toCalendar.set(Calendar.MILLISECOND, 0);
        return (int) ((toCalendar.getTime().getTime() - fromCalendar.getTime().getTime()) / (1000 * 60 * 60 * 24));
    }
    public static void setTodayRemind(boolean todayRemind)
    {
        editor.putBoolean(ConstantField.SPITEM_TODAYREMIND,todayRemind);
        editor.commit();
    }
    public static int getScreenOnFrequency()
    {
        return mySharedPreferences.getInt(ConstantField.SPITEM_SCREENONFREQUENCY,0);
    }
    public static void increaseScreenOnFrequency()
    {
        editor.putInt(ConstantField.SPITEM_SCREENONFREQUENCY,getScreenOnFrequency()+1);
        editor.commit();
    }
    public static long getTodaySeconds()
    {
        return mySharedPreferences.getLong(ConstantField.SPITEM_TODAYSECONDS,0);
    }
    public static void setBeginTime(long begintime)
    {
        editor.putLong(ConstantField.SPITEM_BEGINTIME,begintime);
        editor.commit();
    }
    public static int processTime(long end)
    {
        
        long begin=mySharedPreferences.getLong(ConstantField.SPITEM_BEGINTIME,0);
        if(begin==0)
        {
            return -1;
        }
        if(end-begin>86400)
        {
            editor.putLong(ConstantField.SPITEM_BEGINTIME,0);
            editor.commit();
            return -2;
        }
        if(begin<1420041600||end<1420041600)//1420041600means20150101
        {
            editor.putLong(ConstantField.SPITEM_BEGINTIME,0);
            editor.commit();
            return -3;
        }
        if(end-begin<0)
        {
            editor.putLong(ConstantField.SPITEM_BEGINTIME,0);
            editor.commit();
            return -4;
        }
        DBManager dbManager=new DBManager(MyApplication.getInstance());
        Time beginTime=new Time();
        beginTime.set(1000*begin);
        Time endTime=new Time();
        endTime.set(1000*end);
        int beginDate=getDaysFromTime(begin);
        if(beginTime.monthDay!=endTime.monthDay)
        {
            int todayseconds=endTime.hour*3600+endTime.minute*60+endTime.second;
            dbManager.insertIntoTimeinfo(beginDate,beginTime.hour*3600+beginTime.minute*60+beginTime.second,86399);
            dbManager.insertIntoTimeinfo(beginDate+1,0,todayseconds);
            dbManager.calculateTimeOfDays(beginDate);
            editor.putLong(ConstantField.SPITEM_TODAYSECONDS,todayseconds);
            editor.putLong(ConstantField.SPITEM_ALLSECONDS,mySharedPreferences.getLong(ConstantField.SPITEM_ALLSECONDS,0)+end-begin);
            editor.putInt(ConstantField.SPITEM_DATE,beginDate+1);
            editor.putInt(ConstantField.SPITEM_SCREENONFREQUENCY,1);
            editor.putBoolean(ConstantField.SPITEM_TODAYREMIND,false);
        }
        else
        {
            dbManager.insertIntoTimeinfo(beginDate,beginTime.hour*3600+beginTime.minute*60+beginTime.second,endTime.hour*3600+endTime.minute*60+endTime.second);
            long timePass=end-begin;
            if(mySharedPreferences.getInt(ConstantField.SPITEM_DATE,0)!=beginDate)
            {
                dbManager.calculateTimeOfDays(mySharedPreferences.getInt(ConstantField.SPITEM_DATE,0));
                editor.putLong(ConstantField.SPITEM_TODAYSECONDS,timePass);
                editor.putInt(ConstantField.SPITEM_DATE,beginDate);
                editor.putInt(ConstantField.SPITEM_SCREENONFREQUENCY,1);
                editor.putBoolean(ConstantField.SPITEM_TODAYREMIND,false);
            }
            else
            {
                editor.putLong(ConstantField.SPITEM_TODAYSECONDS,mySharedPreferences.getLong(ConstantField.SPITEM_TODAYSECONDS,0)+timePass);
            }
            editor.putLong(ConstantField.SPITEM_ALLSECONDS,mySharedPreferences.getLong(ConstantField.SPITEM_ALLSECONDS,0)+timePass);
        }
        editor.putLong(ConstantField.SPITEM_BEGINTIME,0);
        editor.commit();
        return 0;
    }
}
