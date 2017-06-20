package com.app.astro.astroassignment.adapter;

import android.content.Context;
import android.content.Intent;
import android.support.v4.content.LocalBroadcastManager;
import android.support.v7.widget.RecyclerView;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.android.volley.toolbox.ImageLoader;
import com.android.volley.toolbox.NetworkImageView;
import com.app.astro.astroassignment.R;
import com.app.astro.astroassignment.data.ChannelModel;
import com.app.astro.astroassignment.database.DataBaseHelper;
import com.app.astro.astroassignment.fragment.HomeChannelFragment;
import com.app.astro.astroassignment.network.AppController;
import com.app.astro.astroassignment.utils.SharedPrefsUtil;

import java.util.ArrayList;

/**
 * Created by suman on 6/16/2017.
 */
public class HomeChannelAdapter  extends RecyclerView.Adapter<RecyclerView.ViewHolder>{
    ArrayList<ChannelModel> arrayList;
    Context mContext;
    ImageLoader imageLoader = AppController.getInstance().getImageLoader();

    public int getmSortingOrder() {
        return mSortingOrder;
    }

    public void setmSortingOrder(int mSortingOrder) {
        this.mSortingOrder = mSortingOrder;
    }

    int mSortingOrder =0;

    public HomeChannelAdapter(ArrayList<ChannelModel> arrayList, Context context){
        this.arrayList = arrayList;
        mContext = context;
    }

    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        LayoutInflater inflater = LayoutInflater.from(mContext);
          View view      =inflater.inflate(R.layout.home_channel_list_row,null);
        RecyclerView.ViewHolder holder = new ChannelRecyclerViewHolder(view);
        return holder;
    }

    @Override
    public void onBindViewHolder(RecyclerView.ViewHolder holder, final int position) {
        final ChannelRecyclerViewHolder hold = (ChannelRecyclerViewHolder)holder;
        final ChannelModel obj = arrayList.get(position);
        if (imageLoader == null)
            imageLoader = AppController.getInstance().getImageLoader();
        hold.tvChannelName.setText(obj.getChannelTitle());
        hold.tvChannelNumber.setText(obj.getChannelNumber());
        hold.networkImageViewChannel.setDefaultImageResId(R.drawable.icon);
        hold.networkImageViewChannel.setImageUrl(obj.getmThumbUrl(), imageLoader);
        if(obj.isFavourite())
            hold.favouriteIcon.setImageResource(R.drawable.favourite);
        else
            hold.favouriteIcon.setImageResource(R.drawable.normal);
        hold.favouriteIcon.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if(TextUtils.isEmpty(SharedPrefsUtil.getUserId())){
                    Intent intent = new Intent("start.socialLogin.action");
                    intent.putExtra("channleId",obj.getChannelId());
                    LocalBroadcastManager.getInstance(mContext).sendBroadcast(intent);
                    return;
                }
                if(obj.isFavourite())
                {
                    Intent intent = new Intent("start.remove.favourite");
                    intent.putExtra("channelId",obj.getChannelId());
                    LocalBroadcastManager.getInstance(mContext).sendBroadcast(intent);
                    DataBaseHelper.removeDataRowFromTable(obj.getChannelId());
                    hold.favouriteIcon.setImageResource(R.drawable.normal);
                    obj.setFavourite(false);
                    if(mSortingOrder== HomeChannelFragment.SORTING_ORDER_FAVOURITE)
                    arrayList.remove(position);
                    updateList(arrayList);
                }
                else
                {
                    DataBaseHelper.insertChannelDataIntoDB(obj);
                    hold.favouriteIcon.setImageResource(R.drawable.favourite);
                    obj.setFavourite(true);

                }


            }
        });
    }

    @Override
    public int getItemCount() {
        return arrayList.size();
    }

    public void updateList(ArrayList<ChannelModel> list){
        this.arrayList = list;
        notifyDataSetChanged();
    }

    public static class ChannelRecyclerViewHolder extends RecyclerView.ViewHolder {
        TextView tvChannelName;
        TextView tvChannelNumber;
        NetworkImageView networkImageViewChannel;
        ImageView favouriteIcon;

        public ChannelRecyclerViewHolder(View itemView) {
            super(itemView);
            tvChannelName = (TextView) itemView.findViewById(R.id.title);
            networkImageViewChannel = (NetworkImageView) itemView.findViewById(R.id.thumbnail);
            tvChannelNumber = (TextView) itemView.findViewById(R.id.channel_num);
            favouriteIcon = (ImageView)itemView.findViewById(R.id.iv_favouriteicon);
        }
    }
}
