package com.app.astro.astroassignment.utils;

import android.content.Context;
import android.content.SharedPreferences;
import android.preference.PreferenceManager;

import com.app.astro.astroassignment.data.ChannelModel;
import com.app.astro.astroassignment.database.DataBaseHelper;

import java.util.HashMap;
import java.util.HashSet;
import java.util.Set;

/**
 * Created by B0096643 on 6/16/2017.
 */
public class SharedPrefsUtil {

    private static SharedPreferences mSharedPreferences;
    private static SharedPreferences.Editor mPreferenceEditor;
    private static Set<String> setFavouriteChannel= new HashSet<>();

    private static final String DB_EXISTS                       = "dbExists";
    private static final String FAVOURITE_CHANNEL_LIST          = "favouriteChannleList";
    private static final String USER_ID                       = "USERID";


    public static void init(Context context){
        setFavouriteChannel.clear();
        mSharedPreferences = PreferenceManager.getDefaultSharedPreferences(context);
        mPreferenceEditor = mSharedPreferences.edit();
    }

    public static void setDbExists(boolean exists) {
        updatePreferences(DB_EXISTS, exists);
    }

    private static void updatePreferences(String key, boolean value) {
        mPreferenceEditor.putBoolean(key, value);
        mPreferenceEditor.apply();
    }

    public static boolean doesDbExists() {
        return mSharedPreferences.getBoolean(DB_EXISTS, false);
    }

    private static void saveFavouriteChannelList(){
        mPreferenceEditor.putStringSet(FAVOURITE_CHANNEL_LIST,setFavouriteChannel);
        mPreferenceEditor.commit();
    }

    public static void markChannelFavourite(int channelId){
        setFavouriteChannel.add(String.valueOf(channelId));
        saveFavouriteChannelList();
    }

    public static void removeFavourite(int channelNumber){
        setFavouriteChannel.remove(String.valueOf(channelNumber));
        saveFavouriteChannelList();
    }

    public static HashSet<String> getFavouriteList(){
        return (HashSet<String>) mSharedPreferences.getStringSet(FAVOURITE_CHANNEL_LIST,null);
    }

    public static void saveUserId(String userId){
        mPreferenceEditor.putString(USER_ID,userId);
        mPreferenceEditor.commit();
    }

    public static String getUserId(){
        return mSharedPreferences.getString(USER_ID,"");
    }

}
