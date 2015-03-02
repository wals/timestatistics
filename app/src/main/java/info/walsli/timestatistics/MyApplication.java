package info.walsli.timestatistics;

import java.util.HashMap;

import android.app.Activity;
import android.app.Application;
import android.content.SharedPreferences;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Typeface;
import android.graphics.drawable.BitmapDrawable;
import android.graphics.drawable.Drawable;

public class MyApplication extends Application
{
    private static MyApplication instance;
    private static Typeface typeface;
    private static Bitmap mBgBitmap = null;
    private static SharedPreferences mySharedPreferences;
    private static SharedPreferences.Editor editor;
    private static HashMap<String , Integer> appSecondsPerDay = new HashMap<String , Integer>();
    private static DBHelper dbhelper;

    @Override
    public void onCreate()
    {
        super.onCreate();
        instance = this;
        typeface=Typeface.createFromAsset(this.getAssets(),"font.ttf");
        mBgBitmap = BitmapFactory.decodeResource(this.getResources(), R.drawable.backgroundsmall);
        mySharedPreferences =getSharedPreferences("info.walsli.timestatistics",Activity.MODE_MULTI_PROCESS);
        editor = mySharedPreferences.edit();
        dbhelper=new DBHelper(this);
    }
    public static DBHelper getDBHelper()
    {
        return dbhelper;
    }
    public static HashMap<String, Integer> getAppSecondsPerDay()
    {
        return appSecondsPerDay;
    }
    public static void setModel(int i)
    {
        editor.putInt("model", i);
        editor.apply();
    }

    public static MyApplication getInstance() {
        return instance;
    }

    public static Drawable getdrawable()
    {
        return new BitmapDrawable(mBgBitmap);
    }

    public static void clearAppSecondsPerDay()
    {
        appSecondsPerDay.clear();
    }
    @Override
    public void onTerminate()
    {
        super.onTerminate();
        if(mBgBitmap != null  && !mBgBitmap.isRecycled())
        {
            mBgBitmap.recycle();
            mBgBitmap = null;
        }
    }

    public static Typeface gettypeface()
    {
        return typeface;
    }
}