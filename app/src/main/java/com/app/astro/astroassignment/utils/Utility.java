package com.app.astro.astroassignment.utils;

import com.app.astro.astroassignment.database.DataBaseHelper;
import com.app.astro.astroassignment.database.FireBaseDataBaseHelper;

/**
 * Created by B0096643 on 6/19/2017.
 */
public class Utility {

    public static void doLogout(){
        FireBaseDataBaseHelper.getInstance().syncFavouriteDateToCloud();
        SharedPrefsUtil.saveUserId(null);
        DataBaseHelper.deleteTable();
    }

    public static void syncServerFavourite(FireBaseDataBaseHelper.IFetchFireBaseData firebaseListner){
        FireBaseDataBaseHelper.getInstance().updateLocalDBFromServer(firebaseListner);
    }
}
