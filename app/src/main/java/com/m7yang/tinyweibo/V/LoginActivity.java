package com.m7yang.tinyweibo.V;

import android.app.Activity;
import android.graphics.Bitmap;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.Snackbar;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.View;
import android.webkit.WebSettings;
import android.webkit.WebView;
import android.webkit.WebViewClient;

import com.m7yang.tinyweibo.C.TaskCompleted;
import com.m7yang.tinyweibo.C.WBViewController;
import com.m7yang.tinyweibo.M.HttpsUtil;
import com.m7yang.tinyweibo.M.WeiboConfiguration;
import com.m7yang.tinyweibo.R;

public class LoginActivity extends AppCompatActivity implements TaskCompleted {

    private static final String TAG = "V.LoginActivity";

    public WebView mWebview;
    private WBViewController mWc = null;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);
        //Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        //setSupportActionBar(toolbar);

        mWebview = (WebView) this.findViewById(R.id.oauth_webview);
        mWebview.getSettings().setJavaScriptEnabled(true);
        mWebview.getSettings().setCacheMode(WebSettings.LOAD_NO_CACHE);
        mWebview.setFocusable(true);
        mWebview.loadUrl(WeiboConfiguration.SINA_OAUTH);

        mWebview.setWebViewClient(new WebViewClient() {
            @Override
            public void onPageFinished(WebView view, String url) {
                Log.v(TAG, "onPageFinished:" + url);
                super.onPageFinished(view, url);
            }

            @Override
            public void onPageStarted(WebView view, String url, Bitmap favicon) {
                Log.v(TAG, "onPageStarted:" + url);
                if (url.startsWith(WeiboConfiguration.SINA_REDIRECT_URI)) {

                    view.cancelLongPress();
                    view.stopLoading();

                    Uri uri = Uri.parse(url);
                    String code = uri.getQueryParameter("code");
                    //Log.e("code", WeiboConfiguration.SINA_ACCESS_TOKEN + code);

                    if (code != null) {

                        if(mWc == null) {
                            mWc = new WBViewController(LoginActivity.this);
                        }

                        mWc.mNetAuthOpTask.execute(code);
                    }

                }
                super.onPageStarted(view, url, favicon);
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
