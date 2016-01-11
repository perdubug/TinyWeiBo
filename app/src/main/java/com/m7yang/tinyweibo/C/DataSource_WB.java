package com.m7yang.tinyweibo.C;

import android.content.ContentValues;
import android.content.Context;
import android.content.CursorLoader;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.util.Log;

import com.m7yang.tinyweibo.M.DBHelper;
import com.m7yang.tinyweibo.M.DataModel_WB;
import com.m7yang.tinyweibo.M.WeiboMessageDB;

import org.w3c.dom.Comment;

import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

/**
 * Created by m7yang on 15-12-30.
 *
 * This class is my DAO. It maintains the database connection and supports adding new record and fetching all records.
 */
public class DataSource_WB {

    private static final String TAG = "C.DataSource_WB";

    // Database fields
    private SQLiteDatabase mDatabase;
    private DBHelper mDBHelper;
    private Context  mContext;

    private String[] mAllColumns = { WeiboMessageDB.DBEntry._ID,
                                     WeiboMessageDB.DBEntry.COLUMN_WB_ID,
                                     WeiboMessageDB.DBEntry.COLUMN_CONTENT,
                                     WeiboMessageDB.DBEntry.COLUMN_CREATE_DATE };

    public DataSource_WB(Context context) {
        mContext = context;
        mDBHelper = new DBHelper(context);
    }

    public void openForWrite() throws SQLException {
        mDatabase = mDBHelper.getWritableDatabase();
    }

    public void openForRead() {
        mDatabase = mDBHelper.getReadableDatabase();
    }

    public void close() {
        mDBHelper.close();
    }

    public DataModel_WB createRecord(DataModel_WB dw)
    {
        ContentValues values = new ContentValues();

        values.put(WeiboMessageDB.DBEntry.COLUMN_WB_ID, dw.getWBId());
        values.put(WeiboMessageDB.DBEntry.COLUMN_CONTENT, dw.getContent());
        values.put(WeiboMessageDB.DBEntry.COLUMN_CREATE_DATE, dw.getCreatedDate());

        Long insertId = mDatabase.insert(WeiboMessageDB.DBEntry.TABLE_NAME,
                                         null,values);

        Cursor cursor = mDatabase.query(WeiboMessageDB.DBEntry.TABLE_NAME,
                                        mAllColumns, WeiboMessageDB.DBEntry._ID + " = " + insertId,
                                        null, null, null, null);
        cursor.moveToFirst();
        DataModel_WB rWb = cursorToRecord(cursor);
        cursor.close();

        return rWb;
    }

    // id is database generated id when insert, not wb_id(which is from Sina WeiBo)
    public void deleteRecord(Long id)
    {
        mDatabase.delete(WeiboMessageDB.DBEntry.TABLE_NAME,
                WeiboMessageDB.DBEntry._ID + " = " + id,
                null);
    }

    public List<DataModel_WB> getAllRecords()
    {
        List<DataModel_WB> records = new ArrayList<DataModel_WB>();

        Cursor cursor = mDatabase.query(WeiboMessageDB.DBEntry.TABLE_NAME,
                                        mAllColumns, null, null, null, null, null);
        cursor.moveToFirst();

        while (!cursor.isAfterLast()) {
            DataModel_WB dw = cursorToRecord(cursor);
            records.add(dw);
            cursor.moveToNext();
        }

        // make sure to close the cursor
        cursor.close();
        return records;
    }

    public Cursor getAllRecordsEx()
    {
        return mDatabase.query(WeiboMessageDB.DBEntry.TABLE_NAME,
                               mAllColumns, null, null, null, null, null);
    }

    // get N records,N specified by n
    public List<DataModel_WB> getNRecords(int n)
    {
        List<DataModel_WB> records = new ArrayList<DataModel_WB>();

        Cursor cursor = mDatabase.query(WeiboMessageDB.DBEntry.TABLE_NAME,
                                        mAllColumns, null, null, null, null, null);
        cursor.moveToFirst();

        while (!cursor.isAfterLast()) {
            DataModel_WB dw = cursorToRecord(cursor);
            records.add(dw);
            cursor.moveToNext();
        }

        // make sure to close the cursor
        cursor.close();
        return records;
    }

    // Check if we already have records in DB->Table
    public boolean isDBEmpty()
    {
        Cursor mCursor = mDatabase.rawQuery("SELECT * FROM " + WeiboMessageDB.DBEntry.TABLE_NAME, null);
        Boolean rowExists = (mCursor.getCount() == 0)? false : true;

        return rowExists;
    }

    //
    // get the record point by cursor
    //
    private DataModel_WB cursorToRecord(Cursor cursor)
    {
        DataModel_WB dw = new DataModel_WB();

        dw.setId(cursor.getLong(0));
        dw.setWBId(cursor.getLong(1));
        dw.setContent(cursor.getString(2));
        dw.setCreatedDate(cursor.getString(3));

        return dw;
    }

}
