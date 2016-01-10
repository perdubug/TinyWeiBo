package com.m7yang.tinyweibo.V;

import android.app.Activity;
import android.app.ListActivity;
import android.app.LoaderManager;
import android.content.CursorLoader;
import android.content.Intent;
import android.content.Loader;
import android.database.Cursor;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Bundle;
import android.provider.ContactsContract;
import android.support.design.widget.FloatingActionButton;
import android.util.Log;
import android.view.View;
import android.support.design.widget.NavigationView;
import android.support.v4.view.GravityCompat;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.ActionBarDrawerToggle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.ArrayAdapter;
import android.widget.ListView;
import android.widget.SimpleCursorAdapter;

import com.m7yang.tinyweibo.C.DBContentProvider;
import com.m7yang.tinyweibo.M.WeiboMessageDB;
import com.m7yang.tinyweibo.R;
import java.util.ArrayList;

public class MainActivity extends AppCompatActivity
        implements NavigationView.OnNavigationItemSelectedListener,LoaderManager.LoaderCallbacks<Cursor> {

    private static final String TAG = "MainActivity";

    // LIST OF ARRAY STRINGS WHICH WILL SERVE AS LIST ITEMS
    private ArrayList<String> listItems = new ArrayList<String>();

    // DEFINING A STRING ADAPTER WHICH WILL HANDLE THE DATA OF THE LISTVIEW
    private ArrayAdapter<String> adapter;

    // This is the Adapter being used to display the list's data
    SimpleCursorAdapter mAdapter;

    ListView mListView;

    private static final int REQUEST_LOGIN = 3;
    private static final int REQUEST_SYNC  = 5;
    private static final int REQUEST_SHOW  = 7;

    // These are the DB rows that we will retrieve
    static final String[] PROJECTION = new String[] {WeiboMessageDB.DBEntry.COLUMN_CONTENT,
                                                     WeiboMessageDB.DBEntry.COLUMN_CREATE_DATE};

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        FloatingActionButton fab = (FloatingActionButton) findViewById(R.id.fab);
        fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(MainActivity.this, EditActivity.class);
                startActivityForResult(intent, 1);
                //Snackbar.make(view, "Replace with your own action", Snackbar.LENGTH_LONG).setAction("Action", null).show();
            }
        });

        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        ActionBarDrawerToggle toggle = new ActionBarDrawerToggle(
                this, drawer, toolbar, R.string.navigation_drawer_open, R.string.navigation_drawer_close);
        drawer.setDrawerListener(toggle);
        toggle.syncState();

        NavigationView navigationView = (NavigationView) findViewById(R.id.nav_view);
        navigationView.setNavigationItemSelectedListener(this);

        mListView = (ListView) findViewById(R.id.main_listview);
        mAdapter = new SimpleCursorAdapter(getBaseContext(),
                                           android.R.layout.simple_list_item_2,
                                           null,
                                           new String[] {WeiboMessageDB.DBEntry.COLUMN_CREATE_DATE,WeiboMessageDB.DBEntry.COLUMN_CONTENT},
                                           new int[] {android.R.id.text1, android.R.id.text2},
                                           0);
        mListView.setAdapter(mAdapter);

        // Start Login activity in case need Sina Weibo auth
        Intent intent = new Intent(MainActivity.this, LoginActivity.class);
        startActivityForResult(intent, REQUEST_LOGIN);
    }

    public void addSingleItem(String str) {
        adapter.add(str);
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {

        Intent intent;

        if (resultCode == Activity.RESULT_OK) {
            switch (requestCode) {
                case REQUEST_LOGIN:
                    // Login activity done successfully, then start SyncActivity activity to sync data from Sina WeiBo
                    intent = new Intent(MainActivity.this, SyncActivity.class);
                    startActivityForResult(intent, REQUEST_SYNC);
                    break;
                case REQUEST_SYNC:
                    // SyncActivity activity done, meaning that we may have something in DB, then prepare the loader - Either re-connect
                    // with an existing one, or start a new one.
                    getLoaderManager().initLoader(0, null, this);
                    break;
                case REQUEST_SHOW:
                    //TODO:Something need to down when show data in DB done
                    break;
            }

        }
    }

    @Override
    public void onBackPressed() {
        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        if (drawer.isDrawerOpen(GravityCompat.START)) {
            drawer.closeDrawer(GravityCompat.START);
        } else {
            super.onBackPressed();
        }
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.main, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

        //noinspection SimplifiableIfStatement
        if (id == R.id.action_settings) {
            return true;
        }

        return super.onOptionsItemSelected(item);
    }

    @SuppressWarnings("StatementWithEmptyBody")
    @Override
    public boolean onNavigationItemSelected(MenuItem item) {
        // Handle navigation view item clicks here.
        int id = item.getItemId();

        if (id == R.id.nav_camera) {

            // Sync weibo with local DB
            Intent intent = new Intent(MainActivity.this, SyncActivity.class);
            startActivityForResult(intent, 1);

        } else if (id == R.id.nav_slideshow) {


        }

        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        drawer.closeDrawer(GravityCompat.START);
        return true;
    }

    @Override
    public void onStart() {
        super.onStart();
    }

    @Override
    public void onStop() {
        super.onStop();
    }

    //
    // Called when a new Loader needs to be created
    //
    public Loader<Cursor> onCreateLoader(int id, Bundle args) {

         //
         // This requires the URI of the Content Provider
         // projection is the list of columns of the database to return. Null will return all the columns
         // selection is the filter which declares which rows to return. Null will return all the rows for the given URI.
         // selectionArgs:  You may include ?s in the selection, which will be replaced by the values from selectionArgs, in the order that they appear in the selection.
         // The values will be bound as Strings.
         // sortOrder determines the order of rows. Passing null will use the default sort order, which may be unordered.
         // To back a ListView with a Cursor, the cursor must contain a column named _ID.
         //
        return new CursorLoader(this, DBContentProvider.CONTENT_URI, PROJECTION, null , null, null);
    }

    //
    // Called when a previously created loader has finished loading
    //
    public void onLoadFinished(Loader<Cursor> loader, Cursor data) {
        // Swap the new cursor in.  (The framework will take care of closing the
        // old cursor once we return.)
        if(mAdapter != null && data != null)
            mAdapter.swapCursor(data); //swap the new cursor in.
        else
            Log.v(TAG,"OnLoadFinished: mAdapter is null");
    }

    //
    // Called when a previously created loader is reset, making the data unavailable
    //
    public void onLoaderReset(Loader<Cursor> loader) {
        // This is called when the last Cursor provided to onLoadFinished()
        // above is about to be closed.  We need to make sure we are no
        // longer using it.
        if(mAdapter != null)
            mAdapter.swapCursor(null);
        else
            Log.v(TAG,"OnLoadFinished: mAdapter is null");
    }

}