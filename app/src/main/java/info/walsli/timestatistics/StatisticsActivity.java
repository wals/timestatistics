package info.walsli.timestatistics;

import android.app.ActionBar.LayoutParams;
import android.app.Activity;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.SharedPreferences;
import android.database.Cursor;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.view.Window;

import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.util.Calendar;

public class StatisticsActivity extends Activity {
    StatisticsView canvasview;
    DBHelper helper;
    SharedPreferences mySharedPreferences;
    SharedPreferences.Editor editor;
    IntentFilter filter;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        getWindow().requestFeature(Window.FEATURE_ACTION_BAR_OVERLAY);
        setContentView(R.layout.activity_statistics);
        findViewById(R.id.container).setBackgroundDrawable(MyApplication.getdrawable());
        helper = MyApplication.getDBHelper();
        viewinit();
        fucksmartbar();

        IntentFilter filter = new IntentFilter();
        filter.addAction("info.walsli.timestatistics.StatisticsActivityFinishReceiver");
        filter.addAction("info.walsli.timestatistics.StatisticsActivityRestartReceiver");
        registerReceiver(receiver, filter);

        mySharedPreferences =getSharedPreferences("info.walsli.timestatistics",Activity.MODE_MULTI_PROCESS);
        editor = mySharedPreferences.edit();

    }
    @Override
    protected void onDestroy()
    {
        System.gc();
        helper.close();
        unregisterReceiver(receiver);
        super.onDestroy();
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
        //Log.e("walsli","invoke");
    }
    public int getGapCount() {
        Calendar fromCalendar = Calendar.getInstance();
        fromCalendar.set(Calendar.YEAR, 2014);
        fromCalendar.set(Calendar.MONTH, 6);
        fromCalendar.set(Calendar.DATE,15);
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
    private void viewinit()
    {
        if(DBHelper.lock)
        {
            ClockView clockView=new ClockView(this,19200);
            clockView.invalidate();
            clockView.layout(0, 0, 0, 0);
            LayoutParams lp=new LayoutParams(0);
            this.addContentView(clockView, lp);
            clockView.refreshclock(19212,0,-1);
        }
        else
        {
            helper.settlement(getGapCount());

            Cursor c=helper.query("select * from timeofdays order by datenum asc;");
            int a[][]=new int[c.getCount()][2];
            int i=0;
            while(c.moveToNext())
            {
                a[i][0]=c.getInt(1);
                a[i][1]=c.getInt(2);
                i++;
            }
            int b[][]=new int[10][2];
            if(c.getCount()<10)
            {
                int startmark=a[0][0]-10+c.getCount();
                for(int k=0;k<10-c.getCount();k++)
                {
                    b[k][0]=startmark;
                    b[k][1]=0;
                    startmark++;
                }
                for(int k=10-c.getCount();k<10;k++)
                {
                    b[k][0]=a[k+c.getCount()-10][0];
                    b[k][1]=a[k+c.getCount()-10][1];
                }
            }
            else if(c.getCount()==10)
            {
                for(int k=0;k<10;k++)
                {
                    b[k][0]=a[k][0];
                    b[k][1]=a[k][1];
                }
            }
            else if(c.getCount()>10)
            {
                for(int k=0;k<10;k++)
                {
                    b[k][0]=a[c.getCount()-10+k][0];
                    b[k][1]=a[c.getCount()-10+k][1];
                }
            }
            this.canvasview=new StatisticsView(this,b);
            canvasview.invalidate();
            canvasview.layout(0, 0, 0, 0);
            LayoutParams lp=new LayoutParams(0);
            this.addContentView(canvasview, lp);
        }


    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {

        MenuItem mnu1=menu.add(0,0,0,"统计");
        {

            mnu1.setIcon(R.drawable.statistics);
            mnu1.setShowAsAction(MenuItem.SHOW_AS_ACTION_ALWAYS);
        }
        MenuItem mnu2=menu.add(1,1,1,"设置");
        {
            mnu2.setIcon(R.drawable.setting);
            mnu2.setShowAsAction(MenuItem.SHOW_AS_ACTION_ALWAYS);
        }
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch(item.getItemId())
        {
            case 1:
                startActivity(new Intent(this,SettingActivity.class));
                return true;
        }
        return super.onOptionsItemSelected(item);
    }


    public BroadcastReceiver receiver = new BroadcastReceiver()
    {
        @Override
        public void onReceive(final Context context, final Intent intent)
        {
            if ("info.walsli.timestatistics.StatisticsFinishReceiver".equals(intent.getAction()))
            {
                StatisticsActivity.this.finish();
            }
            else if ("info.walsli.timestatistics.StatisticsActivityRestartReceiver".equals(intent.getAction()))
            {
                StatisticsActivity.this.recreate();
            }
        }
    };

}
