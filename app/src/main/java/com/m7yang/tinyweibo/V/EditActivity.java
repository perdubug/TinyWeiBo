package com.m7yang.tinyweibo.V;

import android.app.Activity;
import android.os.Bundle;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.Snackbar;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.View;
import android.widget.EditText;

import com.m7yang.tinyweibo.C.TaskCompleted;
import com.m7yang.tinyweibo.C.WBViewController;
import com.m7yang.tinyweibo.M.WeiboConfiguration;
import com.m7yang.tinyweibo.R;

public class EditActivity extends AppCompatActivity implements TaskCompleted {

    private static final String TAG = "V.EditActivity";

    private WBViewController mWc = null;

    public EditText content;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_edit);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        FloatingActionButton fab = (FloatingActionButton) findViewById(R.id.fab);
        fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                //Snackbar.make(view, "Replace with your own action", Snackbar.LENGTH_LONG)
                //        .setAction("Action", null).show();
                if(mWc == null) {
                    mWc = new WBViewController(EditActivity.this);
                }

                content = (EditText) findViewById(R.id.content);
                mWc.mNetPushOpTask.execute(content.getText().toString().trim());
            }
        });
    }

    @Override
    public void onTaskComplete(Integer result) {

        if (result == 0) {
            setResult(Activity.RESULT_OK);
        } else {
            setResult(Activity.RESULT_CANCELED);
        }

        // Close current activity since AsyncTask is completed
        finish();
    }

}
