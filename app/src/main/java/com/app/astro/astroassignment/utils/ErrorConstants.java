package com.app.astro.astroassignment.utils;

import android.content.Context;
import android.content.res.Resources;

import com.app.astro.astroassignment.R;

/**
 * Created by B0096643 on 6/19/2017.
 */
public class ErrorConstants {

    public static final String CUSTOM_NO_CONNECTION_ERROR      = "1001";
    public static final String CUSTOM_NETWORK_ERROR            = "1002";
    public static final String CUSTOM_CONNECTION_TIMEOUT_ERROR = "1003";
    public static final String CUSTOM_NOT_ONLINE_ERROR         = "15435";

    public static String getErrorMessage(Context context, String code) {
        return getErrorMessage(context, code, false);
    }

    public static String getErrorMessage(Context context, String code, boolean isTransactional) {
        Resources resources = context.getResources();
        String message = resources.getString(R.string.error_network_default);
        switch (code) {
            case CUSTOM_NETWORK_ERROR:
                message = resources.getString(R.string.error_network_default);
                break;
            case CUSTOM_CONNECTION_TIMEOUT_ERROR:
                message = resources.getString(R.string.error_connection_timeout);
                break;
            case CUSTOM_NO_CONNECTION_ERROR:
                message = resources.getString(R.string.error_no_network);
                break;
        }
        return message;
    }

}
