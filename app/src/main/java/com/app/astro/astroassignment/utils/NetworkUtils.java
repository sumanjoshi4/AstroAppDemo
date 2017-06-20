package com.app.astro.astroassignment.utils;

import android.content.Context;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.telephony.TelephonyManager;

import com.android.volley.DefaultRetryPolicy;
import com.android.volley.RetryPolicy;

import java.util.concurrent.TimeUnit;
/**
 * Created by B0096643 on 6/16/2017.
 */
public class NetworkUtils {


    private static Context sContext;

    public static void init(Context context) {
        sContext = context;
    }

    public static boolean isConnected() {
        ConnectivityManager cm = (ConnectivityManager) sContext.getSystemService(Context.CONNECTIVITY_SERVICE);

        NetworkInfo activeNetwork = cm.getActiveNetworkInfo();
        boolean isConnected = activeNetwork != null && activeNetwork.isConnectedOrConnecting();

        return isConnected;
    }

    public static boolean isOnline() {
        return isConnected();

    }

}
