package info.walsli.timestatistics;

import android.app.Activity;
import android.app.ActivityManager;
import android.content.Context;
import android.content.SharedPreferences;
import android.util.Log;

import java.text.SimpleDateFormat;
import java.util.ArrayList;

public class MyLogic{
    private static SharedPreferences mySharedPreferences=MyApplication.getInstance().getSharedPreferences("info.walsli.timestatistics",Activity.MODE_MULTI_PROCESS);
    private static SharedPreferences.Editor editor =mySharedPreferences.edit();
    private static String TAG="walsli";

    public static String BLANKACTIVITY_FINISH="info.walsli.timestatistics.BlankActivityFinishReceiver";
    public static String packageName="info.walsli.timestatistics";

    public static void log(String s)
    {
        Log.e(TAG,s);
    }
    public static boolean isCallCountDown(long minutes)
    {
        return mySharedPreferences.getBoolean("iscountdown",false)&&!mySharedPreferences.getBoolean("todayremind",false)&&Long.parseLong(mySharedPreferences.getString("countdownnum","0"))<=minutes;
    }

    public static boolean isServiceWorked(Activity activity)
    {
        ActivityManager myManager=(ActivityManager)activity.getSystemService(Context.ACTIVITY_SERVICE);
        ArrayList<ActivityManager.RunningServiceInfo> runningService = (ArrayList<ActivityManager.RunningServiceInfo>) myManager.getRunningServices(100);
        for(int i = 0 ; i<runningService.size();i++)
        {
            if(runningService.get(i).service.getClassName().toString().equals("info.walsli.timestatistics.ScreenListenerService"))
            {
                return true;
            }
        }
        return false;
    }
    public static boolean isModelDetermined()
    {
        if(mySharedPreferences.getInt("model",0)==0)
        {
            return false;
        }
        else
        {
            return true;
        }
    }

    public static boolean isSharedPreferencesInit()
    {
        return !mySharedPreferences.getBoolean("init",true);
    }

    public static void setTodayRemind(boolean b)
    {
        editor.putBoolean("todayremind", b);
        editor.apply();
    }

    public static void setReboot(boolean whetherreboot)
    {
        editor.putBoolean("reboot",whetherreboot);
        editor.apply();
    }
    public static int getModel()
    {
        return mySharedPreferences.getInt("model",3);
    }
    public static void initSharedPreferences()
    {
        SimpleDateFormat sDateFormat = new SimpleDateFormat("yyyy:MM:dd:HH:mm:ss");
        String date = sDateFormat.format(new java.util.Date());
        editor.putString("begintime",date);
        editor.putLong("todayseconds",0);
        editor.putLong("allseconds",0);
        sDateFormat = new SimpleDateFormat("dd");
        date = sDateFormat.format(new java.util.Date());
        editor.putInt("date",Integer.parseInt(date));
        editor.putBoolean("init", false);
        editor.putBoolean("todayremind", false);
        editor.apply();
    }
}