package info.walsli.timestatistics;

import android.app.Activity;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.os.Bundle;
import android.text.format.Time;
import android.view.Menu;
import android.view.MenuItem;
import android.view.Window;

import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.util.Calendar;

public class MainActivity extends Activity {
    private boolean inFront =false;
    private MainView mMainView =null;
    private Thread mThread;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        getWindow().requestFeature(Window.FEATURE_ACTION_BAR_OVERLAY);
        setContentView(R.layout.activity_main);
        findViewById(R.id.activity_main).setBackgroundResource(R.drawable.defaultbackground);

        mMainView =(MainView)findViewById(R.id.clockview);
        fucksmartbar();
        logicOperatings();
        intentOperatings();

    }
    private void intentOperatings()
    {
        IntentFilter filter = new IntentFilter();
        filter.addAction(ConstantField.MAINACTIVITY_FINISH);
        filter.addAction(ConstantField.MAINACTIVITY_RESTART);
        registerReceiver(receiver, filter);

        Intent intent = new Intent();
        intent.setAction(ConstantField.BLANKACTIVITY_FINISH);
        this.sendBroadcast(intent);
    }
    private void logicOperatings()
    {
        MyUtils.initSharedPreferences();
        MyUtils.setReboot(true);
        if(!MyUtils.isServiceWorked(this))
        {
            Intent serviceIntent = new Intent(this, ScreenListenerService.class);
            this.startService(serviceIntent);
        }
        DBManager mDBManager=new DBManager(MyApplication.getInstance());
        mDBManager.upgradeDB();
        mDBManager.closeDB();
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
            Time t=new Time();
            t.setToNow();
            MyUtils.processTime(t.toMillis(false)/1000);
            MyUtils.setBeginTime(t.toMillis(false)/1000);
            inFront =true;
            mThread =new Thread(new ProgressRunable());
            mThread.start();
        }
        else
        {
            mMainView.setTime(19212, 0, -1);
        }
        super.onResume();
    }

    @Override
    public void onPause()
    {
        inFront =false;
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
            long seconds=MyUtils.getTodaySeconds();
            int screenon_frequency=MyUtils.getScreenOnFrequency();
            Calendar c = Calendar.getInstance();
            int hour=c.get(Calendar.HOUR_OF_DAY);
            while(inFront)
            {
                mMainView.setTime(seconds, screenon_frequency, hour);
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
            if (ConstantField.MAINACTIVITY_FINISH.equals(intent.getAction()))
            {
                MainActivity.this.finish();
            }
            else if (ConstantField.MAINACTIVITY_RESTART.equals(intent.getAction()))
            {
                MainActivity.this.recreate();
            }
        }
    };
}
