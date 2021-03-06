package info.walsli.timestatistics;

import android.app.Activity;
import android.content.Intent;
import android.content.SharedPreferences;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.graphics.Color;
import android.net.Uri;
import android.os.Bundle;
import android.os.Environment;
import android.os.Handler;
import android.os.Message;
import android.preference.CheckBoxPreference;
import android.preference.EditTextPreference;
import android.preference.Preference;
import android.preference.Preference.OnPreferenceChangeListener;
import android.preference.Preference.OnPreferenceClickListener;
import android.preference.PreferenceActivity;
import android.preference.PreferenceCategory;
import android.preference.PreferenceScreen;
import android.text.InputType;
import android.text.format.Time;
import android.widget.Toast;

import com.readystatesoftware.systembartint.SystemBarTintManager;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;

public class SettingActivity extends PreferenceActivity implements OnPreferenceChangeListener,OnPreferenceClickListener{
    private SharedPreferences mySharedPreferences;
    private SharedPreferences.Editor editor;
    private static Handler mHandler;

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

        getPreferenceManager().setSharedPreferencesName(ConstantField.PACKAGE_NAME);
        addPreferencesFromResource(R.xml.setting);
        mySharedPreferences =getSharedPreferences(ConstantField.PACKAGE_NAME,Activity.MODE_MULTI_PROCESS);
        editor = mySharedPreferences.edit();

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
        resetPreference.setKey("reset");
        resetPreference.setTitle("清除数据");
        resetPreference.setSummary("清空数据时会自动备份当前数据");
        resetPreference.setOnPreferenceClickListener(this);
        resetPreference.setOrder(10);
        blankPreferenceCategory.setOrder(11);
        aboutPreference.setKey("about");
        aboutPreference.setTitle("关于");
        aboutPreference.setOnPreferenceClickListener(this);
        aboutPreference.setOrder(12);
        quitPreference.setKey("quit");
        quitPreference.setTitle("完全退出");//TODO add alert
        quitPreference.setOnPreferenceClickListener(this);
        quitPreference.setOrder(13);
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
            intent2.setAction(ConstantField.MAINACTIVITY_FINISH);
            this.sendBroadcast(intent2);
            Intent intent3 = new Intent();
            intent3.setAction(ConstantField.STATISTICSACTIVITY_FINISH);
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

            if(DBHelper.lock)
            {
                Toast.makeText(getApplicationContext(), "数据库忙，请稍后", Toast.LENGTH_SHORT).show();
            }
            else
            {
                Thread thread=new Thread(new ProgressRunable());
                thread.start();
                Toast.makeText(this,"正在 后台还原数据，请稍后，根据数据量大小还原过程可能持续几分钟，还原完毕后时间显示将恢复正常",Toast.LENGTH_LONG).show();
            }

        }
        else if(preference.getKey().equals("reset"))
        {
            if(DBHelper.lock)
            {
                Toast.makeText(this,"数据库忙，请稍候",Toast.LENGTH_SHORT).show();
            }
            else
            {
                backupData();
                DBManager dbManager=new DBManager(MyApplication.getInstance());
                dbManager.cleanDB();
                dbManager.closeDB();
                MyUtils.initSharedPreferences();
                Toast.makeText(this,"清空数据完毕,之前数据已经自动备份",Toast.LENGTH_SHORT).show();
            }
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
        else if(preference.getKey().equals("countdownnum"))
        {
            countdownNumEditTextPreference.setSummary((CharSequence) newValue);
            return true;
        }
        return false;
    }
    public void backupData()
    {
        if(DBHelper.lock)
        {
            Toast.makeText(this,"数据库忙，请稍候",Toast.LENGTH_SHORT).show();
        }
        else
        {
            DBHelper.lock=true;

            Time t=new Time();
            t.setToNow();
            long l=t.toMillis(false)/1000;
            MyUtils.processTime(l);
            MyUtils.setBeginTime(l);
            String folderstr = Environment.getExternalStorageDirectory().getAbsolutePath()+"/Download/timestatistics/";
            File folder = new File(folderstr);
            if(!folder.exists())
            {
                folder.mkdir();
            }
            String path = Environment.getExternalStorageDirectory().getAbsolutePath()+"/Download/timestatistics/魅时间备份.bak";
            File saveFile = new File(path);
            String pathbackup = Environment.getExternalStorageDirectory().getAbsolutePath()+"/Download/timestatistics/魅时间备份"+t.format2445()+".bak";
            File saveFilebackup = new File(pathbackup);
            if(saveFile.exists())
            {
                saveFile.delete();
            }
            try
            {
                BufferedWriter output = new BufferedWriter(new FileWriter(saveFile,true));
                BufferedWriter outputbackup = new BufferedWriter(new FileWriter(saveFilebackup,true));
                DBManager dbManager=new DBManager(MyApplication.getInstance());
                Cursor c=dbManager.query("select * from timeinfo");
                while(c.moveToNext())
                {
                    output.append(String.valueOf(c.getInt(1))+" "+String.valueOf(c.getInt(2))+" "+String.valueOf(c.getInt(3))+"\n");
                    outputbackup.append(String.valueOf(c.getInt(1))+" "+String.valueOf(c.getInt(2))+" "+String.valueOf(c.getInt(3))+"\n");
                }
                c.close();
                dbManager.closeDB();
                output.flush();
                output.close();
                outputbackup.flush();
                outputbackup.close();
            } catch (IOException e) {
                e.printStackTrace();
            }
            Toast.makeText(this,"备份成功！",Toast.LENGTH_SHORT).show();

            DBHelper.lock=false;
        }

    }
    @Override
    public void onResume()
    {
        mHandler=new Handler(){

            public void handleMessage(Message message)
            {
                switch(message.what)
                {
                    case 1:
                        Toast.makeText(getApplicationContext(), "数据库忙，请稍后", Toast.LENGTH_SHORT).show();
                        break;
                    case 2:
                        Toast.makeText(getApplicationContext(), "未检测到备份文件，请将备份文件置于下载文件夹并重命名为魅时间备份.bak", Toast.LENGTH_SHORT).show();
                        break;
                    case 3:
                        Toast.makeText(getApplicationContext(), "还原完毕.", Toast.LENGTH_SHORT).show();
                        break;
                    case 4:
                        restorePreference.setSummary("数据还原完毕");
                        Intent intent = new Intent();
                        intent.setAction("info.walsli.timestatistics.MainActivityRestartReceiver");
                        SettingActivity.this.sendBroadcast(intent);

                        break;
                    default:
                        restorePreference.setSummary("数据还原中，已还原"+ (message.what-100) +"%");
                        showPreferences(mySharedPreferences.getBoolean("iscountdown",false));

                        break;
                }

            }
        };
        super.onResume();
    }
    class ProgressRunable implements Runnable
    {
        @Override
        public void run() {
            if(DBHelper.lock)
            {
                mHandler.sendEmptyMessage(1);
            }
            else
            {
                DBHelper.lock=true;
                DBManager dbManager=new DBManager(MyApplication.getInstance());
                dbManager.cleanDB();
                String path = Environment.getExternalStorageDirectory().getAbsolutePath()+"/Download/timestatistics/魅时间备份.bak";
                File file = new File(path);
                if(!file.exists())
                {
                    mHandler.sendEmptyMessage(2);
                }
                else
                {
                    BufferedReader reader = null;
                    try
                    {
                        BufferedReader countReader=new BufferedReader(new FileReader(file));
                        int count=0;
                        while(countReader.readLine()!=null)
                        {
                            count++;
                        }
                        countReader.close();

                        reader = new BufferedReader(new FileReader(file));
                        String tempString;
                        int tempInteger=0;
                        SQLiteDatabase db=dbManager.getDB();
                        db.beginTransaction();
                        try
                        {
                            while ((tempString = reader.readLine()) != null)
                            {
                                if(count>=90&&tempInteger%(count/90)==0)
                                {
                                    mHandler.sendEmptyMessage(100 + tempInteger / (count / 90));
                                }
                                String ints[]=tempString.split(" ");
                                dbManager.insertIntoTimeinfo(Integer.parseInt(ints[0]), Integer.parseInt(ints[1]), Integer.parseInt(ints[2]));
                                tempInteger++;
                            }
                            reader.close();
                            db.setTransactionSuccessful();
                        }
                        finally {
                            db.endTransaction();
                        }
                        Time t=new Time();
                        t.setToNow();
                        long l=t.toMillis(false)/1000;
                        MyUtils.setBeginTime(l);
                        int date=MyUtils.getDaysFrom20140715();
                        editor.putInt("date",date);
                        for(int i=1;i<=date;i++)
                        {
                            dbManager.calculateTimeOfDays(i);
                            mHandler.sendEmptyMessage((int) (190+(float)i/date*10));
                        }
                        Cursor c=dbManager.query("select * from timeofdays where datenum="+String.valueOf(date));
                        while(c.moveToNext())
                        {
                            editor.putLong("todayseconds", c.getInt(2));
                        }
                        c=dbManager.query("select * from timeofdays");
                        long allseconds=0;
                        while(c.moveToNext())
                        {
                            allseconds+=c.getInt(2);
                        }
                        editor.putLong("allseconds", allseconds);
                        editor.apply();
                        c.close();
                        dbManager.closeDB();
                        mHandler.sendEmptyMessage(4);
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
                    mHandler.sendEmptyMessage(3);
                }
                DBHelper.lock=false;
            }
        }
    }
}
