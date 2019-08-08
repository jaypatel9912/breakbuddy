package com.breakbuddy.UI;

import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Typeface;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.DividerItemDecoration;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.WindowManager;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import com.breakbuddy.R;
import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;
import com.loopj.android.http.AsyncHttpClient;
import com.loopj.android.http.AsyncHttpResponseHandler;
import com.timehop.stickyheadersrecyclerview.StickyRecyclerHeadersDecoration;

import org.json.JSONArray;
import org.json.JSONObject;

import java.lang.reflect.Type;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;

import Adapters.AddedMeContactAdapter;
import Adapters.MyFriendsAdapter;
import Model.Friend;
import Model.Friends;
import QuickScroll.FastScroller;
import Utils.Constants;
import Utils.DividerDecoration;
import Utils.FontManager;
import Utils.Utilz;
import cz.msebera.android.httpclient.Header;
import cz.msebera.android.httpclient.entity.StringEntity;

public class MyFriendsActivity extends BaseActivity implements MyFriendsAdapter.OnMyFriendOperate {

    Typeface iconFont, bold, medium;
    EditText edSearch;
    FastScroller fastscroll;
    RecyclerView rcMyFriendList;
    MyFriendsAdapter mAdapter;
    List<Friend> friendList;
    TextView tvNoFriends;
    MyFriendsAdapter.OnMyFriendOperate listener;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        getWindow().setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_STATE_ALWAYS_HIDDEN);
        setContentView(R.layout.activity_my_friends);
        friendList = new ArrayList<>();
        listener = this;
        android.support.v7.app.ActionBar mActionBar = getSupportActionBar();
        mActionBar.setDisplayShowHomeEnabled(false);
        mActionBar.setDisplayShowTitleEnabled(false);
        LayoutInflater mInflater = LayoutInflater.from(this);

        View mCustomView = mInflater.inflate(R.layout.toolbar, null);
        TextView mTitleTextView = (TextView) mCustomView.findViewById(R.id.title_text);
        mTitleTextView.setText(getString(R.string.my_frnds));
        mTitleTextView.setTypeface(Utilz.getTypefaceBold(MyFriendsActivity.this));

        iconFont = FontManager.getTypeface(getApplicationContext(), FontManager.FONTAWESOME);
        bold = Utilz.getTypefaceBold(MyFriendsActivity.this);
        medium = Utilz.getTypefaceMedium(MyFriendsActivity.this);

        TextView tvBack = (TextView) mCustomView.findViewById(R.id.font_awesome_android_icon);
        tvBack.setVisibility(View.VISIBLE);
        tvBack.setTypeface(iconFont);
        tvBack.setOnClickListener(backClickListener);

        mActionBar.setCustomView(mCustomView);
        mActionBar.setDisplayShowCustomEnabled(true);

        initViews();
        getMyFriends();
    }

    View.OnClickListener backClickListener = new View.OnClickListener() {
        @Override
        public void onClick(View v) {
            finish();
        }
    };

    private void initViews() {
        edSearch = (EditText) findViewById(R.id.edSearch);
        edSearch.setTypeface(medium);
        tvNoFriends = (TextView) findViewById(R.id.tvNoFriends);
        tvNoFriends.setTypeface(medium);

        ArrayList<String> ar = new ArrayList<>();
        mAdapter = new MyFriendsAdapter(MyFriendsActivity.this, listener);
        rcMyFriendList = (RecyclerView) findViewById(R.id.rcMyFriends);

        final StickyRecyclerHeadersDecoration headersDecor = new StickyRecyclerHeadersDecoration(mAdapter);
        rcMyFriendList.addItemDecoration(headersDecor);
        rcMyFriendList.addItemDecoration(new DividerItemDecoration(MyFriendsActivity.this, DividerItemDecoration.VERTICAL));
        rcMyFriendList.setHasFixedSize(true);
        rcMyFriendList.setLayoutManager(new LinearLayoutManager(MyFriendsActivity.this));
        fastscroll = (FastScroller) findViewById(R.id.fastscroll);
        rcMyFriendList.setAdapter(mAdapter);

        mAdapter.registerAdapterDataObserver(new RecyclerView.AdapterDataObserver() {
            @Override
            public void onChanged() {
                headersDecor.invalidateHeaders();
            }
        });

        fastscroll.setRecyclerView(rcMyFriendList);

        edSearch.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                if (s.length() <= 0) {
                    if (friendList.size() > 0) {
                        mAdapter.refreshList(friendList);
                        tvNoFriends.setVisibility(View.GONE);
                        rcMyFriendList.setVisibility(View.VISIBLE);
                    } else {
                        rcMyFriendList.setVisibility(View.GONE);
                        tvNoFriends.setVisibility(View.VISIBLE);
                    }
                } else {
                    List<Friend> list = filter(friendList, s.toString());
                    if (list.size() > 0) {
                        mAdapter.refreshList(list);
                        tvNoFriends.setVisibility(View.GONE);
                        rcMyFriendList.setVisibility(View.VISIBLE);
                    } else {
                        rcMyFriendList.setVisibility(View.GONE);
                        tvNoFriends.setVisibility(View.VISIBLE);
                    }
                }
            }

            @Override
            public void afterTextChanged(Editable s) {

            }
        });
    }

    private static List<Friend> filter(List<Friend> models, String query) {
        final String lowerCaseQuery = query.toLowerCase();

        final List<Friend> filteredModelList = new ArrayList<>();
        for (Friend model : models) {
            final String text = model.getFirstname().toLowerCase();
            final String text2 = model.getLastname().toLowerCase();
            if (text.contains(lowerCaseQuery) || text2.contains(lowerCaseQuery)) {
                filteredModelList.add(model);
            }
        }
        return filteredModelList;
    }


    private void getMyFriends() {

        if (!Utilz.isNetworkAvailable(MyFriendsActivity.this)) {
            tvNoFriends.setVisibility(View.VISIBLE);
            Utilz.showMessage2(MyFriendsActivity.this, getString(R.string.network_error));
            return;
        }

        Utilz.showProgressDialog(MyFriendsActivity.this);
        AsyncHttpClient client = new AsyncHttpClient();
        JSONObject objMain = new JSONObject();
        try {
            objMain.put(Constants.MM_user_id, Utilz.getUserDetails(MyFriendsActivity.this).get(Constants.MM_user_id));

            StringEntity entity = new StringEntity(objMain.toString());
            client.post(MyFriendsActivity.this, Constants.Base_url + Constants.API_my_friend_list, entity, "application/json", new MyFriendsActivity.FindFriendsResponseHandler());

        } catch (Exception e) {
            e.printStackTrace();
            Utilz.closeProgressDialog();
        }
    }


    private class FindFriendsResponseHandler extends AsyncHttpResponseHandler {

        @Override
        public void onSuccess(int statusCode, Header[] headers, byte[] responseBody) {
            Utilz.closeProgressDialog();
            JSONObject response = null;
            try {
                response = new JSONObject(new String(responseBody));
                Log.i("response.) ", response.toString());
                if (response.has(Constants.MM_STATUS_CODE) && response.getString(Constants.MM_STATUS_CODE).equalsIgnoreCase(Constants.MM_STATUS_CODE_SUCCESS)) {

                    JSONArray jsonfriends = response.getJSONArray(Constants.MM_friends);
                    Type listType = new TypeToken<List<Friend>>() {
                    }.getType();

                    friendList = (List<Friend>) new Gson().fromJson(jsonfriends.toString(), listType);
                    Collections.sort(friendList, new CustomComparator());
                    if (friendList.size() > 0) {
                        mAdapter.refreshList(friendList);
                    } else {
                        tvNoFriends.setVisibility(View.VISIBLE);
                        rcMyFriendList.setVisibility(View.GONE);
                        fastscroll.setVisibility(View.GONE);
                    }

                } else {
                    Utilz.showMessage2(MyFriendsActivity.this, response.get(Constants.MM_msg).toString());
                }

            } catch (Exception ex) {
                ex.printStackTrace();
            }
        }

        @Override
        public void onFailure(int statusCode, Header[] headers, byte[] responseBody, Throwable error) {
            Utilz.closeProgressDialog();
            Toast.makeText(MyFriendsActivity.this, getString(R.string.server_error), Toast.LENGTH_SHORT).show();
        }
    }

    public class CustomComparator implements Comparator<Friend> {
        @Override
        public int compare(Friend o1, Friend o2) {
            return o1.getFirstname().toUpperCase().compareTo(o2.getFirstname().toUpperCase());
        }
    }

    @Override
    public void onDelete(final Friend friend) {
        AlertDialog.Builder builder1 = new AlertDialog.Builder(MyFriendsActivity.this);
        builder1.setMessage(getString(R.string.if_delete_user));
        builder1.setCancelable(false);

        builder1.setPositiveButton(
                android.R.string.ok,
                new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int id) {
                        dialog.dismiss();
                        deleteFriend(friend.getFriend_user_id());
                    }
                });

        builder1.setNegativeButton(android.R.string.cancel, new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                dialog.dismiss();
            }
        });

        AlertDialog alert11 = builder1.create();
        alert11.show();
    }

    @Override
    public void onShowScheduleOfUser(Friend friend) {
//        Toast.makeText(getApplicationContext(), "Show schedule for : " + friendList.get(pos).getLastname(), Toast.LENGTH_SHORT).show();
        Intent intent = new Intent(MyFriendsActivity.this, ScheduleActivity.class);
        intent.putExtra(Constants.MM_user_id, friend.getFriend_user_id());
        intent.putExtra(Constants.MM_friend_name, friend.getFirstname() + " " + friend.getLastname());
        startActivity(intent);
    }

    public void deleteFriend(String friend_id) {

        if (!Utilz.isNetworkAvailable(MyFriendsActivity.this)) {
            Utilz.showMessage2(MyFriendsActivity.this, getString(R.string.network_error));
            return;
        }

        Utilz.showProgressDialog(MyFriendsActivity.this);
        AsyncHttpClient client = new AsyncHttpClient();
        JSONObject objMain = new JSONObject();

        String user_id = Utilz.getUserDetails(MyFriendsActivity.this).get(Constants.MM_user_id);
        String friend_user_id = friend_id;

        try {
            objMain.put(Constants.MM_user_id, user_id);
            objMain.put(Constants.MM_friend_user_id, friend_user_id);

            StringEntity entity = new StringEntity(objMain.toString());
            client.post(MyFriendsActivity.this, Constants.Base_url + Constants.API_delete_friend, entity, "application/json", new DeleteRequestHandler());

        } catch (Exception e) {
            e.printStackTrace();
            Utilz.closeProgressDialog();
        }
    }


    private class DeleteRequestHandler extends AsyncHttpResponseHandler {

        @Override
        public void onSuccess(int statusCode, Header[] headers, byte[] responseBody) {
            Utilz.closeProgressDialog();
            JSONObject response = null;
            try {
                response = new JSONObject(new String(responseBody));
                Log.i("response.) ", response.toString());
                if (response.has(Constants.MM_STATUS_CODE) && response.getString(Constants.MM_STATUS_CODE).equalsIgnoreCase(Constants.MM_STATUS_CODE_SUCCESS)) {
                    AlertDialog.Builder builder1 = new AlertDialog.Builder(MyFriendsActivity.this);
                    builder1.setMessage(response.get(Constants.MM_msg).toString());
                    builder1.setCancelable(false);

                    builder1.setPositiveButton(
                            android.R.string.ok,
                            new DialogInterface.OnClickListener() {
                                public void onClick(DialogInterface dialog, int id) {
                                    dialog.cancel();
                                    edSearch.setText("");
                                    getMyFriends();
                                }
                            });
                    AlertDialog alert11 = builder1.create();
                    alert11.show();

                } else {
                    Utilz.showMessage2(MyFriendsActivity.this, response.get(Constants.MM_msg).toString());
                }

            } catch (Exception ex) {
                ex.printStackTrace();
            }
        }

        @Override
        public void onFailure(int statusCode, Header[] headers, byte[] responseBody, Throwable error) {
            Utilz.closeProgressDialog();
            Toast.makeText(MyFriendsActivity.this, getString(R.string.server_error), Toast.LENGTH_SHORT).show();
        }
    }


}