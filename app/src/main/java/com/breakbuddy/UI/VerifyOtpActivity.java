package com.breakbuddy.UI;

import android.app.Dialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.os.CountDownTimer;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import com.breakbuddy.R;
import com.loopj.android.http.AsyncHttpClient;
import com.loopj.android.http.AsyncHttpResponseHandler;

import org.json.JSONObject;

import java.util.concurrent.TimeUnit;

import Utils.Constants;
import Utils.Utilz;
import cz.msebera.android.httpclient.Header;
import cz.msebera.android.httpclient.entity.StringEntity;

public class VerifyOtpActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_verify_otp);
        insertOtpPopUp();
    }

    @Override
    public void onBackPressed() {
        Utilz.setUserDetails(VerifyOtpActivity.this, null);
        Toast.makeText(getApplicationContext(), getString(R.string.invalid_otp3), Toast.LENGTH_SHORT).show();
        super.onBackPressed();
    }

    TextView tvSendOtp;
    Dialog insertOtpDialog;
    Button btnOTP;
    int resendOtpCount = 0;

    public void insertOtpPopUp() {
        insertOtpDialog = new Dialog(VerifyOtpActivity.this);
        insertOtpDialog.setContentView(R.layout.insert_otp_layout);
        insertOtpDialog.setCancelable(false);
        insertOtpDialog.getWindow().setBackgroundDrawable(new ColorDrawable(android.graphics.Color.TRANSPARENT));

        WindowManager.LayoutParams lp = new WindowManager.LayoutParams();
        Window window = insertOtpDialog.getWindow();
        lp.copyFrom(window.getAttributes());
        lp.width = WindowManager.LayoutParams.MATCH_PARENT;
        lp.height = WindowManager.LayoutParams.WRAP_CONTENT;
        window.setAttributes(lp);

        TextView tvTitle = (TextView) insertOtpDialog.findViewById(R.id.tvTitle);
        tvTitle.setTypeface(Utilz.getTypefaceBold(VerifyOtpActivity.this));

        final EditText edOtp = (EditText) insertOtpDialog.findViewById(R.id.edOtp);
        edOtp.setTypeface(Utilz.getTypefaceBold(VerifyOtpActivity.this));
        Button cancel = (Button) insertOtpDialog.findViewById(R.id.btnCancel);
        cancel.setTypeface(Utilz.getTypefaceBold(VerifyOtpActivity.this));
        Button continuebtn = (Button) insertOtpDialog.findViewById(R.id.btnContinue);
        continuebtn.setTypeface(Utilz.getTypefaceBold(VerifyOtpActivity.this));

        btnOTP = (Button) insertOtpDialog.findViewById(R.id.btnOTP);
        btnOTP.setTypeface(Utilz.getTypefaceBold(VerifyOtpActivity.this));

        tvSendOtp = (TextView) insertOtpDialog.findViewById(R.id.tvSendOtp);
        tvSendOtp.setTypeface(Utilz.getTypefaceBold(VerifyOtpActivity.this));

        btnOTP.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                resendOtpCount++;
                if (resendOtpCount >= 5)
                    Toast.makeText(getApplicationContext(), getString(R.string.otp_limit_reach), Toast.LENGTH_LONG).show();
                else
                    resendOTP();
            }
        });

        cancel.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Utilz.setUserDetails(VerifyOtpActivity.this, null);
                insertOtpDialog.dismiss();
                Toast.makeText(getApplicationContext(), getString(R.string.invalid_otp3), Toast.LENGTH_LONG).show();
                finish();
            }
        });

        continuebtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                String otp = edOtp.getText().toString();
                if (otp == null || otp.isEmpty()) {
                    edOtp.setError(getString(R.string.invalid_otp));
                    edOtp.requestFocus();
                    return;
                }

                if (otp.length() != 6) {
                    edOtp.setError(getString(R.string.invalid_otp2));
                    edOtp.requestFocus();
                    return;
                }

                if (Utilz.isNetworkAvailable(VerifyOtpActivity.this)) {
                    verifyPhone(edOtp.getText().toString());
                } else {
                    Utilz.showMessage2(VerifyOtpActivity.this, getString(R.string.network_error));
                }
            }
        });

        insertOtpDialog.show();
        startTimer();
    }

    public void startTimer() {
        tvSendOtp.setVisibility(View.VISIBLE);
        btnOTP.setClickable(false);
        btnOTP.setTextColor(Color.GRAY);
        new CountDownTimer(60000, 1000) {

            public void onTick(long millisUntilFinished) {
                long secs = TimeUnit.MILLISECONDS.toSeconds(millisUntilFinished) - TimeUnit.MINUTES.toSeconds(TimeUnit.MILLISECONDS.toMinutes(millisUntilFinished));
                tvSendOtp.setText(String.format("%s%s", "00:", secs < 10 ? "0" + secs : secs));
            }

            public void onFinish() {
                tvSendOtp.setVisibility(View.GONE);
                btnOTP.setClickable(true);
                btnOTP.setTextColor(Color.WHITE);
            }
        }.start();
    }


    private void resendOTP() {
        Utilz.showProgressDialog(VerifyOtpActivity.this);

        AsyncHttpClient client = new AsyncHttpClient();
        JSONObject objMain = new JSONObject();
        try {
            objMain.put(Constants.MM_user_id, Integer.parseInt(Utilz.getUserDetails(VerifyOtpActivity.this).get(Constants.MM_user_id)));

            StringEntity entity = new StringEntity(objMain.toString());
            client.post(VerifyOtpActivity.this, Constants.Base_url + Constants.API_resend_otp, entity, "application/json", new VerifyOtpActivity.ResendOtpHandler());

        } catch (Exception e) {
            e.printStackTrace();
            Utilz.closeProgressDialog();
        }
    }

    private class ResendOtpHandler extends AsyncHttpResponseHandler {

        @Override
        public void onSuccess(int statusCode, Header[] headers, byte[] responseBody) {
            Utilz.closeProgressDialog();
            JSONObject response = null;
            try {
                response = new JSONObject(new String(responseBody));
//                makeDDbEntryForSubscription(response, mainPurchase);
                Log.i("response.) ", response.toString());
                if (response.has(Constants.MM_STATUS_CODE) && response.getString(Constants.MM_STATUS_CODE).equalsIgnoreCase(Constants.MM_STATUS_CODE_SUCCESS)) {
                    Toast.makeText(VerifyOtpActivity.this, response.getString(Constants.MM_msg), Toast.LENGTH_SHORT).show();
                    startTimer();
                } else {
                    AlertDialog.Builder builder1 = new AlertDialog.Builder(VerifyOtpActivity.this);
                    builder1.setMessage(response.getString(Constants.MM_msg));
                    builder1.setCancelable(false);

                    builder1.setPositiveButton(
                            android.R.string.ok,
                            new DialogInterface.OnClickListener() {
                                public void onClick(DialogInterface dialog, int id) {
                                    Utilz.setUserDetails(VerifyOtpActivity.this, null);
                                    dialog.cancel();
                                    insertOtpDialog.dismiss();
                                    finish();
                                }
                            });

                    AlertDialog alert11 = builder1.create();
                    alert11.show();
                }
            } catch (Exception ex) {
                ex.printStackTrace();
            }
        }

        @Override
        public void onFailure(int statusCode, Header[] headers, byte[] responseBody, Throwable error) {
            Utilz.closeProgressDialog();
            Toast.makeText(VerifyOtpActivity.this, getString(R.string.server_error), Toast.LENGTH_SHORT).show();
        }
    }


    private void verifyPhone(String otp) {
        Utilz.showProgressDialog(VerifyOtpActivity.this);

        AsyncHttpClient client = new AsyncHttpClient();
        JSONObject objMain = new JSONObject();
        try {
            objMain.put(Constants.MM_user_id, Integer.parseInt(Utilz.getUserDetails(VerifyOtpActivity.this).get(Constants.MM_user_id)));
            objMain.put(Constants.MM_otp, otp);

            StringEntity entity = new StringEntity(objMain.toString());
            client.post(VerifyOtpActivity.this, Constants.Base_url + Constants.API_verify_phone_no, entity, "application/json", new VerifyOtpActivity.VerifyPhoneNumberHandler());

        } catch (Exception e) {
            e.printStackTrace();
            Utilz.closeProgressDialog();
        }
    }

    private class VerifyPhoneNumberHandler extends AsyncHttpResponseHandler {

        @Override
        public void onSuccess(int statusCode, Header[] headers, byte[] responseBody) {
            Utilz.closeProgressDialog();
            JSONObject response = null;
            try {
                response = new JSONObject(new String(responseBody));
//                makeDDbEntryForSubscription(response, mainPurchase);
                Log.i("response.) ", response.toString());
                if (response.has(Constants.MM_STATUS_CODE) && response.getString(Constants.MM_STATUS_CODE).equalsIgnoreCase(Constants.MM_STATUS_CODE_SUCCESS)) {
                    Toast.makeText(VerifyOtpActivity.this, response.getString(Constants.MM_msg), Toast.LENGTH_SHORT).show();
                    insertOtpDialog.dismiss();
                    gotoScheduleActivity();
                } else {
                    Toast.makeText(VerifyOtpActivity.this, response.getString(Constants.MM_msg), Toast.LENGTH_SHORT).show();
                }
            } catch (Exception ex) {
                ex.printStackTrace();
            }
        }

        @Override
        public void onFailure(int statusCode, Header[] headers, byte[] responseBody, Throwable error) {
            Utilz.closeProgressDialog();
            Toast.makeText(VerifyOtpActivity.this, getString(R.string.server_error), Toast.LENGTH_SHORT).show();
        }
    }


    private void gotoScheduleActivity() {
        Intent i = new Intent(VerifyOtpActivity.this, ScheduleActivity.class);
        Utilz.saveBooleanToUserDefaults(VerifyOtpActivity.this, Constants.MM_is_new_register, true);
        i.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
        startActivity(i);
        overridePendingTransition(R.anim.anim_slide_in_left,
                R.anim.anim_slide_out_left);
        finish();
    }


}
