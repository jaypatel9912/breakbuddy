package com.breakbuddy.UI;

import android.content.Intent;
import android.graphics.Typeface;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentTransaction;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.RadioButton;
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
import java.util.Arrays;
import java.util.List;

import Fragments.ScheduleFragment;
import Model.BreakSchedules;
import Model.BreakTime;
import Model.LeaveSchedules;
import Model.Schedule;
import Model.WeekSchedule;
import Utils.Constants;
import Utils.FontManager;
import Utils.Interfaces;
import Utils.Utilz;
import cz.msebera.android.httpclient.Header;
import cz.msebera.android.httpclient.entity.StringEntity;

public class ScheduleActivity extends BaseActivity implements View.OnClickListener, Interfaces.ContinueClickListener {


    Typeface iconFont;
    RadioButton btnMon, btnTue, btnWed, btnThu, btnFri, btnSat, btnSun;
    int currentDay = 1;
    public static ArrayList<WeekSchedule> weekDays;
    public static boolean ifEditable = true;
    TextView tvScheduleTitle;

    public static List<BreakSchedules> breakSchedules;
    public static List<LeaveSchedules> leaveSchedules;

    public static String userId = "";
    public String friend_name = "";
    public  static  Interfaces.ContinueClickListener continueClickListener;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_schedule);

        continueClickListener = this;

        breakSchedules = new ArrayList<>();
        leaveSchedules = new ArrayList<>();

        if (getIntent().getExtras() != null) {
            if (getIntent().getExtras().containsKey(Constants.MM_user_id) && getIntent().getExtras().get(Constants.MM_user_id) != null && !getIntent().getExtras().get(Constants.MM_user_id).toString().isEmpty()) {
                userId = getIntent().getExtras().get(Constants.MM_user_id).toString();
                friend_name = getIntent().getExtras().get(Constants.MM_friend_name).toString();
            } else {
                userId = Utilz.getUserDetails(ScheduleActivity.this).get(Constants.MM_user);
            }
        } else {
            userId = Utilz.getUserDetails(ScheduleActivity.this).get(Constants.MM_user_id);
        }

        if (!userId.equalsIgnoreCase(Utilz.getUserDetails(ScheduleActivity.this).get(Constants.MM_user_id)))
            ifEditable = false;
        else
            ifEditable = true;

        android.support.v7.app.ActionBar mActionBar = getSupportActionBar();
        mActionBar.setDisplayShowHomeEnabled(false);
        mActionBar.setDisplayShowTitleEnabled(false);
        LayoutInflater mInflater = LayoutInflater.from(this);

        View mCustomView = mInflater.inflate(R.layout.toolbar, null);
        TextView mTitleTextView = (TextView) mCustomView.findViewById(R.id.title_text);
        mTitleTextView.setText(getString(R.string.schedule_title));
        mTitleTextView.setTypeface(Utilz.getTypefaceBold(ScheduleActivity.this));

        iconFont = FontManager.getTypeface(getApplicationContext(), FontManager.FONTAWESOME);

        TextView tvBack = (TextView) mCustomView.findViewById(R.id.font_awesome_android_icon);
        tvBack.setVisibility(View.VISIBLE);
        tvBack.setTypeface(iconFont);
        tvBack.setOnClickListener(backClickListener);

        mActionBar.setCustomView(mCustomView);
        mActionBar.setDisplayShowCustomEnabled(true);

        prepareWeekData();
        initViews();

        getWeekSchedule(true);
    }

    private void initViews() {

        tvScheduleTitle = (TextView) findViewById(R.id.tvScheduleTitle);
        tvScheduleTitle.setTypeface(Utilz.getTypefaceMedium(ScheduleActivity.this));
        if(!ifEditable)
            tvScheduleTitle.setText(friend_name != null ? friend_name : "");

        btnMon = (RadioButton) findViewById(R.id.btnMon);
        btnMon.setOnClickListener(this);
        btnTue = (RadioButton) findViewById(R.id.btnTue);
        btnTue.setOnClickListener(this);
        btnWed = (RadioButton) findViewById(R.id.btnWed);
        btnWed.setOnClickListener(this);
        btnThu = (RadioButton) findViewById(R.id.btnThu);
        btnThu.setOnClickListener(this);
        btnFri = (RadioButton) findViewById(R.id.btnFri);
        btnFri.setOnClickListener(this);
        btnSat = (RadioButton) findViewById(R.id.btnSat);
        btnSat.setOnClickListener(this);
        btnSun = (RadioButton) findViewById(R.id.btnSun);
        btnSun.setOnClickListener(this);
    }

    View.OnClickListener backClickListener = new View.OnClickListener() {
        @Override
        public void onClick(View v) {
            onBackPressed();
        }
    };

    @Override
    public void onBackPressed() {
        if (Utilz.getBooleanFromUserDefaults(ScheduleActivity.this, Constants.MM_is_new_register)) {
            Utilz.saveBooleanToUserDefaults(ScheduleActivity.this, Constants.MM_is_new_register, false);
            startActivity(new Intent(ScheduleActivity.this, MainActivity.class));
            overridePendingTransition(R.anim.anim_slide_in_right,
                    R.anim.anim_slide_out_right);
            finish();
        } else {
            finish();
        }
        super.onBackPressed();
    }

    public void prepareWeekData() {
        weekDays = new ArrayList<>();

        ArrayList<BreakTime> myScheduleList = new ArrayList<>();

        String[] myResArray = getResources().getStringArray(R.array.schedule_list);
        List<String> scheduleTimes = Arrays.asList(myResArray);

        for (int i = 0; i < scheduleTimes.size() - 1; i++) {
            BreakTime breakTime = new BreakTime();
            breakTime.setStartTime(scheduleTimes.get(i));
            breakTime.setEndTime(scheduleTimes.get(i + 1));
            myScheduleList.add(breakTime);
        }

        for (int j = 0; j < 8; j++) {
            WeekSchedule schedule = new WeekSchedule();
            schedule.setScheduleList(myScheduleList);
            weekDays.add(schedule);
        }
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.btnMon:
                currentDay = 1;
                ScheduleFragment fr1 = ScheduleFragment.getInstance(currentDay, weekDays.get(currentDay - 1));
                addFragment(fr1);
                break;

            case R.id.btnTue:
                currentDay = 2;
                ScheduleFragment fr2 = ScheduleFragment.getInstance(currentDay, weekDays.get(currentDay - 1));
                addFragment(fr2);
                break;

            case R.id.btnWed:
                currentDay = 3;
                ScheduleFragment fr3 = ScheduleFragment.getInstance(currentDay, weekDays.get(currentDay - 1));
                addFragment(fr3);
                break;

            case R.id.btnThu:
                currentDay = 4;
                ScheduleFragment fr4 = ScheduleFragment.getInstance(currentDay, weekDays.get(currentDay - 1));
                addFragment(fr4);
                break;

            case R.id.btnFri:
                currentDay = 5;
                ScheduleFragment fr5 = ScheduleFragment.getInstance(currentDay, weekDays.get(currentDay - 1));
                addFragment(fr5);
                break;

            case R.id.btnSat:
                currentDay = 6;
                ScheduleFragment fr6 = ScheduleFragment.getInstance(currentDay, weekDays.get(currentDay - 1));
                addFragment(fr6);
                break;

            case R.id.btnSun:
                currentDay = 7;
                ScheduleFragment fr7 = ScheduleFragment.getInstance(currentDay, weekDays.get(currentDay - 1));
                addFragment(fr7);
                break;
        }
    }

    public void addFragment(Fragment fr) {
        FragmentManager fm = getSupportFragmentManager();
        FragmentTransaction fragmentTransaction = fm.beginTransaction();
        fragmentTransaction.replace(R.id.fragment_switch, fr);
        fragmentTransaction.commit();
    }

    public void continueClick(int day) {
        if (day == 7) {
            getWeekSchedule(false);
            Utilz.showMessage2(ScheduleActivity.this, getString(R.string.break_schedule_saved));
        } else {
            currentDay = day + 1;
            selectRadioButton();
//            ScheduleFragment fragment = ScheduleFragment.getInstance(currentDay, weekDays.get(currentDay - 1));
//            addFragment(fragment);
            getWeekSchedule(false);
        }
    }

    @Override
    public void onContinueClick(int day) {
        if (day == 7) {
            getWeekSchedule(false);
            Utilz.showMessage2(ScheduleActivity.this, getString(R.string.break_schedule_saved));
        } else {
            currentDay = day + 1;
            selectRadioButton();
//            ScheduleFragment fragment = ScheduleFragment.getInstance(currentDay, weekDays.get(currentDay - 1));
//            addFragment(fragment);
            getWeekSchedule(false);
        }
    }

    private void selectRadioButton() {
        if (currentDay == 1)
            btnMon.setChecked(true);
        else if (currentDay == 2)
            btnTue.setChecked(true);
        else if (currentDay == 3)
            btnWed.setChecked(true);
        else if (currentDay == 4)
            btnThu.setChecked(true);
        else if (currentDay == 5)
            btnFri.setChecked(true);
        else if (currentDay == 6)
            btnSat.setChecked(true);
        else if (currentDay == 7)
            btnSun.setChecked(true);
    }


    public void getWeekSchedule(boolean showProgress) {

        if (!Utilz.isNetworkAvailable(ScheduleActivity.this)) {
            Utilz.showMessage2(ScheduleActivity.this, getString(R.string.network_error));
            return;
        }
        if (showProgress)
            Utilz.showProgressDialog(ScheduleActivity.this);

        AsyncHttpClient client = new AsyncHttpClient();
        JSONObject objMain = new JSONObject();
        try {
            objMain.put(Constants.MM_user_id, Integer.parseInt(ScheduleActivity.userId));
            StringEntity entity = new StringEntity(objMain.toString());
            client.post(ScheduleActivity.this, Constants.Base_url + Constants.API_get_break_schedule, entity, "application/json", new ScheduleResultHandler());

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

                    if (response.has(Constants.MM_break_schedules)) {
                        JSONArray arrSchedule = response.getJSONArray(Constants.MM_break_schedules);
                        Type listType = new TypeToken<List<BreakSchedules>>() {
                        }.getType();
                        breakSchedules = (List<BreakSchedules>) new Gson().fromJson(arrSchedule.toString(), listType);
                    }

                    if (response.has(Constants.MM_leave_schedules)) {
                        JSONArray arrSchedule = response.getJSONArray(Constants.MM_leave_schedules);
                        Type listType = new TypeToken<List<LeaveSchedules>>() {
                        }.getType();
                        leaveSchedules = (List<LeaveSchedules>) new Gson().fromJson(arrSchedule.toString(), listType);
                    }

                    ScheduleFragment fragment = ScheduleFragment.getInstance(currentDay, weekDays.get(currentDay - 1));
                    addFragment(fragment);

                } else {
                    Utilz.showMessage2(ScheduleActivity.this, response.get(Constants.MM_msg).toString());
                }
            } catch (Exception ex) {
                ex.printStackTrace();
            }
        }

        @Override
        public void onFailure(int statusCode, Header[] headers, byte[] responseBody, Throwable error) {
            Utilz.closeProgressDialog();
            Toast.makeText(ScheduleActivity.this, getString(R.string.server_error), Toast.LENGTH_SHORT).show();
        }
    }

}
