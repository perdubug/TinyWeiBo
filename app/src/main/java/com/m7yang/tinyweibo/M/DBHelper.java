package com.m7yang.tinyweibo.M;

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.util.Log;

/**
 * Created by m7yang on 15-12-30.
 */
public class DBHelper extends SQLiteOpenHelper {

    private static final String TAG = "M.DBHelper";

    private static final String TEXT_TYPE     = " TEXT";
    private static final String INTE_TYPE     = " INTEGER";
    private static final String COMMA_SEP     = ",";
    private static final String DATABASE_NAME = "wbm.db";

    //
    // If you change the database schema, you must increment the database version.
    //
    private static final int DATABASE_VERSION = 1;

    // Database creation sql statement
    private static final String DATABASE_CREATE =
            "CREATE TABLE " + WeiboMessageDB.DBEntry.TABLE_NAME + " (" +
                    WeiboMessageDB.DBEntry._ID + " INTEGER PRIMARY KEY AUTOINCREMENT, " +
                    WeiboMessageDB.DBEntry.COLUMN_WB_ID + INTE_TYPE + COMMA_SEP +
                    WeiboMessageDB.DBEntry.COLUMN_CONTENT + TEXT_TYPE + COMMA_SEP +
                    WeiboMessageDB.DBEntry.COLUMN_CREATE_DATE + TEXT_TYPE + " );";

    // statements that delete the table
    private static final String DATABASE_DELETE_ENTRIES = "DROP TABLE IF EXISTS " + WeiboMessageDB.DBEntry.TABLE_NAME;

    public DBHelper(Context context) {
        super(context, DATABASE_NAME, null, DATABASE_VERSION);
    }

    @Override
    public void onCreate(SQLiteDatabase database) {

        Log.v(TAG,"Create Table:"+DATABASE_CREATE);
        database.execSQL(DATABASE_CREATE);
    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        Log.v(TAG,DBHelper.class.getName() + "Upgrading database from version " + oldVersion + " to " + newVersion + ", which will destroy all old data");
        db.execSQL(DATABASE_DELETE_ENTRIES);
        onCreate(db);
    }
}
