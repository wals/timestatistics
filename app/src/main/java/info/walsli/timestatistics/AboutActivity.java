package info.walsli.timestatistics;

import android.app.Activity;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import android.os.Bundle;

import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;


public class AboutActivity extends Activity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_about);
        findViewById(R.id.activity_about).setBackgroundResource(R.drawable.defaultbackground);
        fucksmartbar();
        ((AboutView)findViewById(R.id.aboutview)).setVersion(getVersion());
    }
    private String getVersion() {
        try {
            PackageManager manager = this.getPackageManager();
            PackageInfo info = manager.getPackageInfo(this.getPackageName(), 0);
            return info.versionName;
        } catch (Exception e) {
            e.printStackTrace();
        }
        return "2.31FTL";
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

}
