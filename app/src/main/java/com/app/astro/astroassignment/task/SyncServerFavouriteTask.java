package com.app.astro.astroassignment.task;

import com.app.astro.astroassignment.data.ChannelModel;
import com.app.astro.astroassignment.database.DataBaseHelper;
import com.app.astro.astroassignment.database.FireBaseDataBaseHelper;
import com.app.astro.astroassignment.network.BusProvider;
import com.app.astro.astroassignment.response.LogoutResponse;
import com.app.astro.astroassignment.response.ServerSyncResponse;
import com.app.astro.astroassignment.utils.Utility;

import java.util.concurrent.Callable;

/**
 * Created by B0096643 on 6/20/2017.
 */
public class SyncServerFavouriteTask implements Callable,FireBaseDataBaseHelper.IFetchFireBaseData{

    ChannelModel channelModel;
    public SyncServerFavouriteTask(ChannelModel channelModel)
    {
        this.channelModel = channelModel;
    }

    @Override
    public Object call() throws Exception {
        if(channelModel!=null)
            DataBaseHelper.insertChannelDataIntoDB(channelModel);
        Utility.syncServerFavourite(this);

        return null;
    }

    @Override
    public void onDataFetchDone() {
        ServerSyncResponse resp = new ServerSyncResponse();
        resp.setSuccesful(true);
        BusProvider.getInstance().post(resp);
    }
}
