package com.breakbuddy.UI;

import android.content.Intent;
import android.graphics.Color;
import android.graphics.Typeface;
import android.os.Handler;
import android.os.Bundle;
import android.util.Log;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import com.breakbuddy.R;

import com.crashlytics.android.Crashlytics;

import API.CreateCountryCodeList;
import io.fabric.sdk.android.Fabric;

import org.json.JSONObject;

import java.util.HashMap;

import API.LoginApi;
import QuickScroll.Utils;
import Utils.Constants;
import Utils.Interfaces;
import Utils.Utilz;


public class SplashActivity extends BaseActivity implements Interfaces.LoginListener {

    Interfaces.LoginListener loginListener;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        Fabric.with(this, new Crashlytics());
        setContentView(R.layout.activity_splash);
        new CreateCountryCodeList().execute();
        loginListener = this;


        ProgressBar pb = (ProgressBar) findViewById(R.id.pb);
        pb.getIndeterminateDrawable().setColorFilter(Color.WHITE, android.graphics.PorterDuff.Mode.MULTIPLY);

        if (Utilz.getUserDetails(SplashActivity.this) != null) {
            if (Utilz.isNetworkAvailable(SplashActivity.this))
                new LoginApi(SplashActivity.this, Utilz.getUserDetails(SplashActivity.this).get(Constants.MM_email), Utilz.getUserDetails(SplashActivity.this).get(Constants.MM_password), loginListener);
            else
                Utilz.showMessage2(SplashActivity.this, getString(R.string.network_error));
        } else {
            new Handler().postDelayed(new Runnable() {
                @Override
                public void run() {
                    startActivity(new Intent(SplashActivity.this, LoginActivity.class));
                    finish();
                }
            }, 2500);
        }
    }

    @Override
    public void onSuccessToLogin(String res) {
        JSONObject response;

        try {
            response = new JSONObject(res);
//                makeDDbEntryForSubscription(response, mainPurchase);

            Log.i("response ", response.toString());

            if (response.has(Constants.MM_STATUS_CODE) && response.getString(Constants.MM_STATUS_CODE).equalsIgnoreCase(Constants.MM_STATUS_CODE_SUCCESS)) {
                if (response.has(Constants.MM_user)) {
                    JSONObject resultObj = response.getJSONObject(Constants.MM_user);
                    HashMap<String, String> user = new HashMap<>();
                    user.put(Constants.MM_user_id, resultObj.getString(Constants.MM_user_id));
                    user.put(Constants.MM_email, resultObj.getString(Constants.MM_email));
                    user.put(Constants.MM_is_active, resultObj.getString(Constants.MM_is_active));
                    user.put(Constants.MM_phone_no, resultObj.getString(Constants.MM_phone_no));
                    user.put(Constants.MM_firstname, resultObj.getString(Constants.MM_firstname));
                    user.put(Constants.MM_lastname, resultObj.getString(Constants.MM_lastname));

                    if (resultObj.has(Constants.MM_added_me_count))
                        user.put(Constants.MM_added_me_count, resultObj.getString(Constants.MM_added_me_count));

                    user.put(Constants.MM_password, Utilz.getUserDetails(SplashActivity.this).get(Constants.MM_password));

                    if (resultObj.has(Constants.MM_is_show_hide))
                        user.put(Constants.MM_is_show_hide, resultObj.getString(Constants.MM_is_show_hide));

                    if (resultObj.has(Constants.MM_is_online_offline))
                        user.put(Constants.MM_is_online_offline, resultObj.getString(Constants.MM_is_online_offline));

                    if (resultObj.has(Constants.MM_days))
                        user.put(Constants.MM_days, resultObj.getString(Constants.MM_days));
                    if (resultObj.has(Constants.MM_added_me_count))
                        user.put(Constants.MM_added_me_count, resultObj.getString(Constants.MM_added_me_count));

//                    Toast.makeText(SplashActivity.this, response.getString(Constants.MM_msg), Toast.LENGTH_SHORT).show();

                    Utilz.setUserDetails(SplashActivity.this, user);

                    Intent i;
                    if (Utilz.getUserDetails(SplashActivity.this).get(Constants.MM_is_active).equalsIgnoreCase("0")) {
                        Utilz.saveBooleanToUserDefaults(SplashActivity.this, Constants.MM_showActivationDialog, true);
                        i = new Intent(SplashActivity.this, LoginActivity.class);
                    } else {
                        i = new Intent(SplashActivity.this, MainActivity.class);
                    }

                    i.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
                    startActivity(i);
                    finish();
                }
            } else {
                Utilz.setUserDetails(SplashActivity.this, null);
                Toast.makeText(SplashActivity.this, response.getString(Constants.MM_msg), Toast.LENGTH_SHORT).show();
                startActivity(new Intent(SplashActivity.this, LoginActivity.class));
                finish();

            }
        } catch (Exception ex) {
            ex.printStackTrace();
        }
    }

    @Override
    public void onFailedToLogin() {
        Toast.makeText(SplashActivity.this, getString(R.string.server_error), Toast.LENGTH_SHORT).show();
        finish();
//        startActivity(new Intent(SplashActivity.this, LoginActivity.class));
    }
}
