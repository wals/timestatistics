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
import android.util.Log;

public class ScreenListenerService extends Service {
    private boolean isScreenOn =false;
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

        private String getUsedPackageName()
        {
            ActivityManager am = (ActivityManager) getSystemService(ACTIVITY_SERVICE);
            ComponentName cn = am.getRunningTasks(1).get(0).topActivity;
            return cn.getPackageName();
        }
        private void insertPackageNameIntoDB(String packageName,long start,long end)
        {
            Time t1=new Time();
            t1.set(start*1000);
            Time t2=new Time();
            t2.set(end*1000);
            int startDay=MyUtils.getDaysFromTime(start);
            int endDay=MyUtils.getDaysFromTime(end);
            DBManager mDBManager=new DBManager(MyApplication.getInstance());
            if(startDay!=endDay)
            {
                mDBManager.insertIntoTimeDetails(startDay,packageName,t1.hour*3600+t1.minute*60+t1.second,86439);
                mDBManager.insertIntoTimeDetails(endDay,packageName,0,t2.hour*3600+t2.minute*60+t2.second);
            }
            else
            {
                mDBManager.insertIntoTimeDetails(startDay,packageName,t1.hour*3600+t1.minute*60+t1.second,t2.hour*3600+t2.minute*60+t2.second);
            }
        }
        @Override
        public void run() {
            String lastusedPackageName=getUsedPackageName();
            Time t=new Time();
            t.setToNow();
            while(isScreenOn)
            {
                String inFrontPackageName=getUsedPackageName();
                if(!lastusedPackageName.equals(inFrontPackageName))
                {
                    Time temp=new Time();
                    temp.setToNow();
                    insertPackageNameIntoDB(lastusedPackageName,t.toMillis(false)/1000,temp.toMillis(false)/1000);
                    Log.e("walsli",lastusedPackageName+" "+t.format2445()+" "+temp.format2445());
                    t.setToNow();
                    lastusedPackageName=inFrontPackageName;

                }
                try
                {
                    Thread.sleep(1000);
                }
                catch (InterruptedException e)
                {
                    e.printStackTrace();
                }

            }
            Time temp=new Time();
            temp.setToNow();
            insertPackageNameIntoDB(lastusedPackageName,t.toMillis(false)/1000,temp.toMillis(false)/1000);
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
