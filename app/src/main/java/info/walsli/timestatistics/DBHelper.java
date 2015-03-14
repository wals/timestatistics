package info.walsli.timestatistics;

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

public class DBHelper extends SQLiteOpenHelper {
    public static boolean lock=false;

    public DBHelper(Context c) {
        super(c, ConstantField.DB_NAME, null, 2);
    }

    @Override
    public void onCreate(SQLiteDatabase db) {
        db.execSQL(ConstantField.CREATE_TABLE_TIMEINFO);
        db.execSQL(ConstantField.CREATE_TABLE_TIMEDETAILS);
        db.execSQL(ConstantField.CREATE_TABLE_TIMEOFDAYS);
    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {

    }
}