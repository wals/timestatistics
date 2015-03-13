package info.walsli.timestatistics;


public final class ConstantField {
    public static final String PACKAGE_NAME="info.walsli.timestatistics";

    public static final String CREATE_TABLE_TIMEINFO="create table timeinfo(_id integer primary key autoincrement,datenum integer,opentime integer,closetime integer)";
    public static final String CREATE_TABLE_TIMEOFDAYS="create table timeofdays(_id integer primary key autoincrement,datenum integer,todaytime integer)";
    public static final String CREATE_TABLE_TIMEOFAPPS="create table timeofapps(_id integer primary key autoincrement,datenum integer,appname text,appseconds integer)";
    public static final String DB_NAME="time.db";

    public static final String MAINVIEW_TOP_STRING="今天我的手机被使用了";
    public static final String MAINVIEW_DATA_RESTORE_STRING_1="数据还原中，本页面时间暂停";
    public static final String MAINVIEW_DATA_RESTORE_STRING_2="还原完毕后恢复正常";
    public static final String MAINVIEW_PROTECT_EYESIGHT_STRING_1="你已经使用超过两个小时的时间，影响视力";
    public static final String MAINVIEW_PROTECT_EYESIGHT_STRING_2="不要只顾着低头社交，也许你抬起头就可以";
    public static final String MAINVIEW_PROTECT_EYESIGHT_STRING_3="发现更多的美好，抬起头来动一动吧";
    public static final String MainView_SAYINGS_1="把活着的每一天看作生命的最后一天";
    public static final String MainView_SAYINGS_2_1="盛年不再来  一日难再晨";
    public static final String MainView_SAYINGS_2_2="及时当自勉  岁月不待人";
    public static final String MainView_SAYINGS_3="早餐要吃饱";
    public static final String MainView_SAYINGS_4="完成工作的方法是爱惜每一分钟";
    public static final String MainView_SAYINGS_5="午餐要吃好";
    public static final String MainView_SAYINGS_6_1="普通人只想如何度过时间";
    public static final String MainView_SAYINGS_6_2="有才能的人才能利用时间";
    public static final String MainView_SAYINGS_7="晚餐要吃少";
    public static final String MainView_SAYINGS_8_1="黑夜到临的时候";
    public static final String MainView_SAYINGS_8_2="没有人能够把一角阳光继续保留";
    public static final String MainView_SAYINGS_9="把活着的每一天看作生命的最后一天";
    public static final String MainView_SAYINGS_10_1="不要为已消逝之年华叹息";
    public static final String MainView_SAYINGS_10_2="须正视欲匆匆溜走的时光";

    public static final String SPITEM_REBOOT="reboot";
    public static final String SPITEM_MODEL="model";
    public static final String SPITEM_ISCONUTDOWN="iscountdown";
    public static final String SPITEM_TODAYREMIND="todayremind";
    public static final String SPITEM_COUNTDOWNNUM="countdownnum";
    public static final String SPITEM_INIT="init";
    public static final String SPITEM_BEGINTIME="begintime";
    public static final String SPITEM_TODAYSECONDS="todayseconds";
    public static final String SPITEM_ALLSECONDS="allseconds";
    public static final String SPITEM_DATE="date";
    public static final String SPITEM_SCREENONFREQUENCY="screenon_frequency";

    public static String BLANKACTIVITY_FINISH="info.walsli.timestatistics.BlankActivityFinishReceiver";
    public static String MAINACTIVITY_FINISH="info.walsli.timestatistics.MainActivityFinishReceiver";
    public static String MAINACTIVITY_RESTART="info.walsli.timestatistics.MainActivityRestartReceiver";
    public static String STATISTICSACTIVITY_FINISH="info.walsli.timestatistics.StatisticsActivityFinishReceiver";
    public static String SERVICE_NAME="info.walsli.timestatistics.ScreenListenerService";
    public static String NEW_WIDGET="info.walsli.timestatistics.NEW_WIDGET";
    public static String MAINACTIVITY_NAME="info.walsli.timestatistics.MainActivity";
    public static String STATISTICSACTIVITY_RESTART="info.walsli.timestatistics.StatisticsActivityRestartReceiver";

}
