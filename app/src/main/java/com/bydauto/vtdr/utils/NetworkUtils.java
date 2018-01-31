package com.bydauto.vtdr.utils;

import android.content.Context;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;

/**
 * Created by byd_tw on 2018/1/29.
 */

public class NetworkUtils {
    public static int getAPNType(Context context) {
        int netType = 0;
        ConnectivityManager connectivityManager = (ConnectivityManager) context.getSystemService(Context.CONNECTIVITY_SERVICE);
        NetworkInfo networkInfo = connectivityManager.getActiveNetworkInfo();
        if (networkInfo == null) {
            return netType;
        }
        switch (networkInfo.getType()) {
            case ConnectivityManager.TYPE_WIFI:
                return 1;
            case ConnectivityManager.TYPE_ETHERNET:
                return 9;
            default:
                break;
        }

        return netType;
    }
}
