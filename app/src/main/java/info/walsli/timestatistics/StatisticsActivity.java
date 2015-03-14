package info.walsli.timestatistics;

import android.app.Activity;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.view.Window;

import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;

public class StatisticsActivity extends Activity {
    SharedPreferences mySharedPreferences;
    SharedPreferences.Editor editor;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        getWindow().requestFeature(Window.FEATURE_ACTION_BAR_OVERLAY);
        setContentView(R.layout.activity_statistics);
        findViewById(R.id.activity_statistics).setBackgroundResource(R.drawable.defaultbackground);
        fucksmartbar();

        IntentFilter filter = new IntentFilter();
        filter.addAction(ConstantField.STATISTICSACTIVITY_FINISH);
        filter.addAction(ConstantField.STATISTICSACTIVITY_RESTART);
        registerReceiver(receiver, filter);

        mySharedPreferences =getSharedPreferences(ConstantField.PACKAGE_NAME,Activity.MODE_MULTI_PROCESS);
        editor = mySharedPreferences.edit();

    }
    @Override
    protected void onDestroy()
    {
        System.gc();
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
            if (ConstantField.STATISTICSACTIVITY_FINISH.equals(intent.getAction()))
            {
                StatisticsActivity.this.finish();
            }
            else if (ConstantField.STATISTICSACTIVITY_RESTART.equals(intent.getAction()))
            {
                StatisticsActivity.this.recreate();
            }
        }
    };

}
