package com.app.astro.astroassignment.task;

import com.android.volley.VolleyError;
import com.app.astro.astroassignment.database.FireBaseDataBaseHelper;
import com.app.astro.astroassignment.network.AppController;
import com.app.astro.astroassignment.network.BaseVolleyResponseListener;
import com.app.astro.astroassignment.network.BusProvider;
import com.app.astro.astroassignment.response.LogoutResponse;
import com.app.astro.astroassignment.response.NotOnlineError;
import com.app.astro.astroassignment.utils.NetworkUtils;
import com.app.astro.astroassignment.utils.Utility;
import com.facebook.login.LoginManager;

import org.json.JSONObject;

import java.util.concurrent.Callable;

/**
 * Created by B0096643 on 6/19/2017.
 */
public class LogoutTask implements Callable{


    @Override
    public Object call() throws Exception {
        if(!NetworkUtils.isOnline()){
            BusProvider.getInstance().post(new NotOnlineError());
        }
        Utility.doLogout();
        LoginManager.getInstance().logOut();
        LogoutResponse resp = new LogoutResponse();
        resp.setSuccesful(true);
        BusProvider.getInstance().post(resp);
        return null;
    }
}
