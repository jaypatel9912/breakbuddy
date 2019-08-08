package com.breakbuddy.UI;

import android.Manifest;
import android.annotation.TargetApi;
import android.app.Dialog;
import android.content.ContentResolver;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.graphics.Color;
import android.graphics.Typeface;
import android.graphics.drawable.BitmapDrawable;
import android.graphics.drawable.ColorDrawable;
import android.graphics.drawable.Drawable;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Build;
import android.os.Handler;
import android.provider.ContactsContract;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AlertDialog;
import android.os.Bundle;
import android.support.v7.widget.PopupMenu;
import android.util.Log;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.MenuItem;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.CompoundButton;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.PopupWindow;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import com.breakbuddy.R;
import com.daimajia.swipe.SwipeLayout;
import com.kyleduo.switchbutton.SwitchButton;
import com.loopj.android.http.AsyncHttpClient;
import com.loopj.android.http.AsyncHttpResponseHandler;

import org.json.JSONObject;
import org.w3c.dom.Text;

import java.lang.reflect.Field;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.HashMap;
import java.util.List;

import API.GetContacts;
import Model.Contact;
import QuickScroll.Utils;
import Utils.Constants;
import Utils.FontManager;
import Utils.Utilz;
import cz.msebera.android.httpclient.Header;
import cz.msebera.android.httpclient.entity.StringEntity;

public class MainActivity extends BaseActivity implements View.OnClickListener, CompoundButton.OnCheckedChangeListener {

    TextView tvUp, tvDown, font_awesome_android_icon_right, tvmain2Title;
    Typeface iconFont, bold, medium;
    TextView tvIcAddme, tvIcAddfrnds, tvIcMyFrnds, tvIcEditSchedule, tvAddedmeCount;
    LinearLayout llAddedMe, llAddfrnds, llMyFrnds, llEditSchedule, llFriendsOnBreak;
    TextView day1, day2, day3, day4, day5, day6, day7, today, tvInfo, tvFrndsOnline;

    TextView tvAddedMe, tvAddFriend, tvMyFrnd, tvEditSchedule, tvBeOnline, tvHideSchedule, frnds;

    SwitchButton sb_set_as_on_break, sb_hide_break_schedule;
    CompoundButton.OnCheckedChangeListener checkedChangeListener;
    ProgressBar pbOnline, pbHide;

    SwipeLayout sample1;

    Drawable draw;
    boolean previousStateOnline = false;
    boolean previousStateHide = false;

    int currDay = 0;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        checkedChangeListener = this;
        initViews();

        Calendar calendar = Calendar.getInstance();
        currDay = calendar.get(Calendar.DAY_OF_WEEK);

        if (currDay == 1) {
            currDay = 7;
        } else {
            currDay--;
        }

        switch (currDay) {
            case 1:
                day1.setVisibility(View.GONE);
                break;
            case 2:
                day2.setVisibility(View.GONE);
                break;
            case 3:
                day3.setVisibility(View.GONE);
                break;
            case 4:
                day4.setVisibility(View.GONE);
                break;
            case 5:
                day5.setVisibility(View.GONE);
                break;
            case 6:
                day6.setVisibility(View.GONE);
                break;
            case 7:
                day7.setVisibility(View.GONE);
                break;
        }
    }

    public void initViews() {
        iconFont = FontManager.getTypeface(getApplicationContext(), FontManager.FONTAWESOME);
        bold = Utilz.getTypefaceBold(MainActivity.this);
        medium = Utilz.getTypefaceMedium(MainActivity.this);

        sample1 = (SwipeLayout) findViewById(R.id.godfather);
        sample1.setDragEdge(SwipeLayout.DragEdge.Bottom);
        sample1.setShowMode(SwipeLayout.ShowMode.LayDown);
        sample1.setLeftSwipeEnabled(false);
        sample1.setRightSwipeEnabled(false);
        sample1.addDrag(SwipeLayout.DragEdge.Bottom, findViewById(R.id.mainRl));

        sb_set_as_on_break = (SwitchButton) findViewById(R.id.sb_set_as_on_break);

        sb_hide_break_schedule = (SwitchButton) findViewById(R.id.sb_hide_break_schedule);


        if (Utilz.getUserDetails(MainActivity.this).containsKey(Constants.MM_is_online_offline)) {
            if (Utilz.getUserDetails(MainActivity.this).get(Constants.MM_is_online_offline).equalsIgnoreCase("1")) {
                sb_set_as_on_break.setChecked(true);
                previousStateOnline = true;
            }
        }

        if (Utilz.getUserDetails(MainActivity.this).containsKey(Constants.MM_is_show_hide)) {
            if (Utilz.getUserDetails(MainActivity.this).get(Constants.MM_is_show_hide).equalsIgnoreCase("1")) {
                sb_hide_break_schedule.setChecked(true);
                previousStateHide = true;
            }
        }

        sb_hide_break_schedule.setOnCheckedChangeListener(checkedChangeListener);
        sb_set_as_on_break.setOnCheckedChangeListener(checkedChangeListener);

        pbOnline = (ProgressBar) findViewById(R.id.pbOnline);
        pbHide = (ProgressBar) findViewById(R.id.pbHide);

        llAddedMe = (LinearLayout) findViewById(R.id.llAddedMe);
        llAddedMe.setOnClickListener(addedMeClickListener);

        llAddfrnds = (LinearLayout) findViewById(R.id.llAddfrnds);
        llAddfrnds.setOnClickListener(addFriendsClickListener);

        llMyFrnds = (LinearLayout) findViewById(R.id.llMyFrnds);
        llMyFrnds.setOnClickListener(myFriendsClickListener);

        llEditSchedule = (LinearLayout) findViewById(R.id.llEditSchedule);
        llEditSchedule.setOnClickListener(editScheduleClickListener);

        llFriendsOnBreak = (LinearLayout) findViewById(R.id.llFriendsOnBreak);
        llFriendsOnBreak.setOnClickListener(friendsOnBreakClickListener);

        tvDown = (TextView) findViewById(R.id.tvDown);
        tvDown.setTypeface(iconFont);

        tvUp = (TextView) findViewById(R.id.tvUp);
        tvUp.setTypeface(iconFont);

        font_awesome_android_icon_right = (TextView) findViewById(R.id.font_awesome_android_icon_right);
        font_awesome_android_icon_right.setTypeface(iconFont);
        font_awesome_android_icon_right.setOnClickListener(settingClickListener);

        tvIcAddme = (TextView) findViewById(R.id.tvIcAddme);
        tvIcAddme.setTypeface(iconFont);

        tvmain2Title = (TextView) findViewById(R.id.tvmain2Title);
        tvmain2Title.setTypeface(bold);


        tvIcAddfrnds = (TextView) findViewById(R.id.tvIcAddfrnds);
        tvIcAddfrnds.setTypeface(iconFont);

        tvIcMyFrnds = (TextView) findViewById(R.id.tvIcMyFrnds);
        tvIcMyFrnds.setTypeface(iconFont);

        tvIcEditSchedule = (TextView) findViewById(R.id.tvIcEditSchedule);
        tvIcEditSchedule.setTypeface(iconFont);


        tvAddedmeCount = (TextView) findViewById(R.id.tvAddedmeCount);

        if (Utilz.getUserDetails(MainActivity.this).containsKey(Constants.MM_added_me_count)) {
            int count = Integer.parseInt(Utilz.getUserDetails(MainActivity.this).get(Constants.MM_added_me_count));
            if (count > 0) {
                tvAddedmeCount.setVisibility(View.VISIBLE);
                tvAddedmeCount.setText(String.valueOf(count));
            } else {
                tvAddedmeCount.setVisibility(View.GONE);
            }
        }

        tvAddedMe = (TextView) findViewById(R.id.tvAddedMe);
        tvAddedMe.setTypeface(bold);
        tvAddFriend = (TextView) findViewById(R.id.tvAddFriend);
        tvAddFriend.setTypeface(bold);
        tvMyFrnd = (TextView) findViewById(R.id.tvMyFrnd);
        tvMyFrnd.setTypeface(bold);
        tvEditSchedule = (TextView) findViewById(R.id.tvEditSchedule);
        tvEditSchedule.setTypeface(bold);
        tvBeOnline = (TextView) findViewById(R.id.tvBeOnline);
        tvBeOnline.setTypeface(bold);
        tvHideSchedule = (TextView) findViewById(R.id.tvHideSchedule);
        tvHideSchedule.setTypeface(bold);
        frnds = (TextView) findViewById(R.id.frnds);
        frnds.setTypeface(bold);

        tvUp.setOnClickListener(downArrowListener);
        tvDown.setOnClickListener(UpArrowListener);

        day1 = (TextView) findViewById(R.id.day1);
        day1.setOnClickListener(this);
        day1.setTypeface(bold);

        day2 = (TextView) findViewById(R.id.day2);
        day2.setOnClickListener(this);
        day2.setTypeface(bold);

        day3 = (TextView) findViewById(R.id.day3);
        day3.setOnClickListener(this);
        day3.setTypeface(bold);

        day4 = (TextView) findViewById(R.id.day4);
        day4.setOnClickListener(this);
        day4.setTypeface(bold);

        day5 = (TextView) findViewById(R.id.day5);
        day5.setOnClickListener(this);
        day5.setTypeface(bold);

        day6 = (TextView) findViewById(R.id.day6);
        day6.setOnClickListener(this);
        day6.setTypeface(bold);

        day7 = (TextView) findViewById(R.id.day7);
        day7.setOnClickListener(this);
        day7.setTypeface(bold);

        today = (TextView) findViewById(R.id.today);
        today.setOnClickListener(this);
        today.setTypeface(bold);

        tvFrndsOnline = (TextView) findViewById(R.id.today);
        tvFrndsOnline.setTypeface(bold);

        tvInfo = (TextView) findViewById(R.id.tvInfo);
        tvInfo.setVisibility(View.VISIBLE);
        tvInfo.setTypeface(iconFont);
        tvInfo.setOnClickListener(infoCliclkListener);
    }

    PopupWindow popupWindowinfo;
    boolean isinfopopupshown = false;

    View.OnClickListener infoCliclkListener = new View.OnClickListener() {
        @Override
        public void onClick(View v) {

            if (isinfopopupshown) {
                isinfopopupshown = false;
                popupWindowinfo.dismiss();
                return;
            }

            LayoutInflater layoutInflater = (LayoutInflater) getSystemService(Context.LAYOUT_INFLATER_SERVICE);
            View popupView = layoutInflater.inflate(R.layout.signature_layout, null);

            popupWindowinfo = new PopupWindow(
                    popupView,
                    ViewGroup.LayoutParams.WRAP_CONTENT,
                    ViewGroup.LayoutParams.WRAP_CONTENT);

            popupWindowinfo.setBackgroundDrawable(new BitmapDrawable());
            popupWindowinfo.setOutsideTouchable(true);
            popupWindowinfo.setOnDismissListener(new PopupWindow.OnDismissListener() {
                @Override
                public void onDismiss() {
                    //TODO do sth here on dismiss
                }
            });


            TextView tv = (TextView) popupView.findViewById(R.id.tvPopContactUs);
            tv.setTypeface(bold);
            LinearLayout llContactUs = (LinearLayout) popupView.findViewById(R.id.llContactUs);
            llContactUs.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    Intent intent = new Intent(Intent.ACTION_SEND, Uri.fromParts("mailto", "info@breakbuddyapp.com", null));
                    intent.setType("text/html");
                    intent.putExtra(Intent.EXTRA_EMAIL, new String[]{"info@breakbuddyapp.com"});
                    intent.putExtra(Intent.EXTRA_SUBJECT, "Support");
                    intent.putExtra(Intent.EXTRA_TEXT, "");

                    startActivity(Intent.createChooser(intent, "Send Email"));
                    popupWindowinfo.dismiss();
                }
            });

            popupWindowinfo.showAsDropDown(v);
            isinfopopupshown = true;
        }
    };


    @Override
    public void onClick(View v) {
        int i = 1;
        switch (v.getId()) {
            case R.id.day1:
                i = 1;
                break;

            case R.id.day2:
                i = 2;
                break;

            case R.id.day3:
                i = 3;
                break;

            case R.id.day4:
                i = 4;
                break;

            case R.id.day5:
                i = 5;
                break;

            case R.id.day6:
                i = 6;
                break;

            case R.id.day7:
                i = 7;
                break;

            case R.id.today:
                i = currDay;
                break;
        }

        Intent intent = new Intent(MainActivity.this, BreakTimeDetailsActivity.class);
        intent.putExtra(Constants.MM_day_id, i);
        startActivity(intent);

    }


    boolean doubleBackToExitPressedOnce = false;

    @Override
    public void onBackPressed() {
        if (doubleBackToExitPressedOnce) {
            super.onBackPressed();
            return;
        }

        this.doubleBackToExitPressedOnce = true;
        Toast.makeText(this, getString(R.string.back_press_exit), Toast.LENGTH_SHORT).show();

        new Handler().postDelayed(new Runnable() {

            @Override
            public void run() {
                if (!isFinishing())
                    doubleBackToExitPressedOnce = false;
            }
        }, 2000);
    }

    View.OnClickListener addedMeClickListener = new View.OnClickListener() {
        @Override
        public void onClick(View v) {
            HashMap<String, String> userDetail = Utilz.getUserDetails(MainActivity.this);
            userDetail.put(Constants.MM_added_me_count, "0");
            Utilz.setUserDetails(MainActivity.this, userDetail);
            tvAddedmeCount.setVisibility(View.INVISIBLE);
            startActivity(new Intent(MainActivity.this, AddedMeActivity.class));
        }
    };

    View.OnClickListener addFriendsClickListener = new View.OnClickListener() {
        @Override
        public void onClick(View v) {
            showAddContactDialoog();
        }
    };

    View.OnClickListener myFriendsClickListener = new View.OnClickListener() {
        @Override
        public void onClick(View v) {
            startActivity(new Intent(MainActivity.this, MyFriendsActivity.class));
        }
    };

    View.OnClickListener editScheduleClickListener = new View.OnClickListener() {
        @Override
        public void onClick(View v) {
            startActivity(new Intent(MainActivity.this, ScheduleActivity.class));
            overridePendingTransition(R.anim.anim_slide_in_left,
                    R.anim.anim_slide_out_left);
        }
    };

    View.OnClickListener friendsOnBreakClickListener = new View.OnClickListener() {
        @Override
        public void onClick(View v) {
            startActivity(new Intent(MainActivity.this, FriendsCurrentlyOnBreakActivity.class));
        }
    };

    View.OnClickListener downArrowListener = new View.OnClickListener() {
        @Override
        public void onClick(View v) {
//            mPanel.setPanelState(SlidingUpPanelLayout.PanelState.EXPANDED);
            sample1.toggle();
        }
    };

    View.OnClickListener UpArrowListener = new View.OnClickListener() {
        @Override
        public void onClick(View v) {
//            mPanel.setPanelState(SlidingUpPanelLayout.PanelState.COLLAPSED);
            sample1.toggle();
        }
    };


    PopupWindow popupWindowSetting;
    boolean isSettingpopupshown = false;

    View.OnClickListener settingClickListener = new View.OnClickListener() {
        @Override
        public void onClick(View v) {

            if (isSettingpopupshown) {
                isSettingpopupshown = false;
                popupWindowSetting.dismiss();
                return;
            }

            LayoutInflater layoutInflater = (LayoutInflater) getSystemService(Context.LAYOUT_INFLATER_SERVICE);
            final View popupView = layoutInflater.inflate(R.layout.main_option_menu, null);

            popupWindowSetting = new PopupWindow(
                    popupView,
                    ViewGroup.LayoutParams.WRAP_CONTENT,
                    ViewGroup.LayoutParams.WRAP_CONTENT);

            popupWindowSetting.setBackgroundDrawable(new BitmapDrawable());
            popupWindowSetting.setOutsideTouchable(true);
            popupWindowSetting.setOnDismissListener(new PopupWindow.OnDismissListener() {
                @Override
                public void onDismiss() {
                    //TODO do sth here on dismiss
                }
            });

            TextView tvUsername, tvUserEmail, tvCP, tvCL, tvCE;

            tvCP = (TextView) popupView.findViewById(R.id.tvCP);
            tvCP.setTypeface(bold);
            tvCL = (TextView) popupView.findViewById(R.id.tvCL);
            tvCL.setTypeface(bold);
            tvCE = (TextView) popupView.findViewById(R.id.tvCE);
            tvCE.setTypeface(bold);

            tvUsername = (TextView) popupView.findViewById(R.id.tvUsername);
            String fname = Utilz.getUserDetails(MainActivity.this).get(Constants.MM_firstname);
            String lname = Utilz.getUserDetails(MainActivity.this).get(Constants.MM_lastname);
            tvUsername.setText(fname.substring(0, 1).toUpperCase() + fname.substring(1, fname.length()) + " " + lname);
            tvUsername.setTypeface(Utilz.getTypefaceMedium(MainActivity.this));

            tvUserEmail = (TextView) popupView.findViewById(R.id.tvUserEmail);
            tvUserEmail.setText(Utilz.getUserDetails(MainActivity.this).get(Constants.MM_email));
            tvUserEmail.setTypeface(Utilz.getTypefaceMedium(MainActivity.this));

            LinearLayout llChangePassword = (LinearLayout) popupView.findViewById(R.id.llChangePassword);
            llChangePassword.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    popupWindowSetting.dismiss();
                    isSettingpopupshown = false;
                    showChangePasswordDialog();
                }
            });

            LinearLayout llChangeEmail = (LinearLayout) popupView.findViewById(R.id.llChangeEmail);
            llChangeEmail.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    popupWindowSetting.dismiss();
                    isSettingpopupshown = false;
                    showChangeEmailDialog();
                }
            });

            LinearLayout llLogout = (LinearLayout) popupView.findViewById(R.id.llLogout);
            llLogout.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    popupWindowSetting.dismiss();
                    isSettingpopupshown = false;
                    Utilz.setUserDetails(MainActivity.this, null);
                    startActivity(new Intent(MainActivity.this, LoginActivity.class));
                    finish();
                }
            });
            popupWindowSetting.showAsDropDown(v);
            isSettingpopupshown = true;
        }
    };


    private void changePasswordRequest(String oldPass, String newPass) {
        Utilz.showProgressDialog(MainActivity.this);
        AsyncHttpClient client = new AsyncHttpClient();
        JSONObject objMain = new JSONObject();
        try {
            objMain.put(Constants.MM_user_id, Utilz.getUserDetails(MainActivity.this).get(Constants.MM_user_id));
            objMain.put(Constants.MM_password, newPass);
            objMain.put(Constants.MM_old_password, oldPass);

            StringEntity entity = new StringEntity(objMain.toString());
            client.post(MainActivity.this, Constants.Base_url + Constants.API_change_password, entity, "application/json", new ChangePasswordHandler());

        } catch (Exception e) {
            e.printStackTrace();
            Utilz.closeProgressDialog();
        }
    }

    @Override
    public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {

        if (!Utilz.isNetworkAvailable(MainActivity.this)) {
            Utilz.showMessage2(MainActivity.this, getString(R.string.network_error));
            return;
        }


        switch (buttonView.getId()) {
            case R.id.sb_hide_break_schedule:
                previousStateHide = isChecked;

                if (isChecked) {
                    makeUserScheduleHideOROnline("1", "0", false);
                    sb_set_as_on_break.setOnCheckedChangeListener(null);
                    sb_set_as_on_break.setChecked(false);
                    sb_set_as_on_break.setEnabled(false);
                } else
                    makeUserScheduleHideOROnline("0", sb_hide_break_schedule.isChecked() ? "1" : "0", false);
                break;
            case R.id.sb_set_as_on_break:
                previousStateOnline = isChecked;

                if (isChecked) {
                    makeUserScheduleHideOROnline("0", "1", true);
                    sb_hide_break_schedule.setOnCheckedChangeListener(null);
                    sb_hide_break_schedule.setChecked(false);
                    sb_hide_break_schedule.setEnabled(false);
                } else
                    makeUserScheduleHideOROnline(sb_set_as_on_break.isChecked() ? "1" : "0", "0", true);
                break;
        }
    }

    private class ChangePasswordHandler extends AsyncHttpResponseHandler {

        @Override
        public void onSuccess(int statusCode, Header[] headers, byte[] responseBody) {
            Utilz.closeProgressDialog();
            JSONObject response = null;
            try {
                response = new JSONObject(new String(responseBody));
                Log.i("response.) ", response.toString());
                if (response.has(Constants.MM_STATUS_CODE) && response.getString(Constants.MM_STATUS_CODE).equalsIgnoreCase(Constants.MM_STATUS_CODE_SUCCESS)) {
                    AlertDialog.Builder builder1 = new AlertDialog.Builder(MainActivity.this);
                    builder1.setTitle(getString(R.string.pass_change_title));
                    builder1.setMessage(getString(R.string.pass_change_msg));
                    builder1.setCancelable(false);
                    builder1.setPositiveButton(
                            android.R.string.ok,
                            new DialogInterface.OnClickListener() {
                                public void onClick(DialogInterface dialog, int id) {
                                    dialog.cancel();
                                    Utilz.setUserDetails(MainActivity.this, null);
                                    startActivity(new Intent(MainActivity.this, LoginActivity.class));
                                    finish();
                                }
                            });
                    AlertDialog alert11 = builder1.create();
                    alert11.show();
                } else {
                    Utilz.showMessage2(MainActivity.this, response.get(Constants.MM_msg).toString());
                }

            } catch (Exception ex) {
                ex.printStackTrace();
            }
        }

        @Override
        public void onFailure(int statusCode, Header[] headers, byte[] responseBody, Throwable error) {
            Utilz.closeProgressDialog();
            Toast.makeText(MainActivity.this, getString(R.string.server_error), Toast.LENGTH_SHORT).show();
        }
    }


    private void changeEmail(String newemail) {
        Utilz.showProgressDialog(MainActivity.this);
        AsyncHttpClient client = new AsyncHttpClient();
        JSONObject objMain = new JSONObject();
        try {
            objMain.put(Constants.MM_user_id, Utilz.getUserDetails(MainActivity.this).get(Constants.MM_user_id));
            objMain.put(Constants.MM_email, newemail);

            StringEntity entity = new StringEntity(objMain.toString());
            client.post(MainActivity.this, Constants.Base_url + Constants.API_update_user_email, entity, "application/json", new ChangeEmaildHandler());

        } catch (Exception e) {
            e.printStackTrace();
            Utilz.closeProgressDialog();
        }
    }

    private class ChangeEmaildHandler extends AsyncHttpResponseHandler {

        @Override
        public void onSuccess(int statusCode, Header[] headers, byte[] responseBody) {
            Utilz.closeProgressDialog();
            JSONObject response = null;
            try {
                response = new JSONObject(new String(responseBody));
                Log.i("response.) ", response.toString());
                if (response.has(Constants.MM_STATUS_CODE) && response.getString(Constants.MM_STATUS_CODE).equalsIgnoreCase(Constants.MM_STATUS_CODE_SUCCESS)) {
                    AlertDialog.Builder builder1 = new AlertDialog.Builder(MainActivity.this);
                    builder1.setTitle(getString(R.string.email_change_title));
                    builder1.setMessage(getString(R.string.email_change_msg));
                    builder1.setCancelable(false);
                    builder1.setPositiveButton(
                            android.R.string.ok,
                            new DialogInterface.OnClickListener() {
                                public void onClick(DialogInterface dialog, int id) {
                                    dialog.cancel();
                                    Utilz.setUserDetails(MainActivity.this, null);
                                    startActivity(new Intent(MainActivity.this, LoginActivity.class));
                                    finish();
                                }
                            });
                    AlertDialog alert11 = builder1.create();
                    alert11.show();
                } else {
                    Utilz.showMessage2(MainActivity.this, response.get(Constants.MM_msg).toString());
                }

            } catch (Exception ex) {
                ex.printStackTrace();
            }
        }

        @Override
        public void onFailure(int statusCode, Header[] headers, byte[] responseBody, Throwable error) {
            Utilz.closeProgressDialog();
            Toast.makeText(MainActivity.this, getString(R.string.server_error), Toast.LENGTH_SHORT).show();
        }
    }


    public void showAddContactDialoog() {
        final Dialog dialog = new Dialog(MainActivity.this);
        dialog.setContentView(R.layout.add_contact_dialog_layout);
        dialog.getWindow().setBackgroundDrawable(new ColorDrawable(android.graphics.Color.TRANSPARENT));

        TextView textView = (TextView) dialog.findViewById(R.id.textView);
        textView.setTypeface(bold);
        final TextView tvClose = (TextView) dialog.findViewById(R.id.tvClose);
        final TextView tvByContact = (TextView) dialog.findViewById(R.id.tvByContact);
        final TextView tvByEmail = (TextView) dialog.findViewById(R.id.tvByEmail);

        final TextView tvByContact2 = (TextView) dialog.findViewById(R.id.tvByContact2);
        final TextView tvByEmail2 = (TextView) dialog.findViewById(R.id.tvByEmail2);

        tvByContact2.setTypeface(bold);
        tvByEmail2.setTypeface(bold);
        tvClose.setTypeface(iconFont);
        tvByContact.setTypeface(iconFont);
        tvByEmail.setTypeface(iconFont);

        LinearLayout llAddContact = (LinearLayout) dialog.findViewById(R.id.llAddContact);
        LinearLayout llAddEmail = (LinearLayout) dialog.findViewById(R.id.llAddEmail);

        tvClose.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                dialog.dismiss();
            }
        });

        llAddContact.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                dialog.dismiss();
                startActivity(new Intent(MainActivity.this, AddContactActivity.class));
            }
        });

        llAddEmail.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                dialog.dismiss();
                showAddViaEmailDialog();
            }
        });

        dialog.show();
        dialog.setCancelable(true);


        WindowManager.LayoutParams lp = new WindowManager.LayoutParams();
        lp.copyFrom(dialog.getWindow().getAttributes());
        lp.width = WindowManager.LayoutParams.MATCH_PARENT;
        lp.height = WindowManager.LayoutParams.MATCH_PARENT;
        dialog.getWindow().setAttributes(lp);
        dialog.getWindow().setGravity(Gravity.CENTER);
    }

    private void showChangeEmailDialog() {


        final Dialog dialog = new Dialog(MainActivity.this);
//        insertOtpDialog.requestWindowFeature(Window.FEATURE_NO_TITLE);
        dialog.setContentView(R.layout.change_username_layout);
        dialog.getWindow().setBackgroundDrawable(new ColorDrawable(android.graphics.Color.TRANSPARENT));

        TextView cePopup = (TextView) dialog.findViewById(R.id.cePopup);
        cePopup.setTypeface(bold);

        final EditText edOldEmail = (EditText) dialog.findViewById(R.id.edOldEmail);
        edOldEmail.setTypeface(bold);
        final EditText edNewEmail = (EditText) dialog.findViewById(R.id.edNewEmail);
        edNewEmail.setTypeface(bold);
        final EditText edCNewEmail = (EditText) dialog.findViewById(R.id.edCNewEmail);
        edCNewEmail.setTypeface(bold);

        Button btnCancel = (Button) dialog.findViewById(R.id.btnCancel);
        btnCancel.setTypeface(bold);
        Button btnChange = (Button) dialog.findViewById(R.id.btnChange);
        btnChange.setTypeface(bold);

        btnCancel.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                dialog.dismiss();
            }
        });

        btnChange.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                String oldemail = edOldEmail.getText().toString();
                if (oldemail == null || oldemail.isEmpty()) {
                    edOldEmail.setError(getString(R.string.enter_email));
                    edOldEmail.requestFocus();
                    return;
                }

                if (!(Utilz.isValidEmail(oldemail))) {
                    edOldEmail.setError(getString(R.string.enter_email_valid2));
                    edOldEmail.requestFocus();
                    return;
                }

                if (!oldemail.equalsIgnoreCase(Utilz.getUserDetails(MainActivity.this).get(Constants.MM_email))) {
                    edOldEmail.setError(getString(R.string.enter_email_valid2));
                    edOldEmail.requestFocus();
                    return;
                }

                String newemail = edNewEmail.getText().toString();
                if (newemail == null || newemail.isEmpty()) {
                    edNewEmail.setError(getString(R.string.enter_new_email));
                    edNewEmail.requestFocus();
                    return;
                }

                if (!(Utilz.isValidEmail(newemail))) {
                    edNewEmail.setError(getString(R.string.enter_new_email_valid));
                    edNewEmail.requestFocus();
                    return;
                }

                if (oldemail.equalsIgnoreCase(newemail)) {
                    edNewEmail.setError(getString(R.string.enter_old_new_email));
                    edNewEmail.requestFocus();
                    return;
                }

                String cemail = edCNewEmail.getText().toString();

                if (!(Utilz.isValidEmail(cemail))) {
                    edCNewEmail.setError(getString(R.string.enter_confirm_email_valid));
                    edCNewEmail.requestFocus();
                    return;
                }

                if (!newemail.equalsIgnoreCase(cemail)) {
                    edCNewEmail.setError(getString(R.string.enter_valid_confirm_email));
                    edCNewEmail.requestFocus();
                    return;
                }

                dialog.dismiss();

                if (Utilz.isNetworkAvailable(MainActivity.this)) {
                    Utilz.hideSoftKeyboard(MainActivity.this);
                    changeEmail(newemail);
                } else {
                    Utilz.showMessage2(MainActivity.this, getString(R.string.network_error));
                }
            }
        });
        dialog.show();

        dialog.setCancelable(true);

        WindowManager.LayoutParams lp = new WindowManager.LayoutParams();
        lp.copyFrom(dialog.getWindow().getAttributes());
        lp.width = WindowManager.LayoutParams.MATCH_PARENT;
        lp.height = WindowManager.LayoutParams.MATCH_PARENT;
        dialog.getWindow().setAttributes(lp);
        dialog.getWindow().setGravity(Gravity.CENTER);
    }

    private void showChangePasswordDialog() {

        final Dialog dialog = new Dialog(MainActivity.this);
        dialog.setContentView(R.layout.change_password_layout);
        dialog.getWindow().setBackgroundDrawable(new ColorDrawable(android.graphics.Color.TRANSPARENT));

        TextView icinfo = (TextView) dialog.findViewById(R.id.icinfo);
        icinfo.setTypeface(iconFont);
        icinfo.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                AlertDialog userDialog;
                final AlertDialog.Builder builder = new AlertDialog.Builder(MainActivity.this);
                builder.setMessage(getString(R.string.password_hint)).setPositiveButton(android.R.string.ok, new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        dialog.dismiss();
                    }
                });
                userDialog = builder.create();
                userDialog.show();
            }
        });
        TextView cpTitle = (TextView) dialog.findViewById(R.id.cpTitle);
        cpTitle.setTypeface(bold);

        final EditText edOldPassword = (EditText) dialog.findViewById(R.id.edOldPassword);
        edOldPassword.setTypeface(bold);
        final EditText edNewPassword = (EditText) dialog.findViewById(R.id.edNewPassword);
        edNewPassword.setTypeface(bold);
        final EditText edNewConfirmPassword = (EditText) dialog.findViewById(R.id.edNewConfirmPassword);
        edNewConfirmPassword.setTypeface(bold);
        Button cancel = (Button) dialog.findViewById(R.id.btnCancel);
        cancel.setTypeface(bold);
        Button btnChange = (Button) dialog.findViewById(R.id.btnChange);
        btnChange.setTypeface(bold);
        cancel.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                dialog.dismiss();
            }
        });

        btnChange.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                String oldpass = edOldPassword.getText().toString();
                if (oldpass == null || oldpass.isEmpty()) {
                    edOldPassword.setError(getString(R.string.error_password));
                    edOldPassword.requestFocus();
                    return;
                }

                if (!oldpass.matches("((?=.*\\d)(?=.*[A-Z]).{6,})")) {
                    edOldPassword.setError(getString(R.string.password_hint));
                    edOldPassword.requestFocus();
                    return;
                }

                String newpass = edNewPassword.getText().toString();
                if (newpass == null || newpass.isEmpty()) {
                    edNewPassword.setError(getString(R.string.error_password));
                    edNewPassword.requestFocus();
                    return;
                }

                if (!newpass.matches("((?=.*\\d)(?=.*[A-Z]).{6,})")) {
                    edNewPassword.setError(getString(R.string.password_hint));
                    edNewPassword.requestFocus();
                    return;
                }

                String newcpass = edNewConfirmPassword.getText().toString();
                if (newcpass == null || newcpass.isEmpty()) {
                    edNewConfirmPassword.setError(getString(R.string.error_password));
                    edNewConfirmPassword.requestFocus();
                    return;
                }

                if (!newcpass.matches("((?=.*\\d)(?=.*[A-Z]).{6,})")) {
                    edNewConfirmPassword.setError(getString(R.string.password_hint));
                    edNewConfirmPassword.requestFocus();
                    return;
                }

                if (!oldpass.equalsIgnoreCase(Utilz.getUserDetails(MainActivity.this).get(Constants.MM_password))) {
                    edOldPassword.setError(getString(R.string.error_old_password));
                    edOldPassword.requestFocus();
                    return;
                }

                if (oldpass.equalsIgnoreCase(newpass)) {
                    edNewPassword.setError(getString(R.string.error_old_new_password));
                    edNewPassword.requestFocus();
                    return;
                }

                if (!newcpass.equalsIgnoreCase(newpass)) {
                    edNewConfirmPassword.setError(getString(R.string.error_new_confirm_password));
                    edNewConfirmPassword.requestFocus();
                    return;
                }
                dialog.dismiss();
                if (Utilz.isNetworkAvailable(MainActivity.this)) {
                    Utilz.hideSoftKeyboard(MainActivity.this);
                    changePasswordRequest(oldpass, newcpass);
                } else {
                    Utilz.showMessage2(MainActivity.this, getString(R.string.network_error));
                }

            }
        });
        dialog.show();

        WindowManager.LayoutParams lp = new WindowManager.LayoutParams();
        lp.copyFrom(dialog.getWindow().getAttributes());
        lp.width = WindowManager.LayoutParams.MATCH_PARENT;
        lp.height = WindowManager.LayoutParams.MATCH_PARENT;
        dialog.getWindow().setAttributes(lp);
        dialog.getWindow().setGravity(Gravity.CENTER);
    }

    Dialog dialog;

    private void showAddViaEmailDialog() {


        dialog = new Dialog(MainActivity.this);
//        insertOtpDialog.requestWindowFeature(Window.FEATURE_NO_TITLE);
        dialog.setContentView(R.layout.add_by_email_layout);
        dialog.getWindow().setBackgroundDrawable(new ColorDrawable(android.graphics.Color.TRANSPARENT));

        TextView tvEmailTitle = (TextView) dialog.findViewById(R.id.tvEmailTitle);
        tvEmailTitle.setTypeface(bold);

        final EditText edEmail = (EditText) dialog.findViewById(R.id.edEmail);
        edEmail.setTypeface(bold);
        Button btnCancel = (Button) dialog.findViewById(R.id.btnCancel);
        btnCancel.setTypeface(bold);
        Button btnChange = (Button) dialog.findViewById(R.id.btnAdd);
        btnChange.setTypeface(bold);

        btnCancel.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                dialog.dismiss();
            }
        });

        btnChange.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                String oldemail = edEmail.getText().toString();
                if (oldemail == null || oldemail.isEmpty()) {
                    edEmail.setError(getString(R.string.enter_email));
                    edEmail.requestFocus();
                    return;
                }

                if (!(Utilz.isValidEmail(oldemail))) {
                    edEmail.setError(getString(R.string.enter_email_valid));
                    edEmail.requestFocus();
                    return;
                }

                if ((Utilz.getUserDetails(MainActivity.this).get(Constants.MM_email).equalsIgnoreCase(oldemail))) {
                    edEmail.setError(getString(R.string.enter_email_friends));
                    edEmail.requestFocus();
                    return;
                }

                if (Utilz.isNetworkAvailable(MainActivity.this)) {
                    Utilz.hideSoftKeyboard(MainActivity.this);
                    addFriendViaEmail(oldemail);
                    edEmail.setText("");
                } else {
                    Utilz.showMessage2(MainActivity.this, getString(R.string.network_error));
                }
            }
        });
        dialog.show();
        dialog.setCancelable(true);
        WindowManager.LayoutParams lp = new WindowManager.LayoutParams();
        lp.copyFrom(dialog.getWindow().getAttributes());
        lp.width = WindowManager.LayoutParams.MATCH_PARENT;
        lp.height = WindowManager.LayoutParams.MATCH_PARENT;
        dialog.getWindow().setAttributes(lp);
        dialog.getWindow().setGravity(Gravity.CENTER);
    }


    private void addFriendViaEmail(String email) {
        Utilz.showProgressDialog(MainActivity.this);
        AsyncHttpClient client = new AsyncHttpClient();
        JSONObject objMain = new JSONObject();
        try {
            objMain.put(Constants.MM_user_id, Utilz.getUserDetails(MainActivity.this).get(Constants.MM_user_id));
            objMain.put(Constants.MM_email, email);

            StringEntity entity = new StringEntity(objMain.toString());
            client.post(MainActivity.this, Constants.Base_url + Constants.API_add_friends_manually, entity, "application/json", new AddFriendResponseHandler());

        } catch (Exception e) {
            e.printStackTrace();
            Utilz.closeProgressDialog();
        }
    }


    private class AddFriendResponseHandler extends AsyncHttpResponseHandler {

        @Override
        public void onSuccess(int statusCode, Header[] headers, byte[] responseBody) {
            Utilz.closeProgressDialog();
            JSONObject response = null;
            try {
                response = new JSONObject(new String(responseBody));
                Log.i("response.) ", response.toString());
                if (response.has(Constants.MM_STATUS_CODE)) {

                    if (response.getString(Constants.MM_STATUS_CODE).equalsIgnoreCase(Constants.MM_STATUS_CODE_SUCCESS)) {

                        AlertDialog.Builder builder1 = new AlertDialog.Builder(MainActivity.this);
                        builder1.setMessage(getString(R.string.add_by_email_done));
                        builder1.setMessage(getString(R.string.add_by_email_done));
                        builder1.setCancelable(true);

                        builder1.setPositiveButton(
                                android.R.string.ok,
                                new DialogInterface.OnClickListener() {
                                    public void onClick(DialogInterface dialogB, int id) {
                                        dialogB.dismiss();
                                        dialog.dismiss();

                                    }
                                });

                        AlertDialog alert11 = builder1.create();
                        alert11.show();

                    } else if (response.getString(Constants.MM_STATUS_CODE).equalsIgnoreCase(Constants.MM_STATUS_CODE_300)) {
                        Utilz.showMessage2(MainActivity.this, getString(R.string.add_by_email_friend_not_using_app));
                    } else if (response.getString(Constants.MM_STATUS_CODE).equalsIgnoreCase(Constants.MM_STATUS_CODE_301)) {
                        Utilz.showMessage2(MainActivity.this, response.getString(Constants.MM_msg));
                    } else {
                        Utilz.showMessage2(MainActivity.this, getString(R.string.server_error));
                    }
                } else {
                    Utilz.showMessage2(MainActivity.this, getString(R.string.server_error));
                }

            } catch (Exception ex) {
                ex.printStackTrace();
            }
        }

        @Override
        public void onFailure(int statusCode, Header[] headers, byte[] responseBody, Throwable error) {
            Utilz.closeProgressDialog();
            Toast.makeText(MainActivity.this, getString(R.string.server_error), Toast.LENGTH_SHORT).show();
        }
    }

    private void makeUserScheduleHideOROnline(String is_show_hide, String is_online_offline, boolean isOnline) {
//        if (isOnline)
//            pbOnline.setVisibility(View.VISIBLE);
//        else
//            pbHide.setVisibility(View.VISIBLE);

        AsyncHttpClient client = new AsyncHttpClient();
        JSONObject objMain = new JSONObject();
        try {
            objMain.put(Constants.MM_user_id, Utilz.getUserDetails(MainActivity.this).get(Constants.MM_user_id));
            objMain.put(Constants.MM_is_show_hide, is_show_hide);
            objMain.put(Constants.MM_is_online_offline, is_online_offline);

            StringEntity entity = new StringEntity(objMain.toString());
            client.post(MainActivity.this, Constants.Base_url + Constants.API_show_hide_my_break_schedule, entity, "application/json", new SwichChangeResponseHandler());

        } catch (Exception e) {
            e.printStackTrace();
            Utilz.closeProgressDialog();
        }
    }


    private class SwichChangeResponseHandler extends AsyncHttpResponseHandler {

        @Override
        public void onSuccess(int statusCode, Header[] headers, byte[] responseBody) {
            Utilz.closeProgressDialog();
            JSONObject response = null;
            try {
                response = new JSONObject(new String(responseBody));
                Log.i("response.) ", response.toString());
                if (response.has(Constants.MM_STATUS_CODE) && response.getString(Constants.MM_STATUS_CODE).equalsIgnoreCase(Constants.MM_STATUS_CODE_SUCCESS)) {

                } else {
                    Utilz.showMessage2(MainActivity.this, response.getString(Constants.MM_msg));
                    previousStateHide = !previousStateHide;
                    previousStateOnline = !previousStateOnline;
                    sb_hide_break_schedule.setChecked(previousStateHide);
                    sb_set_as_on_break.setChecked(previousStateOnline);
                }
            } catch (Exception ex) {
                ex.printStackTrace();
            }
//            pbOnline.setVisibility(View.INVISIBLE);
//            pbHide.setVisibility(View.INVISIBLE);
            sb_hide_break_schedule.setOnCheckedChangeListener(checkedChangeListener);
            sb_set_as_on_break.setOnCheckedChangeListener(checkedChangeListener);

            sb_hide_break_schedule.setEnabled(true);
            sb_set_as_on_break.setEnabled(true);

        }

        @Override
        public void onFailure(int statusCode, Header[] headers, byte[] responseBody, Throwable error) {
            Utilz.closeProgressDialog();
            Toast.makeText(MainActivity.this, getString(R.string.server_error), Toast.LENGTH_SHORT).show();
            sb_hide_break_schedule.setOnCheckedChangeListener(checkedChangeListener);
            sb_set_as_on_break.setOnCheckedChangeListener(checkedChangeListener);

            sb_hide_break_schedule.setEnabled(true);
            sb_set_as_on_break.setEnabled(true);
        }
    }


}
