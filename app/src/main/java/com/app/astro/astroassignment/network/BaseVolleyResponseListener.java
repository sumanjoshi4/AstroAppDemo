package com.app.astro.astroassignment.network;

import com.android.volley.Response;
import com.android.volley.VolleyError;

/**
 * Created by suman on 6/16/2017.
 */
public class BaseVolleyResponseListener<T> implements Response.Listener<T>, Response.ErrorListener {

    private static final String LOG_TAG = "BASE_VOLLEY_RESPONSE_LISTENER";

    @Override
    public void onResponse(T response) {

    }

    @Override
    public void onErrorResponse(VolleyError error) {
        // Handle your error types accordingly
        // For Timeout & No connection error, you can show 'retry' button.
        // For AuthFailure, you can re login with user credentials.
        // For ServerError 5xx, you can do retry or handle accordingly.
    }
}
