package com.breakbuddy.UI;

import android.graphics.Typeface;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.DefaultItemAnimator;
import android.support.v7.widget.DividerItemDecoration;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.Log;
import android.util.TypedValue;
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
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;

import Adapters.FriendsOnBreakAdapter;
import Model.Friend;
import Model.Friends;
import Model.Schedule;
import QuickScroll.FastScroller;
import Utils.Constants;
import Utils.FontManager;
import Utils.Utilz;
import cz.msebera.android.httpclient.Header;
import cz.msebera.android.httpclient.entity.StringEntity;

public class FriendsCurrentlyOnBreakActivity extends BaseActivity {

    Typeface iconFont;
    TextView tvNoData;
    RecyclerView rlFriendOnBreakList;
    List<Friends> friendList;
    FriendsOnBreakAdapter mAdapter;
    EditText edSearch;
    FastScroller fastscroll;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        getWindow().setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_STATE_ALWAYS_HIDDEN);
        setContentView(R.layout.activity_friends_currently_on_break);

        friendList = new ArrayList<>();

        android.support.v7.app.ActionBar mActionBar = getSupportActionBar();
        mActionBar.setDisplayShowHomeEnabled(false);
        mActionBar.setDisplayShowTitleEnabled(false);
        LayoutInflater mInflater = LayoutInflater.from(this);

        View mCustomView = mInflater.inflate(R.layout.toolbar_2, null);
        TextView mTitleTextView = (TextView) mCustomView.findViewById(R.id.title_text);
        mTitleTextView.setText(getString(R.string.friends_curr_on_break_now));
        mTitleTextView.setTextSize(TypedValue.COMPLEX_UNIT_SP, 16);
        mTitleTextView.setTypeface(Utilz.getTypefaceBold(FriendsCurrentlyOnBreakActivity.this));

        iconFont = FontManager.getTypeface(getApplicationContext(), FontManager.FONTAWESOME);

        TextView tvBack = (TextView) mCustomView.findViewById(R.id.font_awesome_android_icon);
        tvBack.setVisibility(View.VISIBLE);
        tvBack.setTypeface(iconFont);
        tvBack.setOnClickListener(backClickListener);

        mActionBar.setCustomView(mCustomView);
        mActionBar.setDisplayShowCustomEnabled(true);

        initViews();
        getFriendsCurrentlyOnBreak(true);
    }


    View.OnClickListener backClickListener = new View.OnClickListener() {
        @Override
        public void onClick(View v) {
            finish();
        }
    };

    private void initViews() {
        tvNoData = (TextView) findViewById(R.id.tvNoData);
        edSearch = (EditText) findViewById(R.id.edSearch);
        fastscroll = (FastScroller) findViewById(R.id.fastscroll);

        mAdapter = new FriendsOnBreakAdapter(FriendsCurrentlyOnBreakActivity.this, true);
        rlFriendOnBreakList = (RecyclerView) findViewById(R.id.rlFriendOnBreakList);
        final StickyRecyclerHeadersDecoration headersDecor = new StickyRecyclerHeadersDecoration(mAdapter);
        rlFriendOnBreakList.addItemDecoration(headersDecor);
        rlFriendOnBreakList.addItemDecoration(new DividerItemDecoration(FriendsCurrentlyOnBreakActivity.this, DividerItemDecoration.VERTICAL));
        rlFriendOnBreakList.setHasFixedSize(true);
        rlFriendOnBreakList.setLayoutManager(new LinearLayoutManager(FriendsCurrentlyOnBreakActivity.this));
        fastscroll = (FastScroller) findViewById(R.id.fastscroll);
        rlFriendOnBreakList.setAdapter(mAdapter);

        mAdapter.registerAdapterDataObserver(new RecyclerView.AdapterDataObserver() {
            @Override
            public void onChanged() {
                headersDecor.invalidateHeaders();
            }
        });

        fastscroll.setRecyclerView(rlFriendOnBreakList);

        edSearch.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                if (s.length() <= 0) {
                    if (friendList.size() > 0) {
                        mAdapter.refreshList(friendList);
                        tvNoData.setVisibility(View.GONE);
                        rlFriendOnBreakList.setVisibility(View.VISIBLE);
                    } else {
                        rlFriendOnBreakList.setVisibility(View.GONE);
                        tvNoData.setVisibility(View.VISIBLE);
                    }
                } else {
                    List<Friends> list = filter(friendList, s.toString());
                    if (list.size() > 0) {
                        mAdapter.refreshList(list);
                        tvNoData.setVisibility(View.GONE);
                        rlFriendOnBreakList.setVisibility(View.VISIBLE);
                    } else {
                        rlFriendOnBreakList.setVisibility(View.GONE);
                        tvNoData.setVisibility(View.VISIBLE);
                    }
                }
            }

            @Override
            public void afterTextChanged(Editable s) {

            }
        });
    }

    private static List<Friends> filter(List<Friends> models, String query) {
        final String lowerCaseQuery = query.toLowerCase();

        final List<Friends> filteredModelList = new ArrayList<>();
        for (Friends model : models) {
            final String text = model.getFirstname().toLowerCase();
            final String text2 = model.getLastname().toLowerCase();
            if (text.contains(lowerCaseQuery) || text2.contains(lowerCaseQuery)) {
                filteredModelList.add(model);
            }
        }
        return filteredModelList;
    }

    public void getFriendsCurrentlyOnBreak(boolean showProgress) {

        if (!Utilz.isNetworkAvailable(FriendsCurrentlyOnBreakActivity.this)) {
            tvNoData.setVisibility(View.VISIBLE);
            Utilz.showMessage2(FriendsCurrentlyOnBreakActivity.this, getString(R.string.network_error));
            return;
        }

        DateFormat df = new SimpleDateFormat("h:mm a");
        String date = df.format(Calendar.getInstance().getTime());

        Calendar calendar = Calendar.getInstance();
        int day = calendar.get(Calendar.DAY_OF_WEEK);

        if (day == 1) {
            day = 7;
        } else {
            day--;
        }

        if (showProgress)
            Utilz.showProgressDialog(FriendsCurrentlyOnBreakActivity.this);

        AsyncHttpClient client = new AsyncHttpClient();
        JSONObject objMain = new JSONObject();
        try {
            objMain.put(Constants.MM_user_id, Utilz.getUserDetails(FriendsCurrentlyOnBreakActivity.this).get(Constants.MM_user_id));
            objMain.put(Constants.MM_weekday_id, String.valueOf(day));
            objMain.put(Constants.MM_current_time, date);

            StringEntity entity = new StringEntity(objMain.toString());
            client.post(FriendsCurrentlyOnBreakActivity.this, Constants.Base_url + Constants.API_get_currently_on_break_friends, entity, "application/json", new FetchCurrentlyOnBreakFriendsHandler());

        } catch (Exception e) {
            e.printStackTrace();
            Utilz.closeProgressDialog();
        }
    }

    private class FetchCurrentlyOnBreakFriendsHandler extends AsyncHttpResponseHandler {

        @Override
        public void onSuccess(int statusCode, Header[] headers, byte[] responseBody) {
            Utilz.closeProgressDialog();
            JSONObject response = null;
            try {
                response = new JSONObject(new String(responseBody));
                Log.i("response.) ", response.toString());
                if (response.has(Constants.MM_STATUS_CODE) && response.getString(Constants.MM_STATUS_CODE).equalsIgnoreCase(Constants.MM_STATUS_CODE_SUCCESS)) {

                    if (response.has(Constants.MM_friends)) {
                        JSONArray arrSchedule = response.getJSONArray(Constants.MM_friends);
                        Type listType = new TypeToken<List<Friends>>() {
                        }.getType();
                        friendList = (List<Friends>) new Gson().fromJson(arrSchedule.toString(), listType);
                        Collections.sort(friendList, new CustomComparator());
                    }
                    setAdapter();
                } else {
                    Utilz.showMessage2(FriendsCurrentlyOnBreakActivity.this, response.get(Constants.MM_msg).toString());
                }
            } catch (Exception ex) {
                ex.printStackTrace();
            }
        }

        @Override
        public void onFailure(int statusCode, Header[] headers, byte[] responseBody, Throwable error) {
            Utilz.closeProgressDialog();
            Toast.makeText(FriendsCurrentlyOnBreakActivity.this, getString(R.string.server_error), Toast.LENGTH_SHORT).show();
        }
    }

    public void setAdapter() {
        if (friendList.size() > 0) {
            mAdapter.refreshList(friendList);
            rlFriendOnBreakList.setVisibility(View.VISIBLE);
            tvNoData.setVisibility(View.GONE);
        } else {
            rlFriendOnBreakList.setVisibility(View.GONE);
            tvNoData.setVisibility(View.VISIBLE);
        }
    }

    public class CustomComparator implements Comparator<Friends> {
        @Override
        public int compare(Friends o1, Friends o2) {
            return o1.getFirstname().toUpperCase().compareTo(o2.getFirstname().toUpperCase());
        }
    }
}
