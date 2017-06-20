package com.app.astro.astroassignment.response;

import com.android.volley.NoConnectionError;
import com.app.astro.astroassignment.data.ChannelModel;
import com.app.astro.astroassignment.network.AppController;
import com.app.astro.astroassignment.utils.ErrorConstants;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.Serializable;
import java.util.ArrayList;

/**
 * Created by B0096643 on 6/16/2017.
 */
public class AstroResponse implements Serializable {

    int mCode;
    protected String          mResponse             = "";
    protected String              mErrCode              = "0";
    ArrayList<ChannelModel> arrayList = new ArrayList<>();
    String mMessage;
    protected static final String DEFAULT_ERROR_MESSAGE = "Something went wrong.\nPlease try again later.";


    public boolean isSuccess() {
        return isSuccess;
    }

    public ArrayList<ChannelModel> getArrayList() {
        return arrayList;
    }

    boolean isSuccess;
    public AstroResponse(JSONObject response){
        mResponse = response.toString();
        try {
            JSONObject jsonObject = new JSONObject(mResponse);
            parseJsonResponse(jsonObject);
            mCode = jsonObject.getInt("responseCode");
            if(mCode==200)
                isSuccess = true;
            else
                isSuccess = false;
        } catch (JSONException e) {
            e.printStackTrace();
        }
    }

   public AstroResponse(Exception e){
       isSuccess = false;
       mMessage = getErrorMessage(e);
   }

    public String getErrorMessage(Exception exception) {
        String message = DEFAULT_ERROR_MESSAGE;
        if (exception instanceof NoConnectionError) {
            mErrCode = ErrorConstants.CUSTOM_NO_CONNECTION_ERROR;
            message = ErrorConstants.getErrorMessage(AppController.getInstance(), mErrCode);
        }else if (exception instanceof ConnectionTimeoutError) {
            mErrCode = ErrorConstants.CUSTOM_CONNECTION_TIMEOUT_ERROR;
            message = ErrorConstants.getErrorMessage(AppController.getInstance(), mErrCode);
        }else if (exception instanceof NotOnlineError) {
            mErrCode = ErrorConstants.CUSTOM_NOT_ONLINE_ERROR;
            message = ErrorConstants.getErrorMessage(AppController.getInstance(), mErrCode);
        }
        return message;
    }

    private void parseJsonResponse(JSONObject obj){
        JSONArray arr = obj.optJSONArray("channel");
        for(int count=0;count<arr.length();count++){
           JSONObject jsonObj =  arr.optJSONObject(count);
            ChannelModel modleObj = new ChannelModel();
            modleObj.setChannelId(jsonObj.optInt("channelId"));
            modleObj.setChannelNumber(jsonObj.optString("channelStbNumber"));
            modleObj.setChannelTitle(jsonObj.optString("channelTitle"));
            modleObj.setmThumbUrl(jsonObj.optJSONArray("channelExtRef").optJSONObject(5).optString("value"));
            arrayList.add(modleObj);
        }
    }


    public String getmMessage() {
        return mMessage;
    }

    public void setmMessage(String mMessage) {
        this.mMessage = mMessage;
    }

}
