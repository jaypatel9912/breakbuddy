package com.breakbuddy.UI;

import android.graphics.Typeface;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.DefaultItemAnimator;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.LinearLayout;
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
import java.util.Calendar;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;

import Adapters.FriendsOnBreakAdapter;
import Adapters.UserBreakTimeListAdapter;
import Fragments.ScheduleFragment;
import Model.BreakSchedules;
import Model.Friends;
import Model.LeaveSchedules;
import Model.Schedule;
import Utils.Constants;
import Utils.DividerItemDecoration;
import Utils.FontManager;
import Utils.SimpleDividerItemDecoration;
import Utils.Utilz;
import cz.msebera.android.httpclient.Header;
import cz.msebera.android.httpclient.entity.StringEntity;

public class BreakTimeDetailsActivity extends BaseActivity {

    Typeface iconFont;
    int dayId = 0;
    UserBreakTimeListAdapter mAdapter;
    RecyclerView rlscheduleList, rlLeaveList;
    TextView tvNoSchedule, tvNoEndTime;
    List<Schedule> scheduleList;
    List<Friends> leaveList;
    LinearLayout ll_leave;
    TextView tvEndTimeTime;
    FriendsOnBreakAdapter mLeaveAdapter;
    String leaveTime = "";
    LinearLayout llEndingTime;
    int currDay;
    View llBreak;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_break_time_details);

        dayId = getIntent().getExtras().getInt(Constants.MM_day_id);
        scheduleList = new ArrayList<>();
        leaveList = new ArrayList<>();

        android.support.v7.app.ActionBar mActionBar = getSupportActionBar();
        mActionBar.setDisplayShowHomeEnabled(false);
        mActionBar.setDisplayShowTitleEnabled(false);
        LayoutInflater mInflater = LayoutInflater.from(this);

        View mCustomView = mInflater.inflate(R.layout.toolbar, null);
        TextView mTitleTextView = (TextView) mCustomView.findViewById(R.id.title_text);
        mTitleTextView.setText(getString(R.string.break_time_details));
        mTitleTextView.setTypeface(Utilz.getTypefaceBold(BreakTimeDetailsActivity.this));

        Calendar calendar = Calendar.getInstance();
        currDay = calendar.get(Calendar.DAY_OF_WEEK);

        if (currDay == 1) {
            currDay = 7;
        } else {
            currDay--;
        }

        if (currDay == dayId) {
            mTitleTextView.setText(getString(R.string.main_today));
        } else {
            switch (dayId) {
                case 1:
                    mTitleTextView.setText(getString(R.string.main_monday));
                    break;
                case 2:
                    mTitleTextView.setText(getString(R.string.main_tueday));
                    break;
                case 3:
                    mTitleTextView.setText(getString(R.string.main_wedday));
                    break;
                case 4:
                    mTitleTextView.setText(getString(R.string.main_thursday));
                    break;
                case 5:
                    mTitleTextView.setText(getString(R.string.main_friday));
                    break;
                case 6:
                    mTitleTextView.setText(getString(R.string.main_satday));
                    break;
                case 7:
                    mTitleTextView.setText(getString(R.string.main_sunday));
                    break;
            }
        }

        iconFont = FontManager.getTypeface(getApplicationContext(), FontManager.FONTAWESOME);

        TextView tvBack = (TextView) mCustomView.findViewById(R.id.font_awesome_android_icon);
        tvBack.setVisibility(View.VISIBLE);
        tvBack.setTypeface(iconFont);
        tvBack.setOnClickListener(backClickListener);

        mActionBar.setCustomView(mCustomView);
        mActionBar.setDisplayShowCustomEnabled(true);

        initViews();
        getWeekDaySchedule(true);

    }

    private void initViews() {
        tvNoSchedule = (TextView) findViewById(R.id.tvNoSchedule);
        tvNoSchedule.setTypeface(Utilz.getTypefaceMedium(BreakTimeDetailsActivity.this));

        tvEndTimeTime = (TextView) findViewById(R.id.tvEndTimeTime);
        tvEndTimeTime.setTypeface(Utilz.getTypefaceBold(BreakTimeDetailsActivity.this));

        llBreak = (View) findViewById(R.id.llBreak);

        tvNoEndTime = (TextView) findViewById(R.id.tvNoEndTime);
        tvNoEndTime.setTypeface(Utilz.getTypefaceMedium(BreakTimeDetailsActivity.this));

        ll_leave = (LinearLayout) findViewById(R.id.ll_leave);

        llEndingTime = (LinearLayout) findViewById(R.id.llEndingTime);

        rlscheduleList = (RecyclerView) findViewById(R.id.rlscheduleList);
        RecyclerView.LayoutManager mLayoutManager = new LinearLayoutManager(BreakTimeDetailsActivity.this);
        rlscheduleList.setLayoutManager(mLayoutManager);
        rlscheduleList.setItemAnimator(new DefaultItemAnimator());
        rlscheduleList.addItemDecoration(new DividerItemDecoration(BreakTimeDetailsActivity.this, R.drawable.rcl_divider));
        mAdapter = new UserBreakTimeListAdapter(BreakTimeDetailsActivity.this);
        rlscheduleList.setAdapter(mAdapter);

        rlLeaveList = (RecyclerView) findViewById(R.id.rlLeaveList);
        RecyclerView.LayoutManager mLayoutManager2 = new LinearLayoutManager(BreakTimeDetailsActivity.this);
        rlLeaveList.setLayoutManager(mLayoutManager2);
        rlLeaveList.addItemDecoration(new DividerItemDecoration(BreakTimeDetailsActivity.this, R.drawable.rcl_divider));
        rlLeaveList.setItemAnimator(new DefaultItemAnimator());
        mLeaveAdapter = new FriendsOnBreakAdapter(BreakTimeDetailsActivity.this, false);
        rlLeaveList.setAdapter(mLeaveAdapter);

    }

    View.OnClickListener backClickListener = new View.OnClickListener() {
        @Override
        public void onClick(View v) {
            finish();
        }
    };

    public void getWeekDaySchedule(boolean showProgress) {

        if (!Utilz.isNetworkAvailable(BreakTimeDetailsActivity.this)) {
            tvNoSchedule.setVisibility(View.VISIBLE);
            ll_leave.setVisibility(View.GONE);
            Utilz.showMessage2(BreakTimeDetailsActivity.this, getString(R.string.network_error));
            return;
        }
        if (showProgress)
            Utilz.showProgressDialog(BreakTimeDetailsActivity.this);

        AsyncHttpClient client = new AsyncHttpClient();
        JSONObject objMain = new JSONObject();
        try {
            objMain.put(Constants.MM_user_id, Utilz.getUserDetails(BreakTimeDetailsActivity.this).get(Constants.MM_user_id));
            objMain.put(Constants.MM_weekday_id, String.valueOf(dayId));
            objMain.put(Constants.MM_current_weekday_id, String.valueOf(currDay));

            StringEntity entity = new StringEntity(objMain.toString());
            client.post(BreakTimeDetailsActivity.this, Constants.Base_url + Constants.API_get_weekdaywise_break_schedule_v3, entity, "application/json", new BreakTimeDetailsActivity.ScheduleResultHandler());

        } catch (Exception e) {
            e.printStackTrace();
            Utilz.closeProgressDialog();
        }
    }

    private class ScheduleResultHandler extends AsyncHttpResponseHandler {

        @Override
        public void onSuccess(int statusCode, Header[] headers, byte[] responseBody) {
            Utilz.closeProgressDialog();
            JSONObject response = null;
            try {
                response = new JSONObject(new String(responseBody));
                Log.i("response.) ", response.toString());
                if (response.has(Constants.MM_STATUS_CODE) && response.getString(Constants.MM_STATUS_CODE).equalsIgnoreCase(Constants.MM_STATUS_CODE_SUCCESS)) {

                    if (response.has(Constants.MM_schedules)) {
                        JSONArray arrSchedule = response.getJSONArray(Constants.MM_schedules);
                        Type listType = new TypeToken<List<Schedule>>() {
                        }.getType();
                        scheduleList = (List<Schedule>) new Gson().fromJson(arrSchedule.toString(), listType);
                    }

                    if (response.has(Constants.MM_leave_schedules)) {

                        JSONObject objLeave = response.getJSONObject(Constants.MM_leave_schedules);

                        if (objLeave.has(Constants.MM_leave_time)) {
                            leaveTime = objLeave.getString(Constants.MM_leave_time);
                        }

                        if (objLeave.has(Constants.MM_friends)) {
                            JSONArray arrSchedule = objLeave.getJSONArray(Constants.MM_friends);
                            Type listType = new TypeToken<List<Friends>>() {
                            }.getType();
                            leaveList = (List<Friends>) new Gson().fromJson(arrSchedule.toString(), listType);
                        }

                    }

                    setmAdapter();
                } else {
                    Utilz.showMessage2(BreakTimeDetailsActivity.this, response.get(Constants.MM_msg).toString());
                }
            } catch (Exception ex) {
                ex.printStackTrace();
            }
        }

        @Override
        public void onFailure(int statusCode, Header[] headers, byte[] responseBody, Throwable error) {
            Utilz.closeProgressDialog();
            Toast.makeText(BreakTimeDetailsActivity.this, getString(R.string.server_error), Toast.LENGTH_SHORT).show();
        }
    }


    private void setmAdapter() {
        tvEndTimeTime.setText(String.format(getString(R.string.ending_at_time), leaveTime));
        if (leaveList.size() > 0) {
            mLeaveAdapter.refreshList(leaveList);
            rlLeaveList.setVisibility(View.VISIBLE);
        } else {
            rlLeaveList.setVisibility(View.GONE);
            tvEndTimeTime.setVisibility(View.VISIBLE);
            llEndingTime.setVisibility(View.VISIBLE);
        }

        if (leaveTime.isEmpty() || leaveTime.equalsIgnoreCase("N/A")) {
            ll_leave.setVisibility(View.GONE);
            llEndingTime.setVisibility(View.GONE);
            llBreak.setVisibility(View.GONE);
        } else {
            ll_leave.setVisibility(View.VISIBLE);
            llBreak.setVisibility(View.VISIBLE);
            llEndingTime.setVisibility(View.VISIBLE);
            tvEndTimeTime.setVisibility(View.VISIBLE);
        }

        ArrayList<Friends> friends = new ArrayList<>();
        for (int i = 0; i < scheduleList.size(); i++) {
            if (scheduleList.get(i).getFriends().size() <= 0) {
                Friends fr = new Friends();
                fr.setParentFromTime(scheduleList.get(i).getFrom_time());
                fr.setParentToTime(scheduleList.get(i).getTo_time());
                friends.add(fr);
            } else {
                for (int j = 0; j < scheduleList.get(i).getFriends().size(); j++) {
                    Friends friends1 = scheduleList.get(i).getFriends().get(j);
                    friends1.setParentFromTime(scheduleList.get(i).getFrom_time());
                    friends1.setParentToTime(scheduleList.get(i).getTo_time());
                    friends.add(friends1);
                }
            }
        }

        for (int i = 0; i < friends.size(); i++) {
            if (friends.get(i).getFriend_user_id() != null) {
                if (i == 0)
                    friends.get(i).setIfShowSection(true);

                if (i > 0) {
                    if (!friends.get(i).getParentFromTime().equalsIgnoreCase(friends.get(i - 1).getParentFromTime()) && !friends.get(i).getParentToTime().equalsIgnoreCase(friends.get(i - 1).getParentToTime()))
                        friends.get(i).setIfShowSection(true);
                }
            } else {
                friends.get(i).setIfShowSection(true);
            }
        }

        Collections.sort(friends, new CustomComparator());

        if (friends.size() > 0) {
            mAdapter.refreshList(friends);
            rlscheduleList.setVisibility(View.VISIBLE);
            tvNoSchedule.setVisibility(View.GONE);
        } else {
            rlscheduleList.setVisibility(View.GONE);
            tvNoSchedule.setVisibility(View.VISIBLE);
        }
    }

    public class CustomComparator implements Comparator<Friends> {
        @Override
        public int compare(Friends o1, Friends o2) {
            return o1.getFirstname().toUpperCase().compareTo(o2.getFirstname().toUpperCase());
        }
    }

}
