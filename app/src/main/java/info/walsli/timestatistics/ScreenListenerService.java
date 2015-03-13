package info.walsli.timestatistics;

import android.app.ActivityManager;
import android.app.Notification;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.app.Service;
import android.content.BroadcastReceiver;
import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.os.IBinder;
import android.os.PowerManager;
import android.text.format.Time;

import java.util.HashMap;

public class ScreenListenerService extends Service {
    private boolean isScreenOn =false;
    private HashMap<String, Integer> appSecondsPerDay = MyApplication.getAppSecondsPerDay();
    @Override
    public IBinder onBind(Intent intent) {
        return null;
    }
    @Override
    public void onCreate()
    {
        super.onCreate();
        final IntentFilter filter = new IntentFilter();
        filter.addAction(Intent.ACTION_SCREEN_OFF);
        filter.addAction(Intent.ACTION_SCREEN_ON);
        filter.addAction(Intent.ACTION_BOOT_COMPLETED);
        filter.addAction(Intent.ACTION_SHUTDOWN);
        filter.addCategory(Intent.ACTION_DATE_CHANGED);
        filter.addAction(ConstantField.NEW_WIDGET);
        registerReceiver(receiver, filter);
        PowerManager powerManager = (PowerManager) getSystemService(Context.POWER_SERVICE);
        isScreenOn =powerManager.isScreenOn();
        Thread thread=new Thread(new ProgressRunable());
        thread.start();

        Time t=new Time();
        t.setToNow();
        long l=t.toMillis(false)/1000;
        MyUtils.setBeginTime(l);
    }
    @Override
    public int onStartCommand(Intent intent, int flags, int startID) {

        return START_STICKY;
    }
    @Override
    public void onDestroy()
    {
        unregisterReceiver(receiver);
        super.onDestroy();
    }

    class ProgressRunable implements Runnable
    {
        public void putSecondsIntoHashmap()
        {
            ActivityManager am = (ActivityManager) getSystemService(ACTIVITY_SERVICE);
            ComponentName cn = am.getRunningTasks(1).get(0).topActivity;
            String packageName = cn.getPackageName();
            if(appSecondsPerDay.containsKey(packageName))
            {
                appSecondsPerDay.put(packageName, appSecondsPerDay.get(packageName)+1);
            }
            else
            {
                appSecondsPerDay.put(packageName,1);
            }
        }

        @Override
        public void run() {
            long todayseconds=MyUtils.getTodaySeconds();
            long countdownnum=60-todayseconds%60;
            long seconds=todayseconds/60;
            updatewidget(seconds-1);
            while(isScreenOn &&(countdownnum>0))
            {
                try
                {
                    Thread.sleep(1000);
                    countdownnum--;
                    putSecondsIntoHashmap();
                }
                catch (InterruptedException e)
                {
                    e.printStackTrace();
                }

            }
            if(isScreenOn)
            {
                updatewidget(seconds);
            }
            countdownnum=59;
            while(isScreenOn)
            {
                try
                {
                    Thread.sleep(1000);
                    countdownnum--;
                    putSecondsIntoHashmap();
                }
                catch (InterruptedException e)
                {
                    e.printStackTrace();
                }
                if(countdownnum==0)
                {
                    updatewidget(++seconds);
                    countdownnum=59;
                }
            }
        }

    }
    public void updatewidget(long minutes) {
        //TODO
    }

    public void callcountdown(long mins)
    {
        MyUtils.setTodayRemind(true);
        String ns = Context.NOTIFICATION_SERVICE;
        NotificationManager mNotificationManager = (NotificationManager)getSystemService(ns);
        CharSequence contentText = "您今天已经使用手机"+String.valueOf(mins)+"分钟了，请放下手机休息一下吧"; //通知栏内容
        long when = System.currentTimeMillis();
        int icon = R.drawable.sb;
        Notification notification = new Notification(icon,contentText,when);
        notification.defaults |= Notification.DEFAULT_VIBRATE;
        Context context = getApplicationContext(); //上下文
        CharSequence contentTitle = "魅时间提醒"; //通知栏标题
        Intent notificationIntent = new Intent(this,MainActivity.class); //点击该通知后要跳转的Activity
        PendingIntent contentIntent = PendingIntent.getActivity(this,0,notificationIntent,0);
        notification.setLatestEventInfo(context, contentTitle, contentText, contentIntent);
        mNotificationManager.notify(0,notification);
    }
    public BroadcastReceiver receiver = new BroadcastReceiver()
    {
        @Override
        public void onReceive(final Context context, final Intent intent)
        {
            Time t=new Time();
            t.setToNow();
            long l=t.toMillis(false)/1000;
            if (Intent.ACTION_SCREEN_ON.equals(intent.getAction()))
            {
                MyUtils.setBeginTime(l);
                MyUtils.increaseScreenOnFrequency();
                isScreenOn =true;
                Thread thread=new Thread(new ProgressRunable());
                thread.start();
            }
            else if (Intent.ACTION_SCREEN_OFF.equals(intent.getAction()))
            {
                MyUtils.processTime(l);
                isScreenOn =false;
            }
            else if (Intent.ACTION_SHUTDOWN.equals(intent.getAction()))
            {
                MyUtils.processTime(l);
                isScreenOn =false;
            }
            else if (Intent.ACTION_DATE_CHANGED.equals(intent.getAction()))
            {
                if(isScreenOn)
                {
                    MyUtils.processTime(l);
                    MyUtils.setBeginTime(l);
                    //updatewidget(MyTime.getTodaySeconds()/60-1);//TODO
                }
            }
            else if ("info.walsli.timestatistics.NEW_WIDGET".equals(intent.getAction()))
            {
                //TODO
            }
        }
    };

}
