package com.m7yang.tinyweibo.C;

import android.content.Context;
import android.os.AsyncTask;
import android.util.Log;

import com.m7yang.tinyweibo.M.DataModel_WB;
import com.m7yang.tinyweibo.M.HttpsUtil;
import com.m7yang.tinyweibo.M.WeiboConfiguration;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.UnsupportedEncodingException;
import java.net.URLEncoder;
import java.sql.SQLException;

/**
 * Created by m7yang on 15-12-29.
 *
 * This is main view controller to 'split'/'bridging' M and V
 * It's a single instance
 */
public class WBViewController {

    private static final String TAG = "C.WBViewController";

    private Context mContext;
    private TaskCompleted mCallback;

    public NetworkAuthOpTask mNetAuthOpTask;
    public NetworkPushOpTask mNetPushOpTask;
    public NetworkSyncOpTask mNetSyncOpTask;

    public WBViewController(Context context) {

        mContext  = context;
        mCallback = (TaskCompleted)context;

        mNetAuthOpTask = new NetworkAuthOpTask();
        mNetPushOpTask = new NetworkPushOpTask();
        mNetSyncOpTask = new NetworkSyncOpTask();
    }

    public class NetworkPushOpTask extends AsyncTask<String, Void, Integer> {

        String mResult = null;

        @Override
        protected void onPostExecute(Integer result) {

            Log.v(TAG, "NetworkPushOpTask@onPostExecute called");

            if (result != 0 && mResult != null) {

            }

            mCallback.onTaskComplete(result);
        }

        protected Integer doInBackground(String... params) {

            String value = params[0];

            try {
                value = URLEncoder.encode(value, "UTF-8");
            } catch (UnsupportedEncodingException e) {
                e.printStackTrace();
            }
            String data = "format=json&access_token="
                    + WeiboConfiguration.ACCESS_TOKEN
                    + "&status=" + value;

            Log.v(TAG, "NetworkPushOpTask@doInBackground called");

            Log.v(TAG, "WeiBo Content:" + data);
            mResult = HttpsUtil.HttpsPost("https://api.weibo.com/2/statuses/update.json?", data);
            Log.v(TAG, "WeiBo post:" + mResult);

            return (mResult == null ? 0 : 1);
        }
    }


    public class NetworkAuthOpTask extends AsyncTask<String, Void, Integer> {

        String mResult = null;

        // Task you want to do on UIThread after completing Network operation
        // onPostExecute is called after doInBackground finishes its task
        @Override
        protected void onPostExecute(Integer result) {

            Log.v(TAG, "NetworkAuthOpTask@onPostExecute called");

            if (result != 0 && mResult != null && mResult.startsWith("{\"access_token\":")) {
                int i = mResult.indexOf(":");
                int j = mResult.indexOf(",");
                WeiboConfiguration.ACCESS_TOKEN = mResult.substring(i + 2, j - 1);

                Log.v(TAG, "ACCESS_TOKEN:"+WeiboConfiguration.ACCESS_TOKEN);
            }

            mCallback.onTaskComplete(result);
        }

        protected Integer doInBackground(String... params) {

            Log.v(TAG, "NetworkAuthOpTask@doInBackground called");

            mResult = HttpsUtil.HttpsPost(WeiboConfiguration.SINA_ACCESS_TOKEN + params[0], "");
            if (mResult != null) {
                Log.v(TAG, "Login result:"+mResult);
            }

            return (mResult == null ? 0 : 1);
        }
    }


    public class NetworkSyncOpTask extends AsyncTask<String, Void, Integer> {

        String mResult = null;

        @Override
        protected void onPostExecute(Integer result) {

            Log.v(TAG, "NetworkSyncOpTask@onPostExecute called");

            if (result != 0 && mResult != null) {

            }

            mCallback.onTaskComplete(result);
        }

        protected Integer doInBackground(String... params)
        {
            Long expected_total_wb_nums = -1L;
            Long actually_total_wb_nums = 0L;

            String url = "https://api.weibo.com/2/statuses/user_timeline.json?"
                        + "access_token=" + WeiboConfiguration.ACCESS_TOKEN
                        + "&feature=1"
                        + "&trim_user=1";
                        //+ "&max_id=3923924100210158"

            mResult = HttpsUtil.HttpsPost(url, null);
            //Log.v(TAG, "WeiBo Sync:" + mResult);

            DataSource_WB dsw = new DataSource_WB(mContext);

            try {
                dsw.openForWrite();
            } catch (SQLException e) {
                e.printStackTrace();
            }

            do {
                try {

                    JSONObject jsonObject = new JSONObject(mResult);

                    if (expected_total_wb_nums == -1L) { //TODO:Only show one line first
                        expected_total_wb_nums = Long.parseLong(jsonObject.getString("total_number"));
                    }

                    Long next_wb_id        = Long.parseLong(jsonObject.getString("next_cursor"));

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
                    }

                    //
                    // get next 5 messages based on 'next_cursor' by specifying max_id in HTTP GET method
                    //
                    mResult = HttpsUtil.HttpsPost(url + "&max_id=" + next_wb_id, null);

              } catch (JSONException e) {
                  e.printStackTrace();
              }

            } while( actually_total_wb_nums < expected_total_wb_nums );

            dsw.close();

            return (mResult == null ? 0 : 1);
        }
    }

}
