package com.app.astro.astroassignment.data;


import java.util.Comparator;

/**
 * Created by B0096643 on 6/16/2017.
 * Model class for data communication within app
 */
public class ChannelModel {
    public String getChannelNumber() {
        return channelNumber;
    }

    public void setChannelNumber(String channelNumber) {
        this.channelNumber = channelNumber;
    }

    String channelNumber;

    int channelId;

    public boolean isFavourite() {
        return isFavourite;
    }

    public void setFavourite(boolean favourite) {
        isFavourite = favourite;
    }

    boolean isFavourite;

    public String getChannelTitle() {
        return channelTitle;
    }

    public void setChannelTitle(String channelTitle) {
        this.channelTitle = channelTitle;
    }

    public int getChannelId() {
        return channelId;
    }

    public void setChannelId(int channelId) {
        this.channelId = channelId;
    }

    String channelTitle;

    public String getmThumbUrl() {
        return mThumbUrl;
    }

    public void setmThumbUrl(String mThumbUrl) {
        this.mThumbUrl = mThumbUrl;
    }

    String mThumbUrl;

    public static Comparator<ChannelModel> getChannleNumberSort(){
        return new Comparator<ChannelModel>() {
            @Override
            public int compare(ChannelModel channelModel, ChannelModel t1) {
                return channelModel.channelNumber.compareTo(t1.channelNumber);
            }
        };
    }

    public static Comparator<ChannelModel> getChannleNameSort(){
        return new Comparator<ChannelModel>() {
            @Override
            public int compare(ChannelModel channelModel, ChannelModel t1) {
                return channelModel.getChannelTitle().compareTo(t1.channelTitle);
            }
        };
    }

    public static Comparator<ChannelModel> getChannleIdSort(){
        return new Comparator<ChannelModel>() {
            @Override
            public int compare(ChannelModel channelModel, ChannelModel t1) {
                return channelModel.getChannelId()-t1.channelId;
            }
        };
    }

}
