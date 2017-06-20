package com.app.astro.astroassignment.database;

import android.content.Context;
import android.provider.ContactsContract;
import android.support.annotation.NonNull;
import android.util.Log;

import com.app.astro.astroassignment.data.ChannelModel;
import com.app.astro.astroassignment.network.AppController;
import com.app.astro.astroassignment.utils.SharedPrefsUtil;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by B0096643 on 6/19/2017.
 * Helper class to communicate with Firebase server
 */
public class FireBaseDataBaseHelper {

    private static FireBaseDataBaseHelper mInstance;
    private static DatabaseReference databaseReference;
    private static DatabaseReference favouriteReference;
    private static Context mContext;
    private IFetchFireBaseData mInterfaceFirebase;

    public static FireBaseDataBaseHelper getInstance(){
        if(mInstance==null){
            synchronized (FireBaseDataBaseHelper.class){
                if(mInstance==null)
                    mInstance = new FireBaseDataBaseHelper();
            }
            databaseReference = FirebaseDatabase.getInstance().getReference();
            favouriteReference = databaseReference.child(SharedPrefsUtil.getUserId());
        }
        return mInstance;
    }


    public void syncFavouriteDateToCloud(){
        ArrayList<ChannelModel> list = DataBaseHelper.getFavouriteList();
        for(ChannelModel model:list){
            favouriteReference.child(model.getChannelId()+"").setValue(model);
        }

    }

    public void updateLocalDBFromServer(final FireBaseDataBaseHelper.IFetchFireBaseData firebaseListner){
        DatabaseReference database = FirebaseDatabase.getInstance().getReference();
        database.child(SharedPrefsUtil.getUserId()).addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                for (DataSnapshot noteSnapshot: dataSnapshot.getChildren()){
                    ChannelModel note = noteSnapshot.getValue(ChannelModel.class);
                    DataBaseHelper.insertChannelDataIntoDB(note);
                }
                firebaseListner.onDataFetchDone();
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {

            }
        }
    );
    }

    public interface IFetchFireBaseData
    {
        public void onDataFetchDone();
    }

}
