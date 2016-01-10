package com.m7yang.tinyweibo.C;

import android.content.ContentProvider;
import android.content.ContentValues;
import android.content.UriMatcher;
import android.database.Cursor;
import android.database.sqlite.SQLiteOpenHelper;
import android.net.Uri;

import com.m7yang.tinyweibo.M.WeiboMessageDB;

/**
 * Created by m7yang on 16-1-10.
 */
public class DBContentProvider extends ContentProvider{
    public static final String AUTHORITY = "com.tinyweibo.DBContentProvider";

    // A URI to do operations on DB
    public static final Uri CONTENT_URI = Uri.parse("content://" + AUTHORITY + "/" + WeiboMessageDB.DBEntry.DATABASE_URI);

    // Contants to identify the requested operation
    private static final int CUSTOMERS = 1;

    private static final UriMatcher uriMatcher;

    static {
        uriMatcher = new UriMatcher(UriMatcher.NO_MATCH);
        uriMatcher.addURI(AUTHORITY, WeiboMessageDB.DBEntry.TABLE_NAME,CUSTOMERS);
    }

    // This content provider does the database operations by this object
    DataSource_WB mCustomerDB;

    @Override
    public boolean onCreate() {
        mCustomerDB = new DataSource_WB(getContext());
        mCustomerDB.openForRead();
        return true;
    }

    @Override
    public String getType(Uri uri) {
        return null;
    }

    @Override
    public Cursor query(Uri uri, String[] projections, String selection, String[] selectionArgs, String sortOrder) {

        int n = uriMatcher.match(uri);

        //TODO: WHY BELOW CODE DOES NOT WORK?
        //if (n == CUSTOMERS) {
        //    return mCustomerDB.getAllRecordsEx();
        //}else {
        //    return null;
        //}

        return mCustomerDB.getAllRecordsEx();
    }

    @Override
    public int delete(Uri uri, String selection, String[] selectionArgs) {
        return 0;
    }

    @Override
    public Uri insert(Uri uri, ContentValues values) {
        return null;
    }

    @Override
    public int update(Uri uri, ContentValues values, String selection, String[] selectionArgs) {
        return 0;
    }
}
