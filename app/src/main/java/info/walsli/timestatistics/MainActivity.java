package info.walsli.timestatistics;

import android.app.ActionBar;
import android.app.Activity;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.view.Window;

import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.text.SimpleDateFormat;
import java.util.Calendar;

public class MainActivity extends Activity {
    private boolean inFornt=false;
    private ClockView clockview=null;
    private Thread thread;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        getWindow().requestFeature(Window.FEATURE_ACTION_BAR_OVERLAY);
        setContentView(R.layout.activity_main);
        findViewById(R.id.container).setBackgroundDrawable(MyApplication.getdrawable());
        //findViewById(R.id.container).setBackgroundColor(Color.parseColor("#E2EDC9"));
        fucksmartbar();

        if(!MyLogic.isSharedPreferencesInit())
        {
            MyLogic.initSharedPreferences();
            startActivity(new Intent(this,GuideActivity.class));
        }
        MyLogic.setReboot(true);
        if(!MyLogic.isModelDetermined())
        {
            if(getWindowManager().getDefaultDisplay().getWidth()==800)
            {
                MyApplication.setModel(2);
            }
            else
            {
                MyApplication.setModel(3);
            }
        }

        clockInit();

        if(!MyLogic.isServiceWorked(this))
        {
            Intent serviceIntent = new Intent(this, ScreenListenerService.class);
            this.startService(serviceIntent);
        }

        IntentFilter filter = new IntentFilter();
        filter.addAction("info.walsli.timestatistics.MainActivityFinishReceiver");
        filter.addAction("info.walsli.timestatistics.MainActivityRestartReceiver");
        registerReceiver(receiver, filter);

        Intent intent = new Intent();
        intent.setAction("info.walsli.timestatistics.BlankActivityFinishReceiver");
        this.sendBroadcast(intent);

        loveformonica();
    }
    private void loveformonica()
    {


    }

    @Override
    protected void onDestroy()
    {
        unregisterReceiver(receiver);
        super.onDestroy();
    }



    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        MenuItem mnu1=menu.add(0,0,0,"统计");
        mnu1.setIcon(R.drawable.statistics);
        mnu1.setShowAsAction(MenuItem.SHOW_AS_ACTION_ALWAYS);
        MenuItem mnu2=menu.add(1,1,1,"设置");
        mnu2.setIcon(R.drawable.setting);
        mnu2.setShowAsAction(MenuItem.SHOW_AS_ACTION_ALWAYS);
        return true;
    }

    @Override
    public void onResume()
    {
        if(!DBHelper.lock)
        {
            SimpleDateFormat sDateFormat = new SimpleDateFormat("yyyy:MM:dd:HH:mm:ss");
            String date = sDateFormat.format(new java.util.Date());
            MyTime.processTime(date);
            MyTime.setBeginTime(date);
            inFornt=true;
            thread=new Thread(new ProgressRunable());
            thread.start();
        }
        else
        {
            clockview.refreshclock(19212,0,-1);
        }
        super.onResume();
    }

    @Override
    public void onPause()
    {
        inFornt=false;
        super.onPause();
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch(item.getItemId())
        {
            case 0:
                startActivity(new Intent(this,StatisticsActivity.class));
                return true;
            case 1:
                startActivity(new Intent(this,SettingActivity.class));
                return true;
        }
        return super.onOptionsItemSelected(item);
    }

    private void clockInit()
    {
        this.clockview=new ClockView(this,MyTime.getTodaySeconds());
        clockview.invalidate();
        clockview.layout(0, 0, 0, 0);
        ActionBar.LayoutParams lp=new ActionBar.LayoutParams(0);
        this.addContentView(clockview, lp);
    }

    private void fucksmartbar()
    {
        try {
            Method method = Class.forName("android.app.ActionBar").getMethod(
                    "setActionBarViewCollapsable", new Class[] { boolean.class });
            try {
                method.invoke(getActionBar(), true);
            } catch (IllegalArgumentException e) {
                e.printStackTrace();
            } catch (IllegalAccessException e) {
                e.printStackTrace();
            } catch (InvocationTargetException e) {
                e.printStackTrace();
            }
        } catch (SecurityException e) {
            e.printStackTrace();
        } catch (NoSuchMethodException e) {
            e.printStackTrace();
        } catch (ClassNotFoundException e) {
            e.printStackTrace();
        }
        getActionBar().setDisplayOptions(0);
    }



    class ProgressRunable implements Runnable
    {
        @Override
        public void run() {
            long seconds=MyTime.getTodaySeconds();
            int screenon_frequency=MyTime.getScreenonFrequency();
            Calendar c = Calendar.getInstance();
            int hour=c.get(Calendar.HOUR_OF_DAY);
            while(inFornt)
            {
                clockview.refreshclock(seconds,screenon_frequency,hour);
                try {
                    Thread.sleep(1000);
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
                seconds++;
            }
        }
    }



    public BroadcastReceiver receiver = new BroadcastReceiver()
    {
        @Override
        public void onReceive(final Context context, final Intent intent)
        {
            if ("info.walsli.timestatistics.MainActivityFinishReceiver".equals(intent.getAction()))
            {
                MainActivity.this.finish();
            }
            else if ("info.walsli.timestatistics.MainActivityRestartReceiver".equals(intent.getAction()))
            {
                MainActivity.this.recreate();
            }
        }
    };
}
