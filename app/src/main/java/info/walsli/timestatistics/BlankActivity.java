package info.walsli.timestatistics;

import android.app.Activity;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.os.Bundle;

public class BlankActivity extends Activity {
    private IntentFilter filter;
    private BlankActivityFinishReceiver receiver;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_blank);

        receiver = new BlankActivityFinishReceiver();
        filter = new IntentFilter();
        filter.addAction(MyLogic.BLANKACTIVITY_FINISH);
        registerReceiver(receiver, filter);

        startActivity(new Intent(this,MainActivity.class));
    }
    @Override
    protected void onDestroy()
    {
        unregisterReceiver(receiver);
        super.onDestroy();
    }
    private class BlankActivityFinishReceiver extends BroadcastReceiver {

        @Override
        public void onReceive(Context context, Intent intent) {

            BlankActivity.this.finish();
        }
    }
}
