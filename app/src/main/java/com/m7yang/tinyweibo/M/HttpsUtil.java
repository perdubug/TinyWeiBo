package com.m7yang.tinyweibo.M;

import java.io.BufferedReader;
import java.io.DataOutputStream;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.net.HttpURLConnection;
import java.net.URL;

public class HttpsUtil {

    private static final String TAG = "M.HttpsUtil";

    private final static int CONNENT_TIMEOUT = 25000;
    private final static int READ_TIMEOUT    = 25000;
    private static String mCookie;
    public static int httpsResponseCode;

    public static String HttpsPost(String httpsurl, String data) {

        String result = null;
        HttpURLConnection http = null;
        URL url;

        try {
            url = new URL(httpsurl);

            http = (HttpURLConnection)url.openConnection();

            http.setConnectTimeout(CONNENT_TIMEOUT);
            http.setReadTimeout(READ_TIMEOUT);
            http.setRequestProperty("Accept-Charset", "iso-8859-5");
            http.setRequestProperty("User-Agent", "Mozilla/5.0 (Linux; X11)");
            http.setRequestProperty("Content-Type", "application/x-www-form-urlencoded");
            http.setRequestProperty("Upgrade", "HTTP/2.0, SHTTP/1.3, IRC/6.9, RTA/x11");

            if (data == null) {

                http.setRequestMethod("GET");
                http.setDoInput(true);

                if (mCookie != null)
                    http.setRequestProperty("Cookie", mCookie);

            } else {

                http.setRequestMethod("POST");
                http.setDoInput(true);
                http.setDoOutput(true);
                http.setRequestProperty("Cache-Control", "no-cache");
                http.setUseCaches(false);
                http.setChunkedStreamingMode(0);

                if (mCookie != null && mCookie.trim().length() > 0)
                    http.setRequestProperty("Cookie", mCookie);

                OutputStream os = http.getOutputStream();

                DataOutputStream out = new DataOutputStream(os);
                out.writeBytes(data);
                out.flush();
                out.close();
            }

            // Check http response code
            httpsResponseCode = http.getResponseCode();
            BufferedReader in = null;
            if (httpsResponseCode == 200) {
                getCookie(http);
                in = new BufferedReader(new InputStreamReader(http.getInputStream()));
            } else {
                in = new BufferedReader(new InputStreamReader(http.getErrorStream()));
            }

            String temp = in.readLine();
            while (temp != null) {
                if (result != null)
                    result += temp;
                else
                    result = temp;
                temp = in.readLine();
            }

            in.close();
            http.disconnect();

        } catch (Exception e) {
            e.printStackTrace();
        }
        return result;
    }

    /**
     * Get Cookie
     *
     */
    private static void getCookie(HttpURLConnection http) {
        String cookieVal = null;
        String key = null;
        mCookie = "";
        for (int i = 1; (key = http.getHeaderFieldKey(i)) != null; i++) {
            if (key.equalsIgnoreCase("set-cookie")) {
                cookieVal = http.getHeaderField(i);
                cookieVal = cookieVal.substring(0, cookieVal.indexOf(";"));
                mCookie = mCookie + cookieVal + ";";
            }
        }
    }
}