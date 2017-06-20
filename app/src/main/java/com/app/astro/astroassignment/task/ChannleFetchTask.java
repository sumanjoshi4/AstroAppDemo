package com.app.astro.astroassignment.task;

import com.android.volley.VolleyError;
import com.app.astro.astroassignment.network.AppController;
import com.app.astro.astroassignment.network.BaseVolleyResponseListener;
import com.app.astro.astroassignment.network.BusProvider;
import com.app.astro.astroassignment.response.NotOnlineError;
import com.app.astro.astroassignment.response.AstroResponse;
import com.app.astro.astroassignment.utils.NetworkUtils;

import org.json.JSONObject;

import java.util.concurrent.Callable;

/**
 * Created by Suman on 6/16/2017.
 */
public class ChannleFetchTask implements Callable {

    private static String BASE_URL = AppController.getBaseUrl();

   private BaseVolleyResponseListener<JSONObject> mListner = new BaseVolleyResponseListener<JSONObject>(){

       @Override
       public void onResponse(JSONObject response) {
           BusProvider.getInstance().post(new AstroResponse(response));
       }

       @Override
       public void onErrorResponse(VolleyError error) {
           BusProvider.getInstance().post(new AstroResponse(error));
       }
   };

    @Override
    public Object call() throws Exception {
        if(!NetworkUtils.isOnline()){
            mListner.onErrorResponse(new NotOnlineError());
        }
        String url = BASE_URL+" /ams/v3/getChannels";
        AppController.getInstance().createJsonRequest(url,null,mListner);
        return null;
    }
}
