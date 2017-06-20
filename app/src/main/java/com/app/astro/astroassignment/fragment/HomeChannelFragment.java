package com.app.astro.astroassignment.fragment;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v4.content.LocalBroadcastManager;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.PopupMenu;
import android.support.v7.widget.RecyclerView;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.app.astro.astroassignment.adapter.HomeChannelAdapter;
import com.app.astro.astroassignment.R;
import com.app.astro.astroassignment.data.ChannelModel;
import com.app.astro.astroassignment.database.DataBaseHelper;
import com.app.astro.astroassignment.network.BusProvider;
import com.app.astro.astroassignment.response.AstroResponse;
import com.app.astro.astroassignment.response.ServerSyncResponse;
import com.app.astro.astroassignment.task.ChannleFetchTask;
import com.app.astro.astroassignment.task.SyncServerFavouriteTask;
import com.app.astro.astroassignment.utils.SharedPrefsUtil;
import com.app.astro.astroassignment.utils.SimpleDividerItemDecoration;
import com.app.astro.astroassignment.utils.ThreadUtils;
import com.squareup.otto.Subscribe;

import java.util.ArrayList;
import java.util.Collections;

/**
 * Created by Suman on 6/16/2017.
 */
public class HomeChannelFragment extends Fragment implements View.OnClickListener{

     private View mRootView;
     private RecyclerView mRecyclerView;
     private RelativeLayout mLoadingLayout;
     private HomeChannelAdapter adapter;
     private LinearLayout llSortingcontainer,llNoDataContainer;
     private TextView tvSortByChanl,tvNoData;
    private Button mBtnNoData;
    ArrayList<ChannelModel> channelList = new ArrayList<>();
    ArrayList<ChannelModel> channelListOriginal;
    PopupMenu popupMenu;
    int sortingOrder =0;

    public static final int SORTING_ORDER_DEFAULT =0;
    public static final int SORTING_ORDER_NAME =1;
    public static final int SORTING_ORDER_NUMBER =2;
    public static final int SORTING_ORDER_FAVOURITE =3;

    //Return Instance of Fragment
    public static HomeChannelFragment newInstance(){
        HomeChannelFragment frag = new HomeChannelFragment();
        return frag;
    }

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        mRootView = inflater.inflate(R.layout.frag_home,container,false);
        LocalBroadcastManager.getInstance(getActivity().getApplicationContext()).registerReceiver(receiverUpdateData,
                new IntentFilter("start.remove.favourite"));
        LocalBroadcastManager.getInstance(getActivity().getApplicationContext()).registerReceiver(receiverLoginComplete,
                new IntentFilter("start.socialLogin.finish.action"));
        BusProvider.getInstance().register(this);
        findViews();
        bindViews();
       fetchData();
        return mRootView;
    }

    private void fetchData(){
        llNoDataContainer.setVisibility(View.GONE);
        mLoadingLayout.setVisibility(View.VISIBLE);
        ThreadUtils.getDefaultExecutorService().submit(new ChannleFetchTask());
    }

    private void findViews() {
        mRecyclerView = (RecyclerView) mRootView.findViewById(R.id.listview_chnnel);
        mRecyclerView.addItemDecoration(new SimpleDividerItemDecoration(getActivity().getApplicationContext()));
        mLoadingLayout = (RelativeLayout) mRootView.findViewById(R.id.rl_loading);
        llSortingcontainer = (LinearLayout)mRootView.findViewById(R.id.ll_sortcontainer);
        tvSortByChanl = (TextView)mRootView.findViewById(R.id.tv_sort_chnl);
        llNoDataContainer = (LinearLayout)mRootView.findViewById(R.id.ll_no_data);
        mBtnNoData = (Button)mRootView.findViewById(R.id.btn_nodata);
        tvNoData  =(TextView)mRootView.findViewById(R.id.tv_no_data) ;
        mBtnNoData.setOnClickListener(this);
        tvSortByChanl.setOnClickListener(this);
    }

    private void bindViews() {
        mRecyclerView.setLayoutManager(new LinearLayoutManager(getActivity()));
    }

    @Subscribe
    public void onFetchChannelList(AstroResponse response){
        mLoadingLayout.setVisibility(View.GONE);
        if(response.isSuccess()){
            llSortingcontainer.setVisibility(View.VISIBLE);
            initPopupMenu();
            //use data
            channelListOriginal = response.getArrayList();
            channelList.addAll(channelListOriginal);// = new ArrayList<>(channelListOriginal);
            ArrayList<ChannelModel> list = channelList;
            if(list.size()>0) {
                llSortingcontainer.setVisibility(View.VISIBLE);
                adapter = new HomeChannelAdapter(list, getActivity().getApplicationContext());
                mRecyclerView.setAdapter(adapter);
                checkDefaultMenu();
            }
            else
            {
                llSortingcontainer.setVisibility(View.GONE);
                handleDataFetchError(getResources().getString(R.string.error_no_data));
            }
        }else
        {
            llSortingcontainer.setVisibility(View.GONE);
            handleDataFetchError(response.getmMessage());
        }
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        BusProvider.getInstance().unregister(this);
        LocalBroadcastManager.getInstance(getActivity().getApplicationContext()).unregisterReceiver(receiverUpdateData);
        LocalBroadcastManager.getInstance(getActivity().getApplicationContext()).unregisterReceiver(receiverLoginComplete);
    }

    @Override
    public void onClick(View view) {
        switch (view.getId())
        {
            case R.id.tv_sort_chnl:
                showPopUpMenu();
                break;
            case R.id.btn_nodata:
                fetchData();
                break;
        }
    }

    private void initPopupMenu(){
        popupMenu = new PopupMenu(getActivity(), tvSortByChanl);
        popupMenu.getMenuInflater().inflate(R.menu.popup_menu,popupMenu.getMenu());
    }

    private void showPopUpMenu(){
        final Menu menuItems = popupMenu.getMenu();
        if(!TextUtils.isEmpty(SharedPrefsUtil.getUserId()))
            menuItems.findItem(R.id.menu_channel_favrt).setVisible(true);
        else
            menuItems.findItem(R.id.menu_channel_favrt).setVisible(false);
        popupMenu.setOnMenuItemClickListener(new PopupMenu.OnMenuItemClickListener() {
            @Override
            public boolean onMenuItemClick(MenuItem item) {
                switch (item.getItemId()){
                    case R.id.menu_default:
                        sortingOrder =SORTING_ORDER_DEFAULT;
                        item.setChecked(!item.isChecked());
                        menuItems.findItem(R.id.menu_channel_number).setChecked(false);
                        menuItems.findItem(R.id.menu_channel_favrt).setChecked(false);
                        menuItems.findItem(R.id.menu_channel_name).setChecked(false);
                        break;
                    case R.id.menu_channel_favrt:
                        item.setChecked(!item.isChecked());
                        menuItems.findItem(R.id.menu_channel_number).setChecked(false);
                        menuItems.findItem(R.id.menu_default).setChecked(false);
                        menuItems.findItem(R.id.menu_channel_name).setChecked(false);
                        sortingOrder =SORTING_ORDER_FAVOURITE;
                        break;
                    case R.id.menu_channel_name: {
                        item.setChecked(!item.isChecked());
                        menuItems.findItem(R.id.menu_channel_number).setChecked(false);
                        menuItems.findItem(R.id.menu_channel_favrt).setChecked(false);
                        menuItems.findItem(R.id.menu_default).setChecked(false);
                        sortingOrder =SORTING_ORDER_NAME;
                    }
                    break;
                    case R.id.menu_channel_number:{
                        item.setChecked(!item.isChecked());
                        menuItems.findItem(R.id.menu_default).setChecked(false);
                        menuItems.findItem(R.id.menu_channel_favrt).setChecked(false);
                        menuItems.findItem(R.id.menu_channel_name).setChecked(false);
                        sortingOrder =SORTING_ORDER_NUMBER;
                    }
                    break;
                }
                setAdapterDataBasisOnSortingOrder(sortingOrder);
                return true;
            }
        });
        popupMenu.show();
    }


    private void setAdapterDataBasisOnSortingOrder(int order){
        ArrayList<ChannelModel> list = channelList;
        switch (order){
            case SORTING_ORDER_DEFAULT:
                adapter.setmSortingOrder(SORTING_ORDER_DEFAULT);
                adapter.updateList(channelListOriginal);
                break;
            case SORTING_ORDER_FAVOURITE:
                adapter = new HomeChannelAdapter(DataBaseHelper.getFavouriteList(),getActivity().getApplicationContext());
                adapter.setmSortingOrder(SORTING_ORDER_FAVOURITE);
                mRecyclerView.setAdapter(adapter);
                break;
            case SORTING_ORDER_NAME:
                adapter.setmSortingOrder(SORTING_ORDER_NAME);
                Collections.sort(list, ChannelModel.getChannleNameSort());
                adapter.updateList(list);
                break;
            case SORTING_ORDER_NUMBER:
                adapter.setmSortingOrder(SORTING_ORDER_NUMBER);
                Collections.sort(list, ChannelModel.getChannleNumberSort());
                adapter.updateList(list);
                break;

        }
    }

    public void updateFavouriteChannelList(){
        ArrayList<ChannelModel> fvrtList = DataBaseHelper.getFavouriteList();
        ArrayList<ChannelModel> temp = channelList;
        Collections.sort(temp,ChannelModel.getChannleIdSort());
        for(int count =0;count<fvrtList.size();count++){
            for(int j = 0; j< channelList.size(); j++){
               if(fvrtList.get(count).getChannelId()== channelList.get(j).getChannelId())
               {
                   channelList.get(j).setFavourite(true);
                   break;
               }
            }
        }

    }

    private void updateList(int channelId,boolean isFavourite){
            for(int j = 0; j< channelList.size(); j++){
                if(channelId== channelList.get(j).getChannelId())
                {
                    channelList.get(j).setFavourite(isFavourite);
                    break;
                }

        }
    }

    BroadcastReceiver receiverUpdateData = new BroadcastReceiver() {
        @Override
        public void onReceive(Context context, Intent intent) {
            int channelId = intent.getIntExtra("channelId",0);
            updateList(channelId,false);

        }
    };

    BroadcastReceiver receiverLoginComplete = new BroadcastReceiver() {
        @Override
        public void onReceive(Context context, Intent intent) {
            getActivity().invalidateOptionsMenu();
            mLoadingLayout.setVisibility(View.VISIBLE);
            mRecyclerView.setVisibility(View.GONE);
            int channelId = intent.getIntExtra("channleId",0);
            ChannelModel obj = null;
            for(int i=0;i<channelList.size();i++){
                if(channelId==channelList.get(i).getChannelId())
                {
                    obj = channelList.get(i);
                    break;
                }
            }
            llSortingcontainer.setVisibility(View.GONE);
            ThreadUtils.getDefaultExecutorService().submit(new SyncServerFavouriteTask(obj));
        }
    };

    private void handleDataFetchError(String message){
      llNoDataContainer.setVisibility(View.VISIBLE);
        llSortingcontainer.setVisibility(View.GONE);
        tvNoData.setText(message);
    }


    public void intListViewAfterLogout(){
        removeAllFavourites();
        adapter.updateList(channelListOriginal);
        checkDefaultMenu();
    }

    private void checkDefaultMenu(){
        popupMenu.getMenu().getItem(0).setChecked(true);
        for(int count=1;count<=3;count++)
            popupMenu.getMenu().getItem(count).setChecked(false);
    }

    public void removeAllFavourites(){
        for(int count=0;count<channelListOriginal.size();count++)
            channelListOriginal.get(count).setFavourite(false);
    }

    @Subscribe
    public void onDataFetchFromServer(ServerSyncResponse response)
    { mLoadingLayout.setVisibility(View.GONE);
        if(response.isSuccesful())
        {
            llSortingcontainer.setVisibility(View.VISIBLE);
            updateFavouriteChannelList();
            setAdapterDataBasisOnSortingOrder(sortingOrder);
            mRecyclerView.setVisibility(View.VISIBLE);
        }
    }
}
