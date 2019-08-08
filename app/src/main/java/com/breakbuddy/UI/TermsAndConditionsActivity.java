package com.breakbuddy.UI;

import android.graphics.Typeface;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.text.Html;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.webkit.WebView;
import android.widget.TextView;
import android.widget.Toast;

import com.breakbuddy.R;
import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;
import com.loopj.android.http.AsyncHttpClient;
import com.loopj.android.http.AsyncHttpResponseHandler;

import org.json.JSONArray;
import org.json.JSONObject;

import java.lang.reflect.Type;
import java.util.ArrayList;
import java.util.List;

import API.LoginApi;
import Model.Friend;
import Utils.Constants;
import Utils.FontManager;
import Utils.Utilz;
import cz.msebera.android.httpclient.Header;
import cz.msebera.android.httpclient.entity.StringEntity;

public class TermsAndConditionsActivity extends BaseActivity {

    WebView tvTnc;
    Typeface iconFont;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_terms_and_conditions);


        android.support.v7.app.ActionBar mActionBar = getSupportActionBar();
        mActionBar.setDisplayShowHomeEnabled(false);
        mActionBar.setDisplayShowTitleEnabled(false);
        LayoutInflater mInflater = LayoutInflater.from(this);

        View mCustomView = mInflater.inflate(R.layout.toolbar, null);
        TextView mTitleTextView = (TextView) mCustomView.findViewById(R.id.title_text);
        mTitleTextView.setText(getString(R.string.tnc2));
        mTitleTextView.setTypeface(Utilz.getTypefaceBold(TermsAndConditionsActivity.this));

        iconFont = FontManager.getTypeface(getApplicationContext(), FontManager.FONTAWESOME);

        TextView tvBack = (TextView) mCustomView.findViewById(R.id.font_awesome_android_icon);
        tvBack.setTypeface(iconFont);
        tvBack.setOnClickListener(bacKClick);

        mActionBar.setCustomView(mCustomView);
        mActionBar.setDisplayShowCustomEnabled(true);

        tvTnc = (WebView) findViewById(R.id.tvTnc);



        if (Utilz.isNetworkAvailable(TermsAndConditionsActivity.this))
            getTermsAndCondition();
        else
            Utilz.showMessage2(TermsAndConditionsActivity.this, getString(R.string.network_error));
    }

    View.OnClickListener bacKClick = new View.OnClickListener() {
        @Override
        public void onClick(View v) {
            finish();
        }
    };

    private void getTermsAndCondition() {
        Utilz.showProgressDialog(TermsAndConditionsActivity.this);
        AsyncHttpClient client = new AsyncHttpClient();
        JSONObject objMain = new JSONObject();
        try {
            client.post(TermsAndConditionsActivity.this, Constants.Base_url + Constants.API_terms_of_use, null, "application/json", new TermsAndConditionsActivity.AddedMeFriendsResponseHandler());

        } catch (Exception e) {
            e.printStackTrace();
            Utilz.closeProgressDialog();
        }
    }

    private class AddedMeFriendsResponseHandler extends AsyncHttpResponseHandler {

        @Override
        public void onSuccess(int statusCode, Header[] headers, byte[] responseBody) {
            Utilz.closeProgressDialog();
            try {
                String tnc = new String(responseBody);
                tvTnc.loadData(tnc, "text/html", "UTF-8");
            } catch (Exception ex) {
                ex.printStackTrace();
            }
        }

        @Override
        public void onFailure(int statusCode, Header[] headers, byte[] responseBody, Throwable error) {
            Utilz.closeProgressDialog();
            Toast.makeText(TermsAndConditionsActivity.this, getString(R.string.server_error), Toast.LENGTH_SHORT).show();
        }
    }

}
