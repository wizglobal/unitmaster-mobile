package com.wizglobal.utils;

import android.content.Context;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.util.Log;

/**
 * Created by Mathew.Godia on 4/15/14.
 */
public class Config {

    public static final String DebugTag = "WIZ_GLOBAL";
//    public static final String serverUrl = "http://192.168.137.1:8080/WizGlobal-war/";
    public static final String serverUrl = "http://192.168.43.225:7001/WizGlobal-war/";


    public static String getDebugTag() {
        return DebugTag;
    }

    public static String getServerUrl(String endpoint) {
        return serverUrl + endpoint;
    }

    //Check for Connection
    public static boolean isConnected(Context context) {
        boolean res = false;
        //Check for connectivity
        ConnectivityManager connMgr = (ConnectivityManager) context.getSystemService(Context.CONNECTIVITY_SERVICE);
        NetworkInfo networkInfo = connMgr.getActiveNetworkInfo();
        if (networkInfo != null && networkInfo.isConnected()) {
            res = true;
        }
        Log.d(Config.getDebugTag(), "IS_Connected: " + res);
        return res;
    }

    public static String getAppVersion(Context context) {
        String version = "";
        try {
            PackageInfo pInfo = context.getPackageManager().getPackageInfo(context.getPackageName(), 0);
            version = pInfo.versionName;
        } catch (PackageManager.NameNotFoundException e) {
            e.printStackTrace();
        }
        return version;
    }
}
