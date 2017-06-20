package com.app.astro.astroassignment.activity;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import android.content.pm.Signature;
import android.support.v4.app.Fragment;
import android.support.v4.content.LocalBroadcastManager;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.text.TextUtils;
import android.util.Base64;
import android.util.Log;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.widget.ProgressBar;
import android.widget.Toast;

import com.app.astro.astroassignment.R;
import com.app.astro.astroassignment.fragment.HomeChannelFragment;
import com.app.astro.astroassignment.network.BusProvider;
import com.app.astro.astroassignment.response.LogoutResponse;
import com.app.astro.astroassignment.task.LogoutTask;
import com.app.astro.astroassignment.utils.SharedPrefsUtil;
import com.app.astro.astroassignment.utils.ThreadUtils;
import com.squareup.otto.Subscribe;

import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;

public class MainActivity extends AppCompatActivity {

    ProgressBar mProgressBar;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
       // generateKeyHash();
        findView();
        BusProvider.getInstance().register(this);
        LocalBroadcastManager.getInstance(this).registerReceiver(receiver,
                new IntentFilter("start.socialLogin.action"));
        setFragment();
    }

    private void findView(){
        mProgressBar = (ProgressBar) findViewById(R.id.progressBar1);
    }

    BroadcastReceiver receiver = new BroadcastReceiver() {
        @Override
        public void onReceive(Context context, Intent intent) {
            launchFbLoginDialog(intent);
        }
    };

    private void setFragment(){
        getSupportFragmentManager()
                .beginTransaction()
                .replace(android.R.id.list,
                        HomeChannelFragment.newInstance(),
                        "listfragment")
                .commit();
    }

    private void launchFbLoginDialog(Intent myIntent){
        Intent intent = new Intent(this,SocialLoginActivity.class);
        intent.putExtra("channleId",myIntent.getIntExtra("channleId",0));
        startActivity(intent);
    }

    @Override
    protected void onDestroy() {
        LocalBroadcastManager.getInstance(this).unregisterReceiver(receiver);
        BusProvider.getInstance().unregister(this);
        super.onDestroy();
    }

    @Override
    public boolean onPrepareOptionsMenu(Menu menu) {
        menu.clear();
        if(!TextUtils.isEmpty(SharedPrefsUtil.getUserId())){
            MenuInflater inflater = getMenuInflater();
            inflater.inflate(R.menu.main_menu,menu);
            return true;
        }
        return false;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()){
            case R.id.logout:
                doLogout();
                return true;
            default:
                return super.onOptionsItemSelected(item);
        }
    }

    private void doLogout(){
        mProgressBar.setVisibility(View.VISIBLE);
        ThreadUtils.getDefaultExecutorService().submit(new LogoutTask());
    }

    @Subscribe
    public void onLogoutEvent(LogoutResponse response){
        mProgressBar.setVisibility(View.GONE);
        if(response.isSuccesful()){
            invalidateOptionsMenu();
            Fragment frag = getSupportFragmentManager().findFragmentByTag("listfragment");
            if(frag instanceof HomeChannelFragment){
                ((HomeChannelFragment)frag).intListViewAfterLogout();
               //z ((HomeChannelFragment)frag).hideFavourite();
            }
        }else{
            Toast.makeText(getApplicationContext(),getResources().getString(R.string.error_network_default),Toast.LENGTH_SHORT).show();
        }
    }

    public void generateKeyHash()
    {
        try {
            PackageInfo info = getPackageManager().getPackageInfo(
                    "com.app.astro.astroassignment",
                    PackageManager.GET_SIGNATURES);
            for (Signature signature : info.signatures) {
                MessageDigest md = MessageDigest.getInstance("SHA");
                md.update(signature.toByteArray());
                Log.d("KeyHash:", Base64.encodeToString(md.digest(), Base64.DEFAULT));
            }
        } catch (PackageManager.NameNotFoundException e) {

        } catch (NoSuchAlgorithmException e) {

        }
    }

}
