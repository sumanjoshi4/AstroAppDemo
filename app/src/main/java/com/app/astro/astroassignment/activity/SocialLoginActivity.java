package com.app.astro.astroassignment.activity;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.content.LocalBroadcastManager;
import android.support.v7.app.AppCompatActivity;
import android.util.AttributeSet;
import android.view.View;
import android.widget.TextView;

import com.app.astro.astroassignment.R;
import com.app.astro.astroassignment.utils.SharedPrefsUtil;
import com.facebook.CallbackManager;
import com.facebook.FacebookCallback;
import com.facebook.FacebookException;
import com.facebook.login.LoginResult;
import com.facebook.login.widget.LoginButton;

/**
 * Created by suman on 6/18/2017.
 * This class is used for login with facebook save its access token in shared pref
 */
public class SocialLoginActivity extends AppCompatActivity{

    private TextView info;
    private LoginButton loginButton;
    private CallbackManager callbackManager;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        callbackManager = CallbackManager.Factory.create();
        setContentView(R.layout.social_login_activity);
        findView();
        bindView();
    }

    private void findView(){
        info = (TextView)findViewById(R.id.info);
        loginButton = (LoginButton)findViewById(R.id.login_button);
    }

    private void bindView(){
        loginButton.registerCallback(callbackManager, new FacebookCallback<LoginResult>() {
            @Override
            public void onSuccess(LoginResult loginResult) {
                SharedPrefsUtil.saveUserId(loginResult.getAccessToken().getUserId());
                Intent intent = new Intent("start.socialLogin.finish.action");
                intent.putExtra("channleId",getIntent().getIntExtra("channleId",0));
                LocalBroadcastManager.getInstance(getApplicationContext()).sendBroadcast(intent);
                SocialLoginActivity.this.finish();
            }

            @Override
            public void onCancel() {

            }

            @Override
            public void onError(FacebookException error) {

            }
        });
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        callbackManager.onActivityResult(requestCode,resultCode,data);
    }
}
