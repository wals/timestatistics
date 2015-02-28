package info.walsli.timestatistics;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;

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

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        SystemBarTintManager tintManager = new SystemBarTintManager(this);
        tintManager.setStatusBarTintEnabled(true);
        tintManager.setNavigationBarTintEnabled(true);
        tintManager.setTintColor(Color.rgb((int) (Math.random() * 255),(int) (Math.random() * 255),(int) (Math.random() * 255)));

        getPreferenceManager().setSharedPreferencesName("info.walsli.timestatistics");
        addPreferencesFromResource(R.xml.setting);
        mySharedPreferences =getSharedPreferences("info.walsli.timestatistics",Activity.MODE_MULTI_PROCESS);
        editor = mySharedPreferences.edit();
        setiscountdown();
        setclicklistener();
        setchangelistener();
        setallsecondssummary();

        helper = new DBHelper(getApplicationContext());
    }

    public void setiscountdown()
    {
        CheckBoxPreference iscountdown=(CheckBoxPreference) findPreference("iscountdown");
        EditTextPreference countdownnum=(EditTextPreference) findPreference("countdownnum");
        countdownnum.setSummary(mySharedPreferences.getString("countdownnum","120"));
        if(iscountdown.isChecked())
        {
            countdownnum.setEnabled(true);
        }
        else
        {
            countdownnum.setEnabled(false);
        }

    }
    public void setallsecondssummary()
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
        findPreference("allseconds").setTitle(allseconds_title);
    }
    public void setclicklistener()
    {
        Preference feedback=findPreference("feedback");
        feedback.setOnPreferenceClickListener(this);
        Preference quit=findPreference("quit");
        quit.setOnPreferenceClickListener(this);
        Preference mstore=findPreference("mstore");
        mstore.setOnPreferenceClickListener(this);
        Preference backup=findPreference("backup");
        backup.setOnPreferenceClickListener(this);
        Preference restore=findPreference("restore");
        restore.setOnPreferenceClickListener(this);
        Preference cleardata=findPreference("cleardata");
        cleardata.setOnPreferenceClickListener(this);

    }
    public void setchangelistener()
    {
        CheckBoxPreference isCountDown=(CheckBoxPreference) findPreference("iscountdown");
        isCountDown.setOnPreferenceChangeListener(this);
        EditTextPreference countDownNum=(EditTextPreference) findPreference("countdownnum");
        countDownNum.setOnPreferenceChangeListener(this);
        CheckBoxPreference isIconHide=(CheckBoxPreference) findPreference("isiconhide");
        isIconHide.setOnPreferenceChangeListener(this);
    }

    @Override
    public boolean onPreferenceClick(Preference preference) {
        if(preference.getKey().equals("feedback"))
        {
            Intent newMail=new Intent(Intent.ACTION_SENDTO);
            newMail.setData(Uri.parse("mailto:925378224@qq.com"));
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
        else if(preference.getKey().equals("cleardata"))
        {
            backupData();
            helper.cleandb();
            MyLogic.initSharedPreferences();
            Toast.makeText(this,"清空数据完毕,之前数据已经自动备份",Toast.LENGTH_SHORT).show();
        }
        return false;
    }
    @Override
    public boolean onPreferenceChange(Preference preference, Object newValue) {
        if(preference.getKey().equals("iscountdown"))
        {
            CheckBoxPreference iscountdown=(CheckBoxPreference) findPreference("iscountdown");
            EditTextPreference countdownnum=(EditTextPreference) findPreference("countdownnum");
            if(!iscountdown.isChecked())
            {
                countdownnum.setEnabled(true);

            }
            else
            {
                countdownnum.setEnabled(false);
            }
            return true;
        }

        else if(preference.getKey().equals("isiconhide"))
        {
            CheckBoxPreference isiconhide=(CheckBoxPreference) findPreference("isiconhide");
            PackageManager packageManager = getPackageManager();
            ComponentName componentName = new ComponentName("info.walsli.timestatistics","info.walsli.timestatistics.BlankActivity");
            if(isiconhide.isChecked())
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
            EditTextPreference countdownnum=(EditTextPreference) findPreference("countdownnum");
            countdownnum.setSummary((CharSequence) newValue);
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
