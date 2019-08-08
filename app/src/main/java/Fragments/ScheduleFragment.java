package Fragments;

import android.content.DialogInterface;
import android.graphics.Typeface;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v7.app.AlertDialog;
import android.support.v7.widget.DefaultItemAnimator;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.breakbuddy.R;
import com.breakbuddy.UI.MyFriendsActivity;
import com.breakbuddy.UI.ScheduleActivity;
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

import Adapters.ScheduleAdapter;
import Model.BreakSchedules;
import Model.BreakTime;
import Model.LeaveSchedules;
import Model.Schedule;
import Model.WeekSchedule;
import QuickScroll.Utils;
import Utils.Constants;
import Utils.FontManager;
import Utils.RecyclerItemClickListener;
import Utils.SimpleDividerItemDecoration;
import Utils.Utilz;
import cz.msebera.android.httpclient.Header;
import cz.msebera.android.httpclient.entity.StringEntity;

public class ScheduleFragment extends Fragment {

    View view;
    int scheduleDay = 0;
    private RecyclerView rlscheduleList;
    ArrayList<BreakTime> scheduleList;
    ScheduleAdapter mAdapter;
    TextView tvDown, tvEndTime, tvEnding;
    Typeface iconFont;
    Button btnContinue;
    LinearLayout llTimea;
    List<String> scheduleTimes;
    WeekSchedule schedule;

    public static ScheduleFragment getInstance(int id, WeekSchedule schedule) {
        ScheduleFragment fr = new ScheduleFragment();
        Bundle bundle = new Bundle();
        bundle.putInt(Constants.MM_Schedule_Day_id, id);
        bundle.putString(Constants.MM_week_schedeule, new Gson().toJson(schedule));
        fr.setArguments(bundle);
        return fr;
    }

    @Override
    public void onDetach() {
        Log.i("detached", scheduleDay + "");
        ScheduleActivity.weekDays.set(scheduleDay - 1, schedule);
        super.onDetach();
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        view = inflater.inflate(R.layout.schedule_fragment_layout, container,
                false);
        scheduleDay = getArguments().getInt(Constants.MM_Schedule_Day_id);
        schedule = (WeekSchedule) new Gson().fromJson(getArguments().getString(Constants.MM_week_schedeule), WeekSchedule.class);
        initViews();
        prepareScheduleListData();

//        getSchedukeForDay();
        return view;
    }

    private void prepareScheduleListData() {
        String[] myResArray = getResources().getStringArray(R.array.schedule_list2);
        scheduleTimes = Arrays.asList(myResArray);
        setAdapter();
    }

    private void initViews() {
        iconFont = FontManager.getTypeface(getActivity(), FontManager.FONTAWESOME);

        rlscheduleList = (RecyclerView) view.findViewById(R.id.scheduleList);
        RecyclerView.LayoutManager mLayoutManager = new LinearLayoutManager(getActivity());
        rlscheduleList.setLayoutManager(mLayoutManager);
        rlscheduleList.addItemDecoration(new SimpleDividerItemDecoration(getActivity()));
        rlscheduleList.setItemAnimator(new DefaultItemAnimator());

        tvDown = (TextView) view.findViewById(R.id.tvDown);
        tvDown.setTypeface(iconFont);
        tvEndTime = (TextView) view.findViewById(R.id.tvEndTime);
        tvEnding = (TextView) view.findViewById(R.id.tvEnding);
        tvEndTime.setTypeface(Utilz.getTypefaceMedium(getActivity()));
        tvEnding.setTypeface(Utilz.getTypefaceMedium(getActivity()));

        if (schedule.getEndTime() != null && !schedule.getEndTime().isEmpty())
            tvEndTime.setText(schedule.getEndTime());


        btnContinue = (Button) view.findViewById(R.id.btnContinue);
        btnContinue.setTypeface(Utilz.getTypefaceBold(getActivity()));
        llTimea = (LinearLayout) view.findViewById(R.id.llTimea);
        btnContinue.setOnClickListener(continueClickListener);

        if (!ScheduleActivity.ifEditable) {
            btnContinue.setVisibility(View.GONE);
        } else {
            llTimea.setOnClickListener(selectTimeClickListener);
        }


        rlscheduleList.addOnItemTouchListener(
                new RecyclerItemClickListener(getActivity(), rlscheduleList, new RecyclerItemClickListener.OnItemClickListener() {
                    @Override
                    public void onItemClick(View view, int position) {
                        if (ScheduleActivity.ifEditable) {
                            if (scheduleList.get(position).isEnable()) {
                                scheduleList.get(position).setEnable(false);
                                mAdapter.refreshAdapter(scheduleList);
                            } else {
                                scheduleList.get(position).setEnable(true);
                                mAdapter.refreshAdapter(scheduleList);
                            }
                        }
                    }

                    @Override
                    public void onLongItemClick(View view, int position) {
                    }
                })
        );

        scheduleList = new ArrayList<>();
        scheduleList = schedule.getScheduleList();
        mAdapter = new ScheduleAdapter(getActivity(), scheduleList);
        rlscheduleList.setAdapter(mAdapter);
    }

    private void setAdapter() {
        for (int i = 0; i < scheduleList.size(); i++) {
            BreakTime breakTime = scheduleList.get(i);
            for (BreakSchedules.schedules schedule : ScheduleActivity.breakSchedules.get(scheduleDay - 1).getSchedules()) {
                if (breakTime.getStartTime().equalsIgnoreCase(schedule.getFrom_time()) && breakTime.getEndTime().equalsIgnoreCase(schedule.getTo_time())) {
                    breakTime.setEnable(true);
                    scheduleList.set(i, breakTime);
                }
            }
        }
        mAdapter.refreshAdapter(scheduleList);

        String endTime = "N/A";
        for (LeaveSchedules leaveSchedules : ScheduleActivity.leaveSchedules) {
            if (leaveSchedules.getWeekday_id().equalsIgnoreCase(String.valueOf(scheduleDay))) {
                if (leaveSchedules.getLeave_time() != null && !leaveSchedules.getLeave_time().isEmpty())
                    endTime = leaveSchedules.getLeave_time();
            }
        }
        tvEndTime.setText(endTime);
    }

    View.OnClickListener selectTimeClickListener = new View.OnClickListener() {
        @Override
        public void onClick(View v) {
            AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());
            builder.setTitle(R.string.select_end_time)
                    .setNegativeButton(android.R.string.cancel, new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialogInterface, int i) {
                            dialogInterface.dismiss();
                        }
                    }).setItems(R.array.schedule_list2, new DialogInterface.OnClickListener() {
                public void onClick(DialogInterface dialog, int which) {
                    dialog.dismiss();
                    tvEndTime.setText(scheduleTimes.get(which));
                }
            });
            builder.create();
            builder.show();
        }
    };

    View.OnClickListener continueClickListener = new View.OnClickListener() {
        @Override
        public void onClick(View v) {
//            ArrayList<BreakTime> breakTimes = mAdapter.getScheduleData();
//            WeekSchedule schedule = new WeekSchedule();
//            schedule.setScheduleList(breakTimes);
//            schedule.setEndTime(tvEndTime.getText().toString());
//            ((ScheduleActivity) getActivity()).continueClick(scheduleDay, schedule);

            try {
                JSONArray schedulesArray = new JSONArray();
                for (int i = 0; i < scheduleList.size(); i++) {
                    JSONObject obj = new JSONObject();
                    obj.put(Constants.MM_weekday_id, String.valueOf(scheduleDay));
                    obj.put(Constants.MM_from_time, scheduleList.get(i).getStartTime());
                    obj.put(Constants.MM_to_time, scheduleList.get(i).getEndTime());
                    obj.put(Constants.MM_status, scheduleList.get(i).isEnable() ? "1" : "0");
                    schedulesArray.put(obj);
                }
                UpdateSchedukeForDay(schedulesArray);
            } catch (Exception ex) {
                ex.printStackTrace();
            }

        }
    };


    public void UpdateSchedukeForDay(JSONArray schedulesArray) {

        if (!Utilz.isNetworkAvailable(getActivity())) {
            Utilz.showMessage2(getActivity(), getString(R.string.network_error));
            return;
        }
        Utilz.showProgressDialog(getActivity());
        AsyncHttpClient client = new AsyncHttpClient();
        JSONObject objMain = new JSONObject();
        try {
            JSONArray objLeaveObj = new JSONArray();
            JSONObject jsonObject = new JSONObject();
            jsonObject.put(Constants.MM_weekday_id, String.valueOf(scheduleDay));
            jsonObject.put(Constants.MM_leave_time, tvEndTime.getText().toString().trim());
            objLeaveObj.put(jsonObject);

            objMain.put(Constants.MM_user_id, Integer.parseInt(ScheduleActivity.userId));
            objMain.put(Constants.MM_schedules, schedulesArray);
            objMain.put(Constants.MM_leave_schedules, objLeaveObj);
            StringEntity entity = new StringEntity(objMain.toString());
            client.post(getActivity(), Constants.Base_url + Constants.API_update_break_schedule, entity, "application/json", new ScheduleResultHandler());

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
                    ScheduleActivity.continueClickListener.onContinueClick(scheduleDay);
                } else {
                    Utilz.showMessage2(getActivity(), response.get(Constants.MM_msg).toString());
                }
            } catch (Exception ex) {
                ex.printStackTrace();
            }
        }

        @Override
        public void onFailure(int statusCode, Header[] headers, byte[] responseBody, Throwable error) {
            Utilz.closeProgressDialog();
            Toast.makeText(getActivity(), getString(R.string.server_error), Toast.LENGTH_SHORT).show();
        }
    }


}
