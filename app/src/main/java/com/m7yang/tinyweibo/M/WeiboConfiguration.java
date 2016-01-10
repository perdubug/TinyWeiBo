package com.m7yang.tinyweibo.M;

/**
 * Created by m7yang on 15-12-29.
 *
 * WeiBo account information,which is necessary for accessing Sina WeiBo REST APIs *
 */
public class WeiboConfiguration {
    public static String SINA_CONSUMER_KEY    = "3071891674";
    public static String SINA_CONSUMER_SECRET = "42407c821f8bf714fb71b274a840993c";

    public static String SINA_REDIRECT_URI = "http://www.weibo.com";

    public static String SINA_OAUTH = "https://api.weibo.com/oauth2/authorize?"
            + "client_id=" + SINA_CONSUMER_KEY
            + "&response_type=code"
            + "&redirect_uri="+SINA_REDIRECT_URI
            + "&display=mobile";

    public static String SINA_ACCESS_TOKEN = "https://api.weibo.com/oauth2/access_token?"
            + "client_id=" + SINA_CONSUMER_KEY
            + "&client_secret=" + SINA_CONSUMER_SECRET
            + "&grant_type=authorization_code"
            + "&redirect_uri="+SINA_REDIRECT_URI
            + "&code=";

    public static String ACCESS_TOKEN = "";
}
