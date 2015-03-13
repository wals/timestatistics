package info.walsli.timestatistics;

import android.app.PendingIntent;
import android.appwidget.AppWidgetManager;
import android.appwidget.AppWidgetProvider;
import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.widget.RemoteViews;

public class MyWidget extends AppWidgetProvider{
    public void onReceive(Context context, Intent intent)
    {
        super.onReceive(context, intent);
    }

    // 每次更新都调用一次该方法，使用频繁
    public void onUpdate(Context context, AppWidgetManager appWidgetManager, int[] appWidgetIds)
    {
        super.onUpdate(context, appWidgetManager, appWidgetIds);

        Intent intent = new Intent(context, MainActivity.class);
        intent.putExtra(AppWidgetManager.EXTRA_APPWIDGET_ID, appWidgetIds);
        intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
        intent.setData(Uri.parse(intent.toUri(Intent.URI_INTENT_SCHEME)));
        PendingIntent pendIntent = PendingIntent.getActivity(context, 0,intent, PendingIntent.FLAG_UPDATE_CURRENT);
        RemoteViews views = new RemoteViews(context.getPackageName(),R.layout.mywidget);
        views.setOnClickPendingIntent(R.id.my_widget_img, pendIntent);
        appWidgetManager.updateAppWidget(appWidgetIds, views );

    }

    // 没删除一个就调用一次
    public void onDeleted(Context context, int[] appWidgetIds)
    {
        super.onDeleted(context, appWidgetIds);

    }

    // 当该Widget第一次添加到桌面是调用该方法，可添加多次但只第一次调用
    public void onEnabled(Context context)
    {
        super.onEnabled(context);
        Intent intent=new Intent(ConstantField.NEW_WIDGET);
        context.sendBroadcast(intent);
    }

    // 当最后一个该Widget删除是调用该方法，注意是最后一个
    public void onDisabled(Context context)
    {
        super.onDisabled(context);
    }

}