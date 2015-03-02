package info.walsli.timestatistics;

import android.app.Activity;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;

public class BootService extends BroadcastReceiver{
    @Override
    public void onReceive(Context context, Intent intent) {
        SharedPreferences mySharedPreferences =context.getSharedPreferences("info.walsli.timestatistics",Activity.MODE_MULTI_PROCESS);

        if (intent.getAction().equals("android.intent.action.BOOT_COMPLETED")&&mySharedPreferences.getBoolean("reboot",true))
        {
            Intent serviceIntent = new Intent(context, ScreenListenerService.class);
            context.startService(serviceIntent);
        }
    }


}