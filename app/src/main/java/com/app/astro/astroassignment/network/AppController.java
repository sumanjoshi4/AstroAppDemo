package com.app.astro.astroassignment.network;

import android.app.Application;
import android.database.sqlite.SQLiteDatabase;
import android.text.TextUtils;

import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.toolbox.ImageLoader;
import com.android.volley.toolbox.JsonObjectRequest;
import com.android.volley.toolbox.Volley;
import com.app.astro.astroassignment.database.DataBaseHelper;
import com.app.astro.astroassignment.utils.LruBitmapCache;
import com.app.astro.astroassignment.utils.NetworkUtils;
import com.app.astro.astroassignment.utils.SharedPrefsUtil;
import com.facebook.FacebookSdk;
import com.facebook.appevents.AppEventsLogger;
import com.google.firebase.database.FirebaseDatabase;

/**
 * Created by B0096643 on 6/16/2017.
 */
public class AppController extends Application {

    public static final String TAG = AppController.class
            .getSimpleName();

    private RequestQueue mRequestQueue;
    private ImageLoader mImageLoader;
    private static DataBaseHelper sDbHelper             = null;
    private static SQLiteDatabase sDb                   = null;

    private static AppController mInstance;

    @Override
    public void onCreate() {
        super.onCreate();
        mInstance = this;
        SharedPrefsUtil.init(getApplicationContext());
        FacebookSdk.sdkInitialize(getApplicationContext());
        AppEventsLogger.activateApp(this);
        sDbHelper = new DataBaseHelper(this);
        NetworkUtils.init(getApplicationContext());
        FirebaseDatabase.getInstance().setPersistenceEnabled(true);
    }

    public static synchronized AppController getInstance() {
        return mInstance;
    }

    public RequestQueue getRequestQueue() {
        if (mRequestQueue == null) {
            mRequestQueue = Volley.newRequestQueue(getApplicationContext());
        }

        return mRequestQueue;
    }

    public ImageLoader getImageLoader() {
        getRequestQueue();
        if (mImageLoader == null) {
            mImageLoader = new ImageLoader(this.mRequestQueue,
                    new LruBitmapCache());
        }
        return this.mImageLoader;
    }

    public <T> void addToRequestQueue(Request<T> req, String tag) {
        // set the default tag if tag is empty
        req.setTag(TextUtils.isEmpty(tag) ? TAG : tag);
        getRequestQueue().add(req);
    }

    public <T> void addToRequestQueue(Request<T> req) {
        req.setTag(TAG);
        getRequestQueue().add(req);
    }

    public void cancelPendingRequests(Object tag) {
        if (mRequestQueue != null) {
            mRequestQueue.cancelAll(tag);
        }
    }

    public void createJsonRequest(String url,String body,BaseVolleyResponseListener baseVolleyResponseListener){
        JsonObjectRequest req = new JsonObjectRequest(Request.Method.GET,url,null,baseVolleyResponseListener,baseVolleyResponseListener);
        req.setShouldCache(true);
        getRequestQueue().add(req);
    }

    public static String getBaseUrl(){
        return "https://ams-api.astro.com.my/";
    }

    public static synchronized SQLiteDatabase getDatabase() {
        if (sDb != null) {
            return sDb;
        }
        sDbHelper.openDataBase();
        sDb = sDbHelper.getWritableDatabase();
        return sDb;
    }

    public static synchronized void closeDatabase() {
        if (sDb != null) {
            sDb.close();
        }
        sDb = null;
        if (sDbHelper != null) {
            sDbHelper.close();
        }
    }
}
