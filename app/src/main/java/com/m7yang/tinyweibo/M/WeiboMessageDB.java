package com.m7yang.tinyweibo.M;

import android.provider.BaseColumns;

/**
 * Created by m7yang on 15-12-8.
 * Defines the table name and column names for a single table
 */
public final class WeiboMessageDB {
    // To prevent someone from accidentally instantiating the contract class,
    // give it an empty constructor.
    public WeiboMessageDB() {}

    // Inner class that defines the table contents
    public static abstract class DBEntry implements BaseColumns {

        public static final String DATABASE_URI       = "wbm";  //NOTE:please keep same prefix as SQLiteOpenHelper's DATABASE_NAME
        public static final String TABLE_NAME         = "wb_messages";

        public static final String COLUMN_WB_ID       = "wb_id";
        public static final String COLUMN_CONTENT     = "content";
        public static final String COLUMN_CREATE_DATE = "created_date";

        //public static final String COLUMN_PICS_PATH   = "pics_path"; // relative path of pictures for single weibo message
    }
}