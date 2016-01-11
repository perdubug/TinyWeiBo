package com.m7yang.tinyweibo.V;

import android.app.Activity;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.View;
import android.widget.ProgressBar;

import com.m7yang.tinyweibo.C.DataSource_WB;
import com.m7yang.tinyweibo.C.TaskCompleted;
import com.m7yang.tinyweibo.C.WBViewController;
import com.m7yang.tinyweibo.M.DataModel_WB;
import com.m7yang.tinyweibo.M.HttpsUtil;
import com.m7yang.tinyweibo.M.WeiboConfiguration;
import com.m7yang.tinyweibo.R;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.sql.SQLException;

public class SyncActivity extends AppCompatActivity {

    private static final String TAG = "V.SyncActivity";

    private ProgressBar mProgress;

    private static final int INIT_MAX_PROGRESS = 100;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_sync);

        mProgress = (ProgressBar) findViewById(R.id.progressBar);
        mProgress.setMax(INIT_MAX_PROGRESS);
        mProgress.setVisibility(View.VISIBLE);
        mProgress.setProgress(0);

        new NetworkSyncOpTask().execute();
    }

    class NetworkSyncOpTask extends AsyncTask<String, Integer, Integer> {

        String mResult = null;

        @Override
        protected void onPostExecute(Integer result) {

            Log.v(TAG, "NetworkSyncOpTask@onPostExecute called");

            mProgress.setVisibility(View.GONE);

            if (result == 0) {
                setResult(Activity.RESULT_OK);
            } else {
                setResult(Activity.RESULT_CANCELED);
            }

            // Close current activity since AsyncTask is completed
            finish();
        }

        protected void onProgressUpdate(Integer... progress) {
            mProgress.setProgress(progress[0]);
        }

        protected Integer doInBackground(String... params)
        {
            int nRet = 0; // 1 - job failed, 0 - job done successfully
            Long expected_total_wb_nums = -1L;
            Long actually_total_wb_nums = 0L;

            String url = "https://api.weibo.com/2/statuses/user_timeline.json?"
                    + "access_token=" + WeiboConfiguration.ACCESS_TOKEN
                    + "&feature=1"
                    + "&trim_user=1";
            //+ "&max_id=3923924100210158"

            mResult = HttpsUtil.HttpsPost(url, null);
            Log.v(TAG, "WeiBo Sync:" + mResult);

            DataSource_WB dsw = new DataSource_WB(getBaseContext());

            try {
                dsw.openForWrite();
            } catch (SQLException e) {
                e.printStackTrace();
                nRet = 1;
                return nRet;
            }

            do {
                try {

                    JSONObject jsonObject = new JSONObject(mResult);

                    // check if we get an error response
                    try {
                        String err = jsonObject.getString("error");
                        Log.v(TAG, "doInBackground@JSONException:err=" + jsonObject.getString("error")
                                 + "error_code:" + Long.parseLong(jsonObject.getString("error_code")));

                        nRet = 1;
                        break;

                    } catch (JSONException e) {
                        // No 'error' meaning everything is ok
                    }

                    if (expected_total_wb_nums == -1L) {
                        expected_total_wb_nums = Long.parseLong(jsonObject.getString("total_number"));
                        Log.v(TAG, "doInBackground: expected_total_wb_nums = " + expected_total_wb_nums);
                        nRet = 1;
                        break;
                    }

                    Long next_wb_id;

                    try {
                        next_wb_id = Long.parseLong(jsonObject.getString("next_cursor"));
                    } catch (JSONException e) {
                        Log.v(TAG, "doInBackground: NO NEXT ID FOUND");
                        nRet = 1;
                        break;
                    }

                    JSONArray jsonArray = jsonObject.getJSONArray("statuses");

                    int len = jsonArray.length();
                    for (int i = 0; i < len; i++) {

                        JSONObject jb = (JSONObject) jsonArray.opt(i);

                        String date = jb.getString("created_at");
                        Long id = jb.getLong("id");
                        String text = jb.getString("text");

                        actually_total_wb_nums++;
                        Log.v(TAG, "Index " + actually_total_wb_nums + ":" + date + ">>ID:" + id + ">>" + text);

                        // add one weibo message into db as one record
                        DataModel_WB dmw = new DataModel_WB();

                        dmw.setWBId(id);
                        dmw.setContent(text);
                        dmw.setCreatedDate(date);

                        DataModel_WB ret = dsw.createRecord(dmw);
                        Log.v(TAG, "Insert record ok:" + date + ">>ID:" + id + ">>" + text);

                        // Update progress bar
                        publishProgress((int) ((actually_total_wb_nums / (float) expected_total_wb_nums) * INIT_MAX_PROGRESS));
                    }

                    //
                    // get next 5 messages based on 'next_cursor' by specifying max_id in HTTP GET method
                    //
                    mResult = HttpsUtil.HttpsPost(url + "&max_id=" + next_wb_id, null);

                } catch (JSONException e) {
                    e.printStackTrace();
                    Log.v(TAG, "doInBackground@JSONException@expected_total_wb_nums = " + expected_total_wb_nums);
                    Log.v(TAG, "doInBackground@JSONException: NO NEXT ID FOUND");

                    nRet = 1;
                }

                // Escape early if cancel() is called
                if (isCancelled()) {
                    nRet = 1;
                    break;
                }

            } while( actually_total_wb_nums < expected_total_wb_nums );

            dsw.close();

            return nRet;
        }
    }
}
