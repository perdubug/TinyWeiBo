package com.m7yang.tinyweibo.C;

import android.content.Context;
import android.os.AsyncTask;
import android.util.Log;
import android.widget.ProgressBar;

import com.m7yang.tinyweibo.M.DataModel_WB;
import com.m7yang.tinyweibo.M.HttpsUtil;
import com.m7yang.tinyweibo.M.WeiboConfiguration;
import com.m7yang.tinyweibo.R;

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

    public WBViewController(Context context) {

        mContext  = context;
        mCallback = (TaskCompleted)context;

        mNetAuthOpTask = new NetworkAuthOpTask();
        mNetPushOpTask = new NetworkPushOpTask();
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
}
