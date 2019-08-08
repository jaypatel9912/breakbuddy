package com.breakbuddy.UI;

import android.content.DialogInterface;
import android.graphics.Typeface;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.DefaultItemAnimator;
import android.support.v7.widget.DividerItemDecoration;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
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

import Adapters.AddedMeContactAdapter;
import Adapters.ContactAdapter;
import Fragments.ScheduleFragment;
import Model.Friend;
import Utils.Constants;
import Utils.FontManager;
import Utils.Utilz;
import cz.msebera.android.httpclient.Header;
import cz.msebera.android.httpclient.entity.StringEntity;

public class AddedMeActivity extends BaseActivity implements AddedMeContactAdapter.AddMeListener {

    Typeface iconFont;
    private RecyclerView rlContacts;
    AddedMeContactAdapter mAdapter;
    TextView tvNoConatcs;
    AddedMeContactAdapter.AddMeListener listener;
    List<Friend> friends;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_added_me);

        listener = this;

        android.support.v7.app.ActionBar mActionBar = getSupportActionBar();
        mActionBar.setDisplayShowHomeEnabled(false);
        mActionBar.setDisplayShowTitleEnabled(false);
        LayoutInflater mInflater = LayoutInflater.from(this);

        View mCustomView = mInflater.inflate(R.layout.toolbar, null);
        TextView mTitleTextView = (TextView) mCustomView.findViewById(R.id.title_text);
        mTitleTextView.setText(getString(R.string.added_me));
        mTitleTextView.setTypeface(Utilz.getTypefaceBold(AddedMeActivity.this));

        iconFont = FontManager.getTypeface(getApplicationContext(), FontManager.FONTAWESOME);

        TextView tvBack = (TextView) mCustomView.findViewById(R.id.font_awesome_android_icon);
        tvBack.setVisibility(View.VISIBLE);
        tvBack.setTypeface(iconFont);
        tvBack.setOnClickListener(backClickListener);

        mActionBar.setCustomView(mCustomView);
        mActionBar.setDisplayShowCustomEnabled(true);

        initViews();
        if (Utilz.isNetworkAvailable(AddedMeActivity.this)) {
            getAddedMeFriends();
        } else {
            Utilz.showMessage2(AddedMeActivity.this, getString(R.string.network_error));
        }
    }

    View.OnClickListener backClickListener = new View.OnClickListener() {
        @Override
        public void onClick(View v) {
            finish();
        }
    };

    private void initViews() {
        friends = new ArrayList<>();
        tvNoConatcs = (TextView) findViewById(R.id.tvNoConatcs);
        tvNoConatcs.setTypeface(Utilz.getTypefaceMedium(AddedMeActivity.this));

        rlContacts = (RecyclerView) findViewById(R.id.rlContacts);
        RecyclerView.LayoutManager mLayoutManager = new LinearLayoutManager(getApplicationContext());
        rlContacts.setLayoutManager(mLayoutManager);
        rlContacts.addItemDecoration(new Utils.DividerItemDecoration(AddedMeActivity.this, R.drawable.rcl_divider));
        rlContacts.setItemAnimator(new DefaultItemAnimator());

        mAdapter = new AddedMeContactAdapter(AddedMeActivity.this, listener);
        rlContacts.setAdapter(mAdapter);
    }


    private void getAddedMeFriends() {

        Utilz.showProgressDialog(AddedMeActivity.this);
        AsyncHttpClient client = new AsyncHttpClient();
        JSONObject objMain = new JSONObject();
        try {
            objMain.put(Constants.MM_user_id, Integer.parseInt(Utilz.getUserDetails(AddedMeActivity.this).get(Constants.MM_user_id)));
            StringEntity entity = new StringEntity(objMain.toString());
            client.post(AddedMeActivity.this, Constants.Base_url + Constants.API_added_me_friend_list, entity, "application/json", new AddedMeFriendsResponseHandler());

        } catch (Exception e) {
            e.printStackTrace();
            Utilz.closeProgressDialog();
        }
    }

    private class AddedMeFriendsResponseHandler extends AsyncHttpResponseHandler {

        @Override
        public void onSuccess(int statusCode, Header[] headers, byte[] responseBody) {
            Utilz.closeProgressDialog();
            JSONObject response = null;
            try {
                response = new JSONObject(new String(responseBody));
                Log.i("response.) ", response.toString());
                if (response.has(Constants.MM_STATUS_CODE) && response.getString(Constants.MM_STATUS_CODE).equalsIgnoreCase(Constants.MM_STATUS_CODE_SUCCESS)) {
                    if (response.has(Constants.MM_friends)) {
                        JSONArray jsonArray = response.getJSONArray(Constants.MM_friends);
                        if (jsonArray.length() > 0) {
                            Type listType = new TypeToken<List<Friend>>() {
                            }.getType();
                            friends = (List<Friend>) new Gson().fromJson(jsonArray.toString(), listType);
                        } else {
                            friends = new ArrayList<>();
                        }
                    }
                    setAdapter();
                } else {
                    Utilz.showMessage2(AddedMeActivity.this, response.get(Constants.MM_msg).toString());
                }
            } catch (Exception ex) {
                ex.printStackTrace();
            }
        }

        @Override
        public void onFailure(int statusCode, Header[] headers, byte[] responseBody, Throwable error) {
            Utilz.closeProgressDialog();
            Toast.makeText(AddedMeActivity.this, getString(R.string.server_error), Toast.LENGTH_SHORT).show();
        }
    }

    private void setAdapter() {
        if (friends.size() > 0) {
            rlContacts.setVisibility(View.VISIBLE);
            tvNoConatcs.setVisibility(View.GONE);
            mAdapter.refreshList(friends);
        } else {
            rlContacts.setVisibility(View.GONE);
            tvNoConatcs.setVisibility(View.VISIBLE);
        }
    }

    @Override
    public void onCancel(int pos) {
        if (Utilz.isNetworkAvailable(AddedMeActivity.this)) {
            manageRequest(friends.get(pos), 2);
        } else {
            Utilz.showMessage2(AddedMeActivity.this, getString(R.string.network_error));
        }

    }

    @Override
    public void onAdd(int pos) {
        if (Utilz.isNetworkAvailable(AddedMeActivity.this)) {
            manageRequest(friends.get(pos), 1);
        } else {
            Utilz.showMessage2(AddedMeActivity.this, getString(R.string.network_error));
        }
    }

    private void manageRequest(Friend friend, int service) {

        Utilz.showProgressDialog(AddedMeActivity.this);
        AsyncHttpClient client = new AsyncHttpClient();
        JSONObject objMain = new JSONObject();

        String friend_user_id = Utilz.getUserDetails(AddedMeActivity.this).get(Constants.MM_user_id);
        String user_id = friend.getUser_id();

        String serviceName = "";
        if (service == 1)
            serviceName = Constants.API_accept_friend_request;
        else if (service == 2)
            serviceName = Constants.API_reject_friend_request;

        try {
            objMain.put(Constants.MM_user_id, user_id);
            objMain.put(Constants.MM_friend_user_id, friend_user_id);

            StringEntity entity = new StringEntity(objMain.toString());
            client.post(AddedMeActivity.this, Constants.Base_url + serviceName, entity, "application/json", new AddedMeActivity.RequestOperationHandler());

        } catch (Exception e) {
            e.printStackTrace();
            Utilz.closeProgressDialog();
        }
    }


    private class RequestOperationHandler extends AsyncHttpResponseHandler {

        @Override
        public void onSuccess(int statusCode, Header[] headers, byte[] responseBody) {
            Utilz.closeProgressDialog();
            JSONObject response = null;
            try {
                response = new JSONObject(new String(responseBody));
                Log.i("response.) ", response.toString());
                if (response.has(Constants.MM_STATUS_CODE) && response.getString(Constants.MM_STATUS_CODE).equalsIgnoreCase(Constants.MM_STATUS_CODE_SUCCESS)) {
                    AlertDialog.Builder builder1 = new AlertDialog.Builder(AddedMeActivity.this);
                    builder1.setMessage(response.get(Constants.MM_msg).toString());
                    builder1.setCancelable(false);

                    builder1.setPositiveButton(
                            android.R.string.ok,
                            new DialogInterface.OnClickListener() {
                                public void onClick(DialogInterface dialog, int id) {
                                    dialog.cancel();
                                    getAddedMeFriends();
                                }
                            });

                    AlertDialog alert11 = builder1.create();
                    alert11.show();
                } else {
                    Utilz.showMessage2(AddedMeActivity.this, response.get(Constants.MM_msg).toString());
                }

            } catch (Exception ex) {
                ex.printStackTrace();
            }
        }

        @Override
        public void onFailure(int statusCode, Header[] headers, byte[] responseBody, Throwable error) {
            Utilz.closeProgressDialog();
            Toast.makeText(AddedMeActivity.this, getString(R.string.server_error), Toast.LENGTH_SHORT).show();
        }
    }

}