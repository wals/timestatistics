package info.walsli.timestatistics;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.text.SimpleDateFormat;

import android.app.Activity;
import android.content.ComponentName;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.database.Cursor;
import android.graphics.Color;
import android.net.Uri;
import android.os.Bundle;
import android.os.Environment;
import android.os.Handler;
import android.os.Message;
import android.preference.PreferenceCategory;
import android.preference.PreferenceScreen;
import android.text.InputType;
import android.widget.Toast;
import android.preference.CheckBoxPreference;
import android.preference.EditTextPreference;
import android.preference.Preference;
import android.preference.Preference.OnPreferenceChangeListener;
import android.preference.Preference.OnPreferenceClickListener;
import android.preference.PreferenceActivity;

import com.readystatesoftware.systembartint.SystemBarTintManager;

public class SettingActivity extends PreferenceActivity implements OnPreferenceChangeListener,OnPreferenceClickListener{
    private SharedPreferences mySharedPreferences;
    private SharedPreferences.Editor editor;
    private DBHelper helper;
    private boolean isrestore=false;

    private PreferenceCategory allsecondsPreferenceCategory;
    private CheckBoxPreference isCountdounCheckBoxPreference;
    private EditTextPreference countdownNumEditTextPreference;
    private PreferenceCategory backupOrRestorePreferenceCategory;
    private Preference backupPreference;
    private Preference restorePreference;
    private Preference resetPreference;
    private PreferenceCategory feedbackOrMstorePreferenceCategory;
    private Preference feedbackPreference;
    private Preference mstorePreference;
    private PreferenceCategory advancedSettingPreferenceCategory;
    private CheckBoxPreference hideIconCheckBoxPreference;
    private PreferenceCategory blankPreferenceCategory;
    private Preference aboutPreference;
    private Preference quitPreference;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        SystemBarTintManager tintManager = new SystemBarTintManager(this);
        tintManager.setStatusBarTintEnabled(true);
        tintManager.setNavigationBarTintEnabled(true);
        tintManager.setTintColor(Color.BLACK);

        getPreferenceManager().setSharedPreferencesName("info.walsli.timestatistics");
        addPreferencesFromResource(R.xml.setting);
        mySharedPreferences =getSharedPreferences("info.walsli.timestatistics",Activity.MODE_MULTI_PROCESS);
        editor = mySharedPreferences.edit();
        helper = new DBHelper(getApplicationContext());

        initPreferenceScreen();
        showPreferences(mySharedPreferences.getBoolean("iscountdown",false));
    }
    private void initPreferenceScreen()
    {
        allsecondsPreferenceCategory=new PreferenceCategory(this);
        isCountdounCheckBoxPreference=new CheckBoxPreference(this);
        countdownNumEditTextPreference=new EditTextPreference(this);
        backupPreference=new Preference(this);
        restorePreference=new Preference(this);
        resetPreference=new Preference(this);
        feedbackPreference=new Preference(this);
        mstorePreference=new Preference(this);
        hideIconCheckBoxPreference =new CheckBoxPreference(this);
        aboutPreference=new Preference(this);
        quitPreference=new Preference(this);
        backupOrRestorePreferenceCategory=new PreferenceCategory(this);
        feedbackOrMstorePreferenceCategory=new PreferenceCategory(this);
        advancedSettingPreferenceCategory=new PreferenceCategory(this);
        blankPreferenceCategory=new PreferenceCategory(this);

        allsecondsPreferenceCategory.setKey("allseconds");
        allsecondsPreferenceCategory.setTitle(getAllSecondsTitle());
        allsecondsPreferenceCategory.setOrder(0);
        isCountdounCheckBoxPreference.setKey("iscountdown");
        isCountdounCheckBoxPreference.setTitle("开启每日限额功能");
        isCountdounCheckBoxPreference.setSummary("开启每日限额功能后，每天使用手机时间达到限额时，将会以通知栏提醒方式提醒放下手机休息");
        isCountdounCheckBoxPreference.setOnPreferenceChangeListener(this);
        isCountdounCheckBoxPreference.setOrder(1);
        countdownNumEditTextPreference.setKey("countdownnum");
        countdownNumEditTextPreference.setTitle("每日手机使用限额(分钟)");
        countdownNumEditTextPreference.setSummary(mySharedPreferences.getString("countdownnum", "120"));
        countdownNumEditTextPreference.setDefaultValue("120");
        countdownNumEditTextPreference.getEditText().setText("每日手机使用限额(分钟)");
        countdownNumEditTextPreference.getEditText().setRawInputType(InputType.TYPE_CLASS_NUMBER | InputType.TYPE_NUMBER_FLAG_DECIMAL);
        countdownNumEditTextPreference.setOnPreferenceChangeListener(this);
        countdownNumEditTextPreference.setOrder(2);
        backupOrRestorePreferenceCategory.setTitle("备份还原");
        backupOrRestorePreferenceCategory.setOrder(3);
        backupPreference.setKey("backup");
        backupPreference.setTitle("备份");
        backupPreference.setSummary("备份文件位于下载文件夹中的timestatistics文件夹中，文件名为魅时间备份.bak\n应用会自动将每次备份内容拷贝一份同文件夹并以当前时间命名");
        backupPreference.setOnPreferenceClickListener(this);
        backupPreference.setOrder(4);
        restorePreference.setKey("restore");
        restorePreference.setTitle("还原");
        restorePreference.setOnPreferenceClickListener(this);
        restorePreference.setOrder(5);
        feedbackOrMstorePreferenceCategory.setTitle("反馈评价");
        feedbackOrMstorePreferenceCategory.setOrder(6);
        feedbackPreference.setKey("feedback");
        feedbackPreference.setTitle("反馈建议");
        feedbackPreference.setOnPreferenceClickListener(this);
        feedbackPreference.setOrder(7);
        mstorePreference.setKey("mstore");
        mstorePreference.setTitle("在应用中心评价");
        mstorePreference.setOnPreferenceClickListener(this);
        mstorePreference.setOrder(8);
        advancedSettingPreferenceCategory.setTitle("高级设置");
        advancedSettingPreferenceCategory.setOrder(9);
        hideIconCheckBoxPreference.setKey("hideIcon");
        hideIconCheckBoxPreference.setTitle("隐藏应用图标");
        hideIconCheckBoxPreference.setSummary("长按主屏幕，在下方添加小工具即可添加魅时间小工具，小工具可以代替图标功能，并且显示时间会动态更新");//TODO add alert
        hideIconCheckBoxPreference.setOnPreferenceChangeListener(this);
        hideIconCheckBoxPreference.setOrder(10);
        resetPreference.setKey("reset");
        resetPreference.setTitle("清除数据");
        resetPreference.setSummary("清空数据时会自动备份当前数据");
        resetPreference.setOnPreferenceClickListener(this);
        resetPreference.setOrder(11);
        blankPreferenceCategory.setOrder(12);
        aboutPreference.setKey("about");
        aboutPreference.setTitle("关于");
        aboutPreference.setOnPreferenceClickListener(this);
        aboutPreference.setOrder(13);
        quitPreference.setKey("quit");
        quitPreference.setTitle("完全退出");//TODO add alert
        quitPreference.setOnPreferenceClickListener(this);
        quitPreference.setOrder(14);
    }
    private void showPreferences(boolean iscountdown)
    {
        PreferenceScreen screen = getPreferenceScreen();
        screen.removeAll();
        screen.setOrderingAsAdded(true);
        screen.addPreference(allsecondsPreferenceCategory);
        screen.addPreference(isCountdounCheckBoxPreference);
        if(iscountdown)
        {
            screen.addPreference(countdownNumEditTextPreference);
        }
        screen.addPreference(backupOrRestorePreferenceCategory);
        screen.addPreference(backupPreference);
        screen.addPreference(restorePreference);
        screen.addPreference(feedbackOrMstorePreferenceCategory);
        screen.addPreference(feedbackPreference);
        screen.addPreference(mstorePreference);
        screen.addPreference(advancedSettingPreferenceCategory);
        screen.addPreference(hideIconCheckBoxPreference);
        screen.addPreference(resetPreference);
        screen.addPreference(blankPreferenceCategory);
        screen.addPreference(aboutPreference);
        screen.addPreference(quitPreference);
    }
    public String getAllSecondsTitle()
    {
        long allsecondspass=mySharedPreferences.getLong("allseconds", 0);
        String allseconds_title="手机总使用时间:";
        if(allsecondspass<60)
        {
            allseconds_title+="小于一分钟";
        }
        if(allsecondspass/86400>0)
        {
            allseconds_title+=String.valueOf(allsecondspass/86400)+"天";
            allsecondspass=allsecondspass%86400;
        }
        if(allsecondspass/3600>0)
        {
            allseconds_title+=String.valueOf(allsecondspass/3600)+"小时";
            allsecondspass=allsecondspass%3600;
        }
        if(allsecondspass/60>0)
        {
            allseconds_title+=String.valueOf(allsecondspass/60)+"分钟";
        }
        return allseconds_title;
    }

    @Override
    public boolean onPreferenceClick(Preference preference) {
        if(preference.getKey().equals("feedback"))
        {
            Intent newMail=new Intent(Intent.ACTION_SENDTO);
            newMail.setData(Uri.parse("mailto:meitime666422@qq.com"));
            newMail.putExtra(Intent.EXTRA_SUBJECT, "魅时间应用反馈");
            newMail.putExtra(Intent.EXTRA_TEXT, "");
            startActivity(newMail);
        }
        else if(preference.getKey().equals("mstore"))
        {
            Uri uri = Uri.parse("mstore:http://app.meizu.com/phone/apps/2e97acf7d20e436b856cdd5244b99308");
            Intent intent = new Intent(Intent.ACTION_VIEW, uri);
            startActivity(intent);
        }
        else if(preference.getKey().equals("quit"))
        {
            Intent intent = new Intent(this, ScreenListenerService.class);
            stopService(intent);
            Intent intent2 = new Intent();
            intent2.setAction("info.walsli.timestatistics.MainActivityFinishReceiver");
            this.sendBroadcast(intent2);
            Intent intent3 = new Intent();
            intent3.setAction("info.walsli.timestatistics.StatisticsActivityFinishReceiver");
            this.sendBroadcast(intent3);
            editor.putString("begintime","");
            editor.putBoolean("reboot",false);
            editor.apply();
            this.finish();
        }
        else if(preference.getKey().equals("backup"))
        {
            backupData();
        }
        else if(preference.getKey().equals("restore"))
        {
            Thread thread=new Thread(new ProgressRunable());
            thread.start();
            Toast.makeText(this,"正在 后台还原数据，请稍后，根据数据量大小还原过程可能持续几分钟，还原完毕后时间显示将恢复正常",Toast.LENGTH_LONG).show();
        }
        else if(preference.getKey().equals("reset"))
        {
            backupData();
            helper.cleandb();
            MyLogic.initSharedPreferences();
            Toast.makeText(this,"清空数据完毕,之前数据已经自动备份",Toast.LENGTH_SHORT).show();
        }
        else if(preference.getKey().equals("about"))
        {
            startActivity(new Intent(this,AboutActivity.class));
        }
        return false;
    }
    @Override
    public boolean onPreferenceChange(Preference preference, Object newValue) {
        if(preference.getKey().equals("iscountdown"))
        {
            showPreferences(!mySharedPreferences.getBoolean("iscountdown",false));
            return true;
        }

        else if(preference.getKey().equals("hideIcon"))
        {
            PackageManager packageManager = getPackageManager();
            ComponentName componentName = new ComponentName("info.walsli.timestatistics","info.walsli.timestatistics.BlankActivity");
            if(hideIconCheckBoxPreference.isChecked())
            {
                packageManager.setComponentEnabledSetting(componentName, PackageManager.COMPONENT_ENABLED_STATE_DEFAULT,
                        PackageManager.DONT_KILL_APP);
            }
            else
            {
                packageManager.setComponentEnabledSetting(componentName, PackageManager.COMPONENT_ENABLED_STATE_DISABLED,
                        PackageManager.DONT_KILL_APP);
            }

            return true;
        }
        else if(preference.getKey().equals("countdownnum"))
        {
            countdownNumEditTextPreference.setSummary((CharSequence) newValue);
            return true;
        }
        return false;
    }
    public void backupData()
    {
        SimpleDateFormat sDateFormat = new SimpleDateFormat("yyyy:MM:dd:HH:mm:ss");
        String date = sDateFormat.format(new java.util.Date());
        MyTime.processTime(date);
        MyTime.setBeginTime(date);
        String folderstr = Environment.getExternalStorageDirectory().getAbsolutePath()+"/Download/timestatistics/";
        File folder = new File(folderstr);
        if(!folder.exists())
        {
            folder.mkdir();
        }
        String path = Environment.getExternalStorageDirectory().getAbsolutePath()+"/Download/timestatistics/魅时间备份.bak";
        File saveFile = new File(path);
        String pathbackup = Environment.getExternalStorageDirectory().getAbsolutePath()+"/Download/timestatistics/魅时间备份"+date+".bak";
        File saveFilebackup = new File(pathbackup);
        if(saveFile.exists())
        {
            saveFile.delete();
        }
        try
        {
            BufferedWriter output = new BufferedWriter(new FileWriter(saveFile,true));
            BufferedWriter outputbackup = new BufferedWriter(new FileWriter(saveFilebackup,true));
            Cursor c=helper.query("select * from timeinfo");
            while(c.moveToNext())
            {
                output.append(String.valueOf(c.getInt(1))+" "+String.valueOf(c.getInt(2))+" "+String.valueOf(c.getInt(3))+"\n");
                outputbackup.append(String.valueOf(c.getInt(1))+" "+String.valueOf(c.getInt(2))+" "+String.valueOf(c.getInt(3))+"\n");
            }
            output.flush();
            output.close();
            outputbackup.flush();
            outputbackup.close();
        } catch (IOException e) {
            e.printStackTrace();
        }
        Toast.makeText(this,"备份成功！",Toast.LENGTH_SHORT).show();
    }
    Handler mHandler=new Handler(){
        public void handleMessage(Message message)
        {
            switch(message.what)
            {
                case 1:
                    Toast.makeText(getApplicationContext(), "已经在还原数据过程在，请稍后", Toast.LENGTH_SHORT).show();
                    break;
                case 2:
                    Toast.makeText(getApplicationContext(), "未检测到备份文件，请将备份文件置于下载文件夹并重命名为魅时间备份.bak", Toast.LENGTH_SHORT).show();
                    break;
                case 3:
                    Toast.makeText(getApplicationContext(), "还原完毕", Toast.LENGTH_SHORT).show();
                    break;
            }

        }
    };
    class ProgressRunable implements Runnable
    {
        @Override
        public void run() {
            Message message=new Message();
            if(isrestore)
            {
                message.what=1;
            }
            else
            {
                isrestore=true;
                helper.cleandb();
                String path = Environment.getExternalStorageDirectory().getAbsolutePath()+"/Download/timestatistics/魅时间备份.bak";
                File file = new File(path);
                if(!file.exists())
                {
                    message.what=2;
                }
                else
                {
                    BufferedReader reader = null;
                    try
                    {

                        reader = new BufferedReader(new FileReader(file));
                        String tempString;
                        while ((tempString = reader.readLine()) != null)
                        {
                            String ints[]=tempString.split(" ");
                            helper.insertIntoTimeinfo(Integer.parseInt(ints[0]), Integer.parseInt(ints[1]), Integer.parseInt(ints[2]));
                        }
                        reader.close();
                        SimpleDateFormat sDateFormat = new SimpleDateFormat("yyyy:MM:dd:HH:mm:ss");
                        String date = sDateFormat.format(new java.util.Date());
                        MyTime.setBeginTime(date);
                        sDateFormat = new SimpleDateFormat("dd");
                        date = sDateFormat.format(new java.util.Date());
                        editor.putInt("date",Integer.parseInt(date));
                        for(int i=1;i<=MyTime.getDayNum();i++)
                        {
                            helper.settlement(i);
                        }
                        Cursor c=helper.query("select * from timeofdays where datenum="+String.valueOf(MyTime.getDayNum()));
                        while(c.moveToNext())
                        {
                            editor.putLong("todayseconds", c.getInt(2));
                        }
                        c=helper.query("select * from timeofdays");
                        long allseconds=0;
                        while(c.moveToNext())
                        {
                            allseconds+=c.getInt(2);
                        }
                        editor.putLong("allseconds", allseconds);
                        editor.apply();
                    }
                    catch (IOException e)
                    {
                        e.printStackTrace();
                    }
                    finally
                    {
                        if (reader != null)
                        {
                            try
                            {
                                reader.close();
                            }
                            catch (IOException e1)
                            {
                                e1.printStackTrace();
                            }
                        }
                    }
                    message.what=3;
                }
                isrestore=false;
                mHandler.sendMessage(message);
            }
        }
    }
}
