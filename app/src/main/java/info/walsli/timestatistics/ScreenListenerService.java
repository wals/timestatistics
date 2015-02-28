package info.walsli.timestatistics;

import android.app.ActivityManager;
import android.app.Notification;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.app.Service;
import android.appwidget.AppWidgetManager;
import android.content.BroadcastReceiver;
import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.RectF;
import android.os.IBinder;
import android.os.PowerManager;
import android.util.TypedValue;
import android.widget.RemoteViews;
import java.text.SimpleDateFormat;
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
        filter.addAction("info.walsli.timestatistics.NEW_WIDGET");
        registerReceiver(receiver, filter);
        PowerManager powerManager = (PowerManager) getSystemService(Context.POWER_SERVICE);
        isScreenOn =powerManager.isScreenOn();
        Thread thread=new Thread(new ProgressRunable());
        thread.start();
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
            long todayseconds=MyTime.getTodaySeconds();
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
    public void updatewidget(long minutes)
    {
        if(MyLogic.isCallCountDown(minutes))
        {
            callcountdown(minutes);
        }
        RemoteViews updateView = new RemoteViews(this.getPackageName(),R.layout.mywidget);
        if(MyLogic.getModel()==3)
        {
            updateView.setTextViewTextSize(R.id.text_02, TypedValue.COMPLEX_UNIT_PX,35);

            Bitmap bitmap=BitmapFactory.decodeResource(getResources(), R.drawable.backgroundofwidgetmx3).copy(Bitmap.Config.ARGB_8888, true); ;
            Canvas canvas = new Canvas(bitmap);
            Paint p=new Paint();
            p.setAntiAlias(true);
            p.setDither(true);
            p.setTypeface(MyApplication.getInstance().gettypeface());
            p.setColor(Color.WHITE);


            RectF oval2 = new RectF(20,20,124,124);//xyxy
            p.setStyle(Paint.Style.STROKE);
            p.setStrokeWidth((float) 1.5);
            canvas.drawArc(oval2,-20,290,false, p);

            //时刻
            String time="";
            if((minutes+1)/60<10)time+="0";
            time+=String.valueOf((minutes+1)/60);
            time+=":";
            if(((minutes+1)%60)<10)time+="0";
            time+=String.valueOf((minutes+1)%60);
            p.setStyle(Paint.Style.FILL);
            p.setSubpixelText(true);
            p.setTextSize(31);
            canvas.drawText(time,37,85,p);
            updateView.setImageViewBitmap(R.id.my_widget_img, bitmap);
        }
        else
        {
            updateView.setTextViewTextSize(R.id.text_02, TypedValue.COMPLEX_UNIT_PX,30);

            Bitmap bitmap=BitmapFactory.decodeResource(getResources(), R.drawable.backgroundofwidgetmx2).copy(Bitmap.Config.ARGB_8888, true); ;
            Canvas canvas = new Canvas(bitmap);
            Paint p=new Paint();
            p.setAntiAlias(true);
            p.setDither(true);
            p.setTypeface(MyApplication.gettypeface());
            p.setColor(Color.WHITE);


            RectF oval2 = new RectF(15,15,91,91);//xyxy
            p.setStyle(Paint.Style.STROKE);
            p.setStrokeWidth((float) 1.0);
            canvas.drawArc(oval2,-20,290,false, p);

            //时刻
            String time="";
            if((minutes+1)/60<10)time+="0";
            time+=String.valueOf((minutes+1)/60);
            time+=":";
            if(((minutes+1)%60)<10)time+="0";
            time+=String.valueOf((minutes+1)%60);
            p.setStyle(Paint.Style.FILL);
            p.setSubpixelText(true);
            p.setTextSize(23);
            canvas.drawText(time,27,63,p);
            updateView.setImageViewBitmap(R.id.my_widget_img, bitmap);
        }
        Intent launchIntent = new Intent();
        launchIntent.setComponent(new ComponentName("info.walsli.timestatistics","info.walsli.timestatistics.MainActivity"));
        launchIntent.setAction(Intent.ACTION_MAIN);
        launchIntent.addCategory(Intent.CATEGORY_LAUNCHER);
        launchIntent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_RESET_TASK_IF_NEEDED);
        PendingIntent intentAction = PendingIntent.getActivity(this, 0, launchIntent, 0);
        updateView.setOnClickPendingIntent(R.id.my_widget_img, intentAction);
        AppWidgetManager awg = AppWidgetManager.getInstance(this);
        awg.updateAppWidget(new ComponentName(this, MyWidget.class), updateView);
    }
    public void callcountdown(long mins)
    {
        MyLogic.setTodayRemind(true);
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
            if (Intent.ACTION_SCREEN_ON.equals(intent.getAction()))
            {

                SimpleDateFormat sDateFormat = new SimpleDateFormat("yyyy:MM:dd:HH:mm:ss");
                String date = sDateFormat.format(new java.util.Date());
                MyTime.setBeginTime(date);
                MyTime.increaseScreenonFrequency();
                isScreenOn =true;
                Thread thread=new Thread(new ProgressRunable());
                thread.start();
            }
            else if (Intent.ACTION_SCREEN_OFF.equals(intent.getAction()))
            {

                SimpleDateFormat sDateFormat = new SimpleDateFormat("yyyy:MM:dd:HH:mm:ss");
                String date = sDateFormat.format(new java.util.Date());
                MyTime.processTime(date);
                isScreenOn =false;

            }
            else if (Intent.ACTION_SHUTDOWN.equals(intent.getAction()))
            {
                SimpleDateFormat sDateFormat = new SimpleDateFormat("yyyy:MM:dd:HH:mm:ss");
                String date = sDateFormat.format(new java.util.Date());
                MyTime.processTime(date);
                isScreenOn =false;
            }
            else if (Intent.ACTION_DATE_CHANGED.equals(intent.getAction()))
            {
                if(isScreenOn)
                {
                    SimpleDateFormat sDateFormat = new SimpleDateFormat("yyyy:MM:dd:HH:mm:ss");
                    String date = sDateFormat.format(new java.util.Date());
                    MyTime.processTime(date);
                    MyTime.setBeginTime(date);
                    updatewidget(MyTime.getTodaySeconds()/60-1);
                }
            }
            else if ("info.walsli.timestatistics.NEW_WIDGET".equals(intent.getAction()))
            {
                SimpleDateFormat sDateFormat = new SimpleDateFormat("yyyy:MM:dd:HH:mm:ss");
                String date = sDateFormat.format(new java.util.Date());
                MyTime.processTime(date);
                MyTime.setBeginTime(date);
                updatewidget(MyTime.getTodaySeconds()/60-1);
            }
        }
    };

}
