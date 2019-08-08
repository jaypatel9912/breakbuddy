package com.breakbuddy.UI;

import android.app.Dialog;
import android.os.CountDownTimer;
import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Color;
import android.graphics.Typeface;
import android.graphics.drawable.ColorDrawable;
import android.os.Bundle;
import android.support.v7.app.AlertDialog;
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

import java.util.HashMap;
import java.util.concurrent.TimeUnit;
import java.util.regex.PatternSyntaxException;

import API.LoginApi;
import Utils.Constants;
import Utils.FontManager;
import Utils.Interfaces;
import Utils.Utilz;
import cz.msebera.android.httpclient.Header;
import cz.msebera.android.httpclient.entity.StringEntity;

public class LoginActivity extends BaseActivity implements Interfaces.LoginListener {


    Typeface iconFont;
    TextView tvUserName, tvDesc, tvPassweor;
    Button bForgotPassword, btnSignUp, btnLogin;
    EditText edEmail, edPassword;
    Interfaces.LoginListener loginListener;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);

        loginListener = this;

        initViews();
    }

    public void initViews() {
        iconFont = FontManager.getTypeface(getApplicationContext(), FontManager.FONTAWESOME);
        Typeface ttf = Utilz.getTypeface(LoginActivity.this);

        tvDesc = (TextView) findViewById(R.id.tvDesc);
        tvDesc.setTypeface(Utilz.getTypefaceBold(LoginActivity.this));

        tvUserName = (TextView) findViewById(R.id.icUsername);
        tvUserName.setTypeface(iconFont);

        tvPassweor = (TextView) findViewById(R.id.icPassword);
        tvPassweor.setTypeface(iconFont);

        edEmail = (EditText) findViewById(R.id.edEmail);
        edEmail.setTypeface(Utilz.getTypefaceBold(LoginActivity.this));
        edPassword = (EditText) findViewById(R.id.edPassword);
        edPassword.setTypeface(Utilz.getTypefaceBold(LoginActivity.this));

        btnSignUp = (Button) findViewById(R.id.btnSignUp);
        btnSignUp.setTypeface(Utilz.getTypefaceMedium(LoginActivity.this));
        btnSignUp.setOnClickListener(signupClickListener);

        bForgotPassword = (Button) findViewById(R.id.btnForgotPassword);
        bForgotPassword.setTypeface(Utilz.getTypefaceMedium(LoginActivity.this));
        bForgotPassword.setOnClickListener(forgotPasswordClickListener);

        btnLogin = (Button) findViewById(R.id.btnLogin);
        btnLogin.setTypeface(Utilz.getTypefaceMedium(LoginActivity.this));
        btnLogin.setOnClickListener(loginClickListener);

        if (Utilz.getBooleanFromUserDefaults(LoginActivity.this, Constants.MM_showActivationDialog)) {
            Utilz.saveBooleanToUserDefaults(LoginActivity.this, Constants.MM_showActivationDialog, false);
            checkforActivation();
        }
    }

    View.OnClickListener signupClickListener = new View.OnClickListener() {
        @Override
        public void onClick(View v) {
            startActivity(new Intent(LoginActivity.this, RegistrationActivity.class));
        }
    };

    View.OnClickListener loginClickListener = new View.OnClickListener() {
        @Override
        public void onClick(View v) {

            Utilz.hideSoftKeyboard(LoginActivity.this);

            String email = edEmail.getText().toString();
            if (email == null || email.isEmpty() || !(Utilz.isValidEmail(email))) {
                edEmail.setError(getString(R.string.error_email));
                edEmail.requestFocus();
                return;
            }

            String password = edPassword.getText().toString();
            if (password == null || password.isEmpty()) {
                edPassword.setError(getString(R.string.error_password));
                edPassword.requestFocus();
                return;
            }

            try {
                if (!password.matches("((?=.*\\d)(?=.*[A-Z]).{6,})")) {
                    edPassword.setError(getString(R.string.password_hint));
                    edPassword.requestFocus();
                    return;
                }
            } catch (PatternSyntaxException ex) {
                Toast.makeText(getApplicationContext(), ex.getMessage(), Toast.LENGTH_SHORT).show();
            }

            if (Utilz.isNetworkAvailable(LoginActivity.this)) {
                Utilz.showProgressDialog(LoginActivity.this);
                new LoginApi(LoginActivity.this, email, password, loginListener);
            } else {
                Utilz.showMessage2(LoginActivity.this, getString(R.string.network_error));
            }

        }
    };

    View.OnClickListener forgotPasswordClickListener = new View.OnClickListener() {
        @Override
        public void onClick(View v) {
            showForgotPasswordPopup();
        }
    };


    @Override
    public void onSuccessToLogin(String res) {
        JSONObject response;
        Utilz.closeProgressDialog();
        try {
            response = new JSONObject(res);
//                makeDDbEntryForSubscription(response, mainPurchase);

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

                    user.put(Constants.MM_password, edPassword.getText().toString().trim());

                    if (resultObj.has(Constants.MM_is_show_hide))
                        user.put(Constants.MM_is_show_hide, resultObj.getString(Constants.MM_is_show_hide));

                    if (resultObj.has(Constants.MM_is_online_offline))
                        user.put(Constants.MM_is_online_offline, resultObj.getString(Constants.MM_is_online_offline));

                    if (resultObj.has(Constants.MM_days))
                        user.put(Constants.MM_days, resultObj.getString(Constants.MM_days));
                    if (resultObj.has(Constants.MM_added_me_count))
                        user.put(Constants.MM_added_me_count, resultObj.getString(Constants.MM_added_me_count));

                    edEmail.setText("");
                    edPassword.setText("");
                    Utilz.setUserDetails(LoginActivity.this, user);
                    checkforActivation();
                }
            } else if (response.has(Constants.MM_STATUS_CODE) && response.getString(Constants.MM_STATUS_CODE).equalsIgnoreCase(Constants.MM_STATUS_CODE_PHONE_NOT_VERIFIED)) {

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

                    user.put(Constants.MM_password, edPassword.getText().toString().trim());

                    if (resultObj.has(Constants.MM_is_show_hide))
                        user.put(Constants.MM_is_show_hide, resultObj.getString(Constants.MM_is_show_hide));

                    if (resultObj.has(Constants.MM_is_online_offline))
                        user.put(Constants.MM_is_online_offline, resultObj.getString(Constants.MM_is_online_offline));

                    if (resultObj.has(Constants.MM_days))
                        user.put(Constants.MM_days, resultObj.getString(Constants.MM_days));
                    if (resultObj.has(Constants.MM_added_me_count))
                        user.put(Constants.MM_added_me_count, resultObj.getString(Constants.MM_added_me_count));

                    edEmail.setText("");
                    edPassword.setText("");
                    Utilz.setUserDetails(LoginActivity.this, user);
                }

                AlertDialog.Builder builder1 = new AlertDialog.Builder(LoginActivity.this);
                builder1.setMessage(response.getString(Constants.MM_msg));
                builder1.setCancelable(false);
                builder1.setPositiveButton(
                        R.string.verify_now,
                        new DialogInterface.OnClickListener() {
                            public void onClick(DialogInterface dialog, int id) {
                                dialog.dismiss();
                                startActivity(new Intent(LoginActivity.this, VerifyOtpActivity.class));
                            }
                        })
                        .setNegativeButton(android.R.string.cancel, new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialog, int which) {
                                Utilz.setUserDetails(LoginActivity.this, null);
                                Toast.makeText(getApplicationContext(), getString(R.string.invalid_otp3), Toast.LENGTH_SHORT).show();
                                dialog.dismiss();
                            }
                        });

                AlertDialog alert11 = builder1.create();
                alert11.show();


            } else {
                Utilz.showMessage1(LoginActivity.this, getString(R.string.fail_login), getString(R.string.fail_login_msg));
            }
        } catch (Exception ex) {
            ex.printStackTrace();
        }
    }

    @Override
    public void onFailedToLogin() {
        Utilz.closeProgressDialog();
        Toast.makeText(LoginActivity.this, getString(R.string.server_error), Toast.LENGTH_SHORT).show();
    }

    private void checkforActivation() {
        if (Utilz.getUserDetails(LoginActivity.this).get(Constants.MM_is_active).equalsIgnoreCase("0")) {

            int days = Integer.parseInt(Utilz.getUserDetails(LoginActivity.this).get(Constants.MM_days));
            days = 7 - days;
//            String msg = days <= 0 ? getString(R.string.bb_account_deactivated) : String.format(getString(R.string.activation_msg), String.valueOf(days));
            String msg = getString(R.string.acc_deactive_new_msg);
            AlertDialog.Builder builder1 = new AlertDialog.Builder(LoginActivity.this);
            builder1.setMessage(msg);
            builder1.setCancelable(false);
            final int finalDays = days;
            builder1.setPositiveButton(
                    R.string.resend_main,
                    new DialogInterface.OnClickListener() {
                        public void onClick(DialogInterface dialog, int id) {
                            dialog.dismiss();
                            resendVerificationLink();
                            Utilz.setUserDetails(LoginActivity.this, null);
                        }
                    })
                    .setNegativeButton(android.R.string.ok, new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialog, int which) {
                            dialog.dismiss();
                            if (finalDays < 0) {
                                Utilz.setUserDetails(LoginActivity.this, null);
                            } else if (finalDays <= 7)
                                gotoMainActivity();
                        }
                    });

            AlertDialog alert11 = builder1.create();
            alert11.show();
        } else {
//            Toast.makeText(LoginActivity.this, msg, Toast.LENGTH_SHORT).show();
            gotoMainActivity();
        }
    }

    private void gotoMainActivity() {
        Intent i = new Intent(LoginActivity.this, MainActivity.class);
        i.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
        startActivity(i);
        finish();
    }

    private void resendVerificationLink() {
        Utilz.showProgressDialog(LoginActivity.this);
        AsyncHttpClient client = new AsyncHttpClient();
        JSONObject objMain = new JSONObject();
        try {
            objMain.put(Constants.MM_user_id, Utilz.getUserDetails(LoginActivity.this).get(Constants.MM_user_id));
            StringEntity entity = new StringEntity(objMain.toString());
            client.post(LoginActivity.this, Constants.Base_url + Constants.API_RESEND_LINK, entity, "application/json", new ResendLinkHandler());

        } catch (Exception e) {
            e.printStackTrace();
            Utilz.closeProgressDialog();
        }
    }

    private class ResendLinkHandler extends AsyncHttpResponseHandler {

        @Override
        public void onSuccess(int statusCode, Header[] headers, byte[] responseBody) {
            Utilz.closeProgressDialog();
            JSONObject response = null;
            try {
                response = new JSONObject(new String(responseBody));
                Log.i("response.) ", response.toString());
                if (response.has(Constants.MM_STATUS_CODE) && response.getString(Constants.MM_STATUS_CODE).equalsIgnoreCase(Constants.MM_STATUS_CODE_SUCCESS)) {
                    Utilz.showMessage2(LoginActivity.this, getString(R.string.veri_email_send));
                } else {
                    Utilz.showMessage2(LoginActivity.this, response.get(Constants.MM_msg).toString());
                }
            } catch (Exception ex) {
                ex.printStackTrace();
            }
        }

        @Override
        public void onFailure(int statusCode, Header[] headers, byte[] responseBody, Throwable error) {
            Utilz.closeProgressDialog();
            Toast.makeText(LoginActivity.this, getString(R.string.server_error), Toast.LENGTH_SHORT).show();
        }
    }


    public void showForgotPasswordPopup() {
        final Dialog dialog = new Dialog(LoginActivity.this);
//        insertOtpDialog.requestWindowFeature(Window.FEATURE_NO_TITLE);
        dialog.setContentView(R.layout.forgot_password_lyout);
        dialog.getWindow().setBackgroundDrawable(new ColorDrawable(android.graphics.Color.TRANSPARENT));

        WindowManager.LayoutParams lp = new WindowManager.LayoutParams();
        Window window = dialog.getWindow();
        lp.copyFrom(window.getAttributes());
        lp.width = WindowManager.LayoutParams.MATCH_PARENT;
        lp.height = WindowManager.LayoutParams.WRAP_CONTENT;
        window.setAttributes(lp);

        TextView tvTitle = (TextView) dialog.findViewById(R.id.tvTitle);
        tvTitle.setTypeface(Utilz.getTypefaceBold(LoginActivity.this));

        final EditText edEmail = (EditText) dialog.findViewById(R.id.edEmail);
        edEmail.setTypeface(Utilz.getTypefaceBold(LoginActivity.this));
        Button cancel = (Button) dialog.findViewById(R.id.btnCancel);
        cancel.setTypeface(Utilz.getTypefaceBold(LoginActivity.this));
        Button continuebtn = (Button) dialog.findViewById(R.id.btnContinue);
        continuebtn.setTypeface(Utilz.getTypefaceBold(LoginActivity.this));

        cancel.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                dialog.dismiss();
            }
        });

        continuebtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                String email = edEmail.getText().toString();
                if (email == null || email.isEmpty() || !(Utilz.isValidEmail(email))) {
                    edEmail.setError(getString(R.string.error_email));
                    edEmail.requestFocus();
                    return;
                }
                dialog.dismiss();
                if (Utilz.isNetworkAvailable(LoginActivity.this)) {
                    forgotPasswordRequest(edEmail.getText().toString().trim());
                    Utilz.hideSoftKeyboard(LoginActivity.this);
                } else {
                    Utilz.showMessage2(LoginActivity.this, getString(R.string.network_error));
                }


            }
        });

        dialog.show();
    }

    private void forgotPasswordRequest(String email) {
        Utilz.showProgressDialog(LoginActivity.this);
        AsyncHttpClient client = new AsyncHttpClient();
        JSONObject objMain = new JSONObject();
        try {
            objMain.put(Constants.MM_email, email);

            StringEntity entity = new StringEntity(objMain.toString());
            client.post(LoginActivity.this, Constants.Base_url + Constants.API_FORGOT_PASSWORD, entity, "application/json", new ForgotPasswordHandler());

        } catch (Exception e) {
            e.printStackTrace();
            Utilz.closeProgressDialog();
        }
    }

    private class ForgotPasswordHandler extends AsyncHttpResponseHandler {

        @Override
        public void onSuccess(int statusCode, Header[] headers, byte[] responseBody) {
            Utilz.closeProgressDialog();
            JSONObject response = null;
            try {
                response = new JSONObject(new String(responseBody));
                Log.i("response.) ", response.toString());
                if (response.has(Constants.MM_STATUS_CODE) && response.getString(Constants.MM_STATUS_CODE).equalsIgnoreCase(Constants.MM_STATUS_CODE_SUCCESS)) {
                    Utilz.setPreference(LoginActivity.this, Constants.MM_reset_password_token, response.getString(Constants.MM_reset_password_token));
                    Utilz.showMessage1(LoginActivity.this, getString(R.string.passwor_reset_done), getString(R.string.chk_mail));
                } else {
                    Utilz.showMessage2(LoginActivity.this, response.get(Constants.MM_msg).toString());
                }
            } catch (Exception ex) {
                ex.printStackTrace();
            }
        }

        @Override
        public void onFailure(int statusCode, Header[] headers, byte[] responseBody, Throwable error) {
            Utilz.closeProgressDialog();
            Toast.makeText(LoginActivity.this, getString(R.string.server_error), Toast.LENGTH_SHORT).show();
        }
    }


//    TextView tvSendOtp;
//    Dialog insertOtpDialog;
//    Button btnOTP;
//    int resendOtpCount = 0;
//
//    public void insertOtpPopUp() {
//        insertOtpDialog = new Dialog(LoginActivity.this);
//        insertOtpDialog.setContentView(R.layout.insert_otp_layout);
//        insertOtpDialog.setCancelable(false);
//        insertOtpDialog.getWindow().setBackgroundDrawable(new ColorDrawable(android.graphics.Color.TRANSPARENT));
//
//        WindowManager.LayoutParams lp = new WindowManager.LayoutParams();
//        Window window = insertOtpDialog.getWindow();
//        lp.copyFrom(window.getAttributes());
//        lp.width = WindowManager.LayoutParams.MATCH_PARENT;
//        lp.height = WindowManager.LayoutParams.WRAP_CONTENT;
//        window.setAttributes(lp);
//
//        TextView tvTitle = (TextView) insertOtpDialog.findViewById(R.id.tvTitle);
//        tvTitle.setTypeface(Utilz.getTypefaceBold(LoginActivity.this));
//
//        final EditText edOtp = (EditText) insertOtpDialog.findViewById(R.id.edOtp);
//        edOtp.setTypeface(Utilz.getTypefaceBold(LoginActivity.this));
//        Button cancel = (Button) insertOtpDialog.findViewById(R.id.btnCancel);
//        cancel.setTypeface(Utilz.getTypefaceBold(LoginActivity.this));
//        Button continuebtn = (Button) insertOtpDialog.findViewById(R.id.btnContinue);
//        continuebtn.setTypeface(Utilz.getTypefaceBold(LoginActivity.this));
//
//        btnOTP = (Button) insertOtpDialog.findViewById(R.id.btnOTP);
//        btnOTP.setTypeface(Utilz.getTypefaceBold(LoginActivity.this));
//
//        tvSendOtp = (TextView) insertOtpDialog.findViewById(R.id.tvSendOtp);
//        tvSendOtp.setTypeface(Utilz.getTypefaceBold(LoginActivity.this));
//
//        btnOTP.setOnClickListener(new View.OnClickListener() {
//            @Override
//            public void onClick(View v) {
//                resendOtpCount++;
//                if (resendOtpCount >= 5)
//                    Toast.makeText(getApplicationContext(), "Maximum limit to send OTP reached", Toast.LENGTH_SHORT).show();
//                else
//                    resendOTP();
//            }
//        });
//
//        cancel.setOnClickListener(new View.OnClickListener() {
//            @Override
//            public void onClick(View v) {
//                Utilz.setUserDetails(LoginActivity.this, null);
//                insertOtpDialog.dismiss();
//                Toast.makeText(getApplicationContext(), getString(R.string.invalid_otp3), Toast.LENGTH_SHORT).show();
//            }
//        });
//
//        continuebtn.setOnClickListener(new View.OnClickListener() {
//            @Override
//            public void onClick(View v) {
//
//                String otp = edOtp.getText().toString();
//                if (otp == null || otp.isEmpty()) {
//                    edOtp.setError(getString(R.string.invalid_otp));
//                    edOtp.requestFocus();
//                    return;
//                }
//
//                if (otp.length() != 6) {
//                    edOtp.setError(getString(R.string.invalid_otp2));
//                    edOtp.requestFocus();
//                    return;
//                }
//
//                if (Utilz.isNetworkAvailable(LoginActivity.this)) {
//                    verifyPhone(edOtp.getText().toString());
//                    Utilz.hideSoftKeyboard(LoginActivity.this);
//                } else {
//                    Toast.makeText(getApplicationContext(), getString(R.string.network_error), Toast.LENGTH_SHORT).show();
//                }
//            }
//        });
//
//        insertOtpDialog.show();
//        resendOTP();
//    }
//
//    public void startTimer() {
//        tvSendOtp.setVisibility(View.VISIBLE);
//        btnOTP.setClickable(false);
//        btnOTP.setTextColor(Color.GRAY);
//        new CountDownTimer(60000, 1000) {
//
//            public void onTick(long millisUntilFinished) {
//                long secs = TimeUnit.MILLISECONDS.toSeconds(millisUntilFinished) - TimeUnit.MINUTES.toSeconds(TimeUnit.MILLISECONDS.toMinutes(millisUntilFinished));
//                tvSendOtp.setText(String.format("%s%s", "00:", secs < 10 ? "0" + secs : secs));
//            }
//
//            public void onFinish() {
//                tvSendOtp.setVisibility(View.GONE);
//                btnOTP.setClickable(true);
//                btnOTP.setTextColor(Color.WHITE);
//            }
//        }.start();
//    }
//
//
//    private void resendOTP() {
//        Utilz.showProgressDialog(LoginActivity.this);
//
//        AsyncHttpClient client = new AsyncHttpClient();
//        JSONObject objMain = new JSONObject();
//        try {
//            objMain.put(Constants.MM_user_id, Integer.parseInt(Utilz.getUserDetails(LoginActivity.this).get(Constants.MM_user_id)));
//
//            StringEntity entity = new StringEntity(objMain.toString());
//            client.post(LoginActivity.this, Constants.Base_url + Constants.API_resend_otp, entity, "application/json", new LoginActivity.ResendOtpHandler());
//
//        } catch (Exception e) {
//            e.printStackTrace();
//            Utilz.closeProgressDialog();
//        }
//    }
//
//    private class ResendOtpHandler extends AsyncHttpResponseHandler {
//
//        @Override
//        public void onSuccess(int statusCode, Header[] headers, byte[] responseBody) {
//            Utilz.closeProgressDialog();
//            JSONObject response = null;
//            try {
//                response = new JSONObject(new String(responseBody));
//                Log.i("response.) ", response.toString());
//                if (response.has(Constants.MM_STATUS_CODE) && response.getString(Constants.MM_STATUS_CODE).equalsIgnoreCase(Constants.MM_STATUS_CODE_SUCCESS)) {
//                    Toast.makeText(LoginActivity.this, response.getString(Constants.MM_msg), Toast.LENGTH_SHORT).show();
//                    startTimer();
//                } else {
//                    AlertDialog.Builder builder1 = new AlertDialog.Builder(LoginActivity.this);
//                    builder1.setMessage(response.getString(Constants.MM_msg));
//                    builder1.setCancelable(false);
//
//                    builder1.setPositiveButton(
//                            android.R.string.ok,
//                            new DialogInterface.OnClickListener() {
//                                public void onClick(DialogInterface dialog, int id) {
//                                    Utilz.setUserDetails(LoginActivity.this, null);
//                                    dialog.cancel();
//                                    insertOtpDialog.dismiss();
//                                }
//                            });
//
//                    AlertDialog alert11 = builder1.create();
//                    alert11.show();
//                }
//            } catch (Exception ex) {
//                ex.printStackTrace();
//            }
//        }
//
//        @Override
//        public void onFailure(int statusCode, Header[] headers, byte[] responseBody, Throwable error) {
//            Utilz.closeProgressDialog();
//            Toast.makeText(LoginActivity.this, getString(R.string.server_error), Toast.LENGTH_SHORT).show();
//        }
//    }
//
//
//    private void verifyPhone(String otp) {
//        Utilz.showProgressDialog(LoginActivity.this);
//
//        AsyncHttpClient client = new AsyncHttpClient();
//        JSONObject objMain = new JSONObject();
//        try {
//            objMain.put(Constants.MM_user_id, Integer.parseInt(Utilz.getUserDetails(LoginActivity.this).get(Constants.MM_user_id)));
//            objMain.put(Constants.MM_otp, otp);
//
//            StringEntity entity = new StringEntity(objMain.toString());
//            client.post(LoginActivity.this, Constants.Base_url + Constants.API_verify_phone_no, entity, "application/json", new LoginActivity.VerifyPhoneNumberHandler());
//
//        } catch (Exception e) {
//            e.printStackTrace();
//            Utilz.closeProgressDialog();
//        }
//    }
//
//    private class VerifyPhoneNumberHandler extends AsyncHttpResponseHandler {
//
//        @Override
//        public void onSuccess(int statusCode, Header[] headers, byte[] responseBody) {
//            Utilz.closeProgressDialog();
//            JSONObject response = null;
//            try {
//                response = new JSONObject(new String(responseBody));
////                makeDDbEntryForSubscription(response, mainPurchase);
//                Log.i("response.) ", response.toString());
//                if (response.has(Constants.MM_STATUS_CODE) && response.getString(Constants.MM_STATUS_CODE).equalsIgnoreCase(Constants.MM_STATUS_CODE_SUCCESS)) {
//                    insertOtpDialog.dismiss();
//                    gotoScheduleActivity();
//                } else {
//                    Toast.makeText(LoginActivity.this, response.getString(Constants.MM_msg), Toast.LENGTH_SHORT).show();
//                }
//            } catch (Exception ex) {
//                ex.printStackTrace();
//            }
//        }
//
//        @Override
//        public void onFailure(int statusCode, Header[] headers, byte[] responseBody, Throwable error) {
//            Utilz.closeProgressDialog();
//            Toast.makeText(LoginActivity.this, getString(R.string.server_error), Toast.LENGTH_SHORT).show();
//        }
//    }
//
//
//    private void gotoScheduleActivity() {
//        Intent i = new Intent(LoginActivity.this, ScheduleActivity.class);
//        Utilz.saveBooleanToUserDefaults(LoginActivity.this, Constants.MM_is_new_register, true);
//        i.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
//        startActivity(i);
//        overridePendingTransition(R.anim.anim_slide_in_left,
//                R.anim.anim_slide_out_left);
//        finish();
//    }

}
