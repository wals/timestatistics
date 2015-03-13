package info.walsli.timestatistics;

import android.app.Activity;
import android.app.Application;
import android.content.SharedPreferences;
import android.graphics.Typeface;

import java.util.HashMap;

public class MyApplication extends Application
{
    private static MyApplication instance=null;
    private static Typeface typeface;
    private static SharedPreferences mySharedPreferences;
    private static SharedPreferences.Editor editor;
    private static HashMap<String , Integer> appSecondsPerDay = new HashMap<String , Integer>();
    public static MyApplication getinstance()
    {
        return instance;
    }
    @Override
    public void onCreate()
    {
        super.onCreate();
        instance=this;
        typeface=Typeface.createFromAsset(this.getAssets(),"font.ttf");
        mySharedPreferences =getSharedPreferences(ConstantField.PACKAGE_NAME,Activity.MODE_MULTI_PROCESS);
        editor = mySharedPreferences.edit();
    }
    public static HashMap<String, Integer> getAppSecondsPerDay()
    {
        return appSecondsPerDay;
    }

    public static MyApplication getInstance() {
        return instance;
    }

    public static void clearAppSecondsPerDay()
    {
        appSecondsPerDay.clear();
    }
    @Override
    public void onTerminate()
    {
        super.onTerminate();
    }

    public static Typeface gettypeface()
    {
        return typeface;
    }
}