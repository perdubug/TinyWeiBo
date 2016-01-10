package com.m7yang.tinyweibo.V;

import android.app.Activity;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;

import com.m7yang.tinyweibo.C.TaskCompleted;
import com.m7yang.tinyweibo.C.WBViewController;
import com.m7yang.tinyweibo.R;

public class SyncActivity extends AppCompatActivity implements TaskCompleted {

    private WBViewController mWc = null;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_sync);
//        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
//        setSupportActionBar(toolbar);

//        FloatingActionButton fab = (FloatingActionButton) findViewById(R.id.fab);
//        fab.setOnClickListener(new View.OnClickListener() {
//            @Override
//            public void onClick(View view) {
//                Snackbar.make(view, "Replace with your own action", Snackbar.LENGTH_LONG)
//                        .setAction("Action", null).show();
//            }
//        });


        if(mWc == null) {
            mWc = new WBViewController(SyncActivity.this);
        }

        mWc.mNetSyncOpTask.execute();
    }

    @Override
    public void onTaskComplete(Integer result) {

        setResult(Activity.RESULT_OK);

        // Close current activity since AsyncTask is completed
        finish();
    }
}
