package com.breakbuddy.UI;

import android.app.Dialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Color;
import android.graphics.Typeface;
import android.graphics.drawable.ColorDrawable;
import android.os.CountDownTimer;
import android.support.v7.app.AlertDialog;
import android.os.Bundle;
import android.text.Spannable;
import android.text.SpannableString;
import android.text.Spanned;
import android.text.TextPaint;
import android.text.method.LinkMovementMethod;
import android.text.style.ClickableSpan;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import com.breakbuddy.R;
import com.google.i18n.phonenumbers.PhoneNumberUtil;
import com.loopj.android.http.AsyncHttpClient;
import com.loopj.android.http.AsyncHttpResponseHandler;

import org.json.JSONObject;

import java.util.HashMap;
import java.util.Locale;
import java.util.concurrent.TimeUnit;
import java.util.regex.PatternSyntaxException;

import QuickScroll.Utils;
import Utils.FontManager;
import Utils.Constants;
import Utils.Utilz;
import cz.msebera.android.httpclient.Header;
import cz.msebera.android.httpclient.entity.StringEntity;

public class RegistrationActivity extends BaseActivity {

    TextView icinfo, icFname, icLname, icEmail, icPassword, icCPassword, icPhoneNo, tvTnc, tvCountryCode;
    EditText edFname, edLname, edEmail, edPassword, edCPassword, edPhoneNo;
    Button btnCotinue;
    Typeface iconFont;
    String countryCode = "";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_registration);

        android.support.v7.app.ActionBar mActionBar = getSupportActionBar();
        mActionBar.setDisplayShowHomeEnabled(false);
        mActionBar.setDisplayShowTitleEnabled(false);
        LayoutInflater mInflater = LayoutInflater.from(this);

        View mCustomView = mInflater.inflate(R.layout.toolbar, null);
        TextView mTitleTextView = (TextView) mCustomView.findViewById(R.id.title_text);
        mTitleTextView.setText(getString(R.string.register_title));
        mTitleTextView.setTypeface(Utilz.getTypefaceBold(RegistrationActivity.this));

        iconFont = FontManager.getTypeface(getApplicationContext(), FontManager.FONTAWESOME);

        TextView tvBack = (TextView) mCustomView.findViewById(R.id.font_awesome_android_icon);
        tvBack.setTypeface(iconFont);
        tvBack.setOnClickListener(bacKClick);

        mActionBar.setCustomView(mCustomView);
        mActionBar.setDisplayShowCustomEnabled(true);

        initViews();
    }

    View.OnClickListener bacKClick = new View.OnClickListener() {
        @Override
        public void onClick(View v) {
            finish();
        }
    };

    public void initViews() {
        iconFont = FontManager.getTypeface(getApplicationContext(), FontManager.FONTAWESOME);
        Typeface ttf = Utilz.getTypeface(RegistrationActivity.this);

        icinfo = (TextView) findViewById(R.id.icinfo);
        icinfo.setTypeface(iconFont);
        icinfo.setOnClickListener(infoclickListener);

        icFname = (TextView) findViewById(R.id.icFname);
        icFname.setTypeface(iconFont);

        icLname = (TextView) findViewById(R.id.icLname);
        icLname.setTypeface(iconFont);

        icEmail = (TextView) findViewById(R.id.icEmail);
        icEmail.setTypeface(iconFont);

        icPassword = (TextView) findViewById(R.id.icPassword);
        icPassword.setTypeface(iconFont);

        tvCountryCode = (TextView) findViewById(R.id.tvCountryCode);

        try {
            Locale current = getResources().getConfiguration().locale;
            PhoneNumberUtil phoneUtil = PhoneNumberUtil.getInstance();
            countryCode = "+" + String.valueOf(phoneUtil.getCountryCodeForRegion(current.getCountry()));
            tvCountryCode.setText("(" + countryCode + ")");
        } catch (Exception e) {
            System.err.println("NumberParseException was thrown: " + e.toString());
        }

        tvCountryCode.setOnClickListener(countrySelectionClick);

        icCPassword = (TextView) findViewById(R.id.icCPassword);
        icCPassword.setTypeface(iconFont);

        tvTnc = (TextView) findViewById(R.id.tvTnc);
        tvTnc.setTypeface(Utilz.getTypefaceNormal(RegistrationActivity.this));

        tvTnc.setMovementMethod(LinkMovementMethod.getInstance());
        String normalBefore = getString(R.string.tnc1);
        String termsAndCondition = getString(R.string.tnc2);
        String finalString = normalBefore + "" + termsAndCondition;

        Spannable sb = new SpannableString(finalString);

        sb.setSpan(
                new ClickableSpan() {
                    @Override
                    public void onClick(View widget) {
                        startActivity(new Intent(RegistrationActivity.this, TermsAndConditionsActivity.class));
                    }

                    @Override
                    public void updateDrawState(TextPaint ds) {
                        ds.setUnderlineText(true);
                    }
                }, finalString.indexOf(termsAndCondition), normalBefore.length() + termsAndCondition.length(), Spanned.SPAN_INCLUSIVE_INCLUSIVE);

        tvTnc.setText(sb);

        icPhoneNo = (TextView) findViewById(R.id.icPhoneNo);
        icPhoneNo.setTypeface(iconFont);

        edFname = (EditText) findViewById(R.id.edFname);
        edFname.setTypeface(Utilz.getTypefaceBold(RegistrationActivity.this));

        edLname = (EditText) findViewById(R.id.edLname);
        edLname.setTypeface(Utilz.getTypefaceBold(RegistrationActivity.this));

        edEmail = (EditText) findViewById(R.id.edEmail);
        edEmail.setTypeface(Utilz.getTypefaceBold(RegistrationActivity.this));

        edPassword = (EditText) findViewById(R.id.edPassword);
        edPassword.setTypeface(Utilz.getTypefaceBold(RegistrationActivity.this));

        edCPassword = (EditText) findViewById(R.id.edCPassword);
        edCPassword.setTypeface(Utilz.getTypefaceBold(RegistrationActivity.this));

        edPhoneNo = (EditText) findViewById(R.id.edPhoneNo);
        edPhoneNo.setTypeface(Utilz.getTypefaceBold(RegistrationActivity.this));

        btnCotinue = (Button) findViewById(R.id.btnCotinue);
        btnCotinue.setTypeface(Utilz.getTypefaceMedium(RegistrationActivity.this));

        btnCotinue.setOnClickListener(continueclick);
    }

    View.OnClickListener countrySelectionClick = new View.OnClickListener() {
        @Override
        public void onClick(View v) {
            AlertDialog.Builder builder = new AlertDialog.Builder(RegistrationActivity.this);
            builder.setTitle(R.string.select_country)
                    .setNegativeButton(android.R.string.cancel, new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialogInterface, int i) {
                            dialogInterface.dismiss();
                        }
                    }).setItems(Constants.MM_COUNTRY_NAMES, new DialogInterface.OnClickListener() {
                public void onClick(DialogInterface dialog, int which) {
                    dialog.dismiss();
                    try {
                        countryCode = "(+" + Constants.MM_COUNTRY_MAP.get(Constants.MM_COUNTRY_NAMES[which].toString()) + ")";
                        tvCountryCode.setText(countryCode);

                    } catch (Exception ex) {
                        ex.printStackTrace();
                    }
                }
            });
            builder.create();
            builder.show();
        }
    };

    View.OnClickListener continueclick = new View.OnClickListener() {
        @Override
        public void onClick(View v) {

            Utilz.hideSoftKeyboard(RegistrationActivity.this);

            String firstName = edFname.getText().toString();
            if (firstName != null) {
                if (firstName == null || firstName.isEmpty()) {
                    edFname.setError(getString(R.string.error_fname));
                    edFname.requestFocus();
                    return;
                }
            }

            String lastname = edLname.getText().toString();
            if (lastname == null || lastname.isEmpty()) {
                edLname.setError(getString(R.string.error_lname));
                edLname.requestFocus();
                return;
            }

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

            String cpassword = edCPassword.getText().toString();
            if (cpassword == null || cpassword.isEmpty()) {
                edCPassword.setError(getString(R.string.error_cpassword));
                edCPassword.requestFocus();
                return;
            }

            try {
                if (!cpassword.matches("((?=.*\\d)(?=.*[A-Z]).{6,})")) {
                    edCPassword.setError(getString(R.string.password_hint));
                    edCPassword.requestFocus();
                    return;
                }
            } catch (PatternSyntaxException ex) {
                Toast.makeText(getApplicationContext(), ex.getMessage(), Toast.LENGTH_SHORT).show();
            }

            if (!cpassword.equals(password)) {
                edCPassword.setError(getString(R.string.error_cpassword2));
                edCPassword.requestFocus();
                return;
            }

            String phone = edPhoneNo.getText().toString();
            if (phone == null || phone.isEmpty() || phone.trim().length() != 10) {
                edPhoneNo.setError(getString(R.string.error_phoneno));
                edPhoneNo.requestFocus();
                return;
            }

            if (Utilz.isNetworkAvailable(RegistrationActivity.this)) {

                AlertDialog.Builder builder1 = new AlertDialog.Builder(RegistrationActivity.this);
                builder1.setTitle(getString(R.string.conf_details));
                builder1.setMessage(String.format(getString(R.string.conf_details1), email, countryCode + phone));
                builder1.setCancelable(false);

                builder1.setPositiveButton(
                        android.R.string.ok,
                        new DialogInterface.OnClickListener() {
                            public void onClick(DialogInterface dialog, int id) {
                                dialog.cancel();
                                doSignUp();
                            }
                        }).setNegativeButton(R.string.cancel, new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        dialog.dismiss();
                    }
                });

                AlertDialog alert11 = builder1.create();
                alert11.show();


            } else
                Utilz.showMessage2(RegistrationActivity.this, getString(R.string.network_error));
        }
    };

    View.OnClickListener infoclickListener = new View.OnClickListener() {
        @Override
        public void onClick(View v) {
            AlertDialog userDialog;
            final AlertDialog.Builder builder = new AlertDialog.Builder(RegistrationActivity.this);
            builder.setMessage(getString(R.string.password_hint)).setPositiveButton(android.R.string.ok, new DialogInterface.OnClickListener() {
                @Override
                public void onClick(DialogInterface dialog, int which) {
                    dialog.dismiss();
                }
            });
            userDialog = builder.create();
            userDialog.show();
        }
    };

    private void doSignUp() {
        Utilz.showProgressDialog(RegistrationActivity.this);

        AsyncHttpClient client = new AsyncHttpClient();
        JSONObject objMain = new JSONObject();
        try {
            countryCode = countryCode.replace("(", "");
            countryCode = countryCode.replace(")", "");
            objMain.put(Constants.MM_email, edEmail.getText().toString().trim());
            objMain.put(Constants.MM_password, edPassword.getText().toString().trim());
            objMain.put(Constants.MM_firstname, edFname.getText().toString().trim());
            objMain.put(Constants.MM_lastname, edLname.getText().toString().trim());
            objMain.put(Constants.MM_phone_no, countryCode + edPhoneNo.getText().toString().trim());

            StringEntity entity = new StringEntity(objMain.toString());
            client.post(RegistrationActivity.this, Constants.Base_url + Constants.API_SIGNUP, entity, "application/json", new SignUpHandler());

        } catch (Exception e) {
            e.printStackTrace();
            Utilz.closeProgressDialog();
        }
    }

    private class SignUpHandler extends AsyncHttpResponseHandler {

        @Override
        public void onSuccess(int statusCode, Header[] headers, byte[] responseBody) {
            Utilz.closeProgressDialog();
            JSONObject response = null;
            try {

                response = new JSONObject(new String(responseBody));
//                makeDDbEntryForSubscription(response, mainPurchase);
                Log.i("response.) ", response.toString());
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

                        Utilz.setUserDetails(RegistrationActivity.this, user);

                        if (Utilz.getUserDetails(RegistrationActivity.this).get(Constants.MM_is_active).equalsIgnoreCase("0")) {
                            showMessage2();
                        } else {
                            Toast.makeText(RegistrationActivity.this, response.getString(Constants.MM_msg), Toast.LENGTH_SHORT).show();
                            gotoScheduleActivity();
                        }
                    }
                } else {
                    Toast.makeText(RegistrationActivity.this, response.getString(Constants.MM_msg), Toast.LENGTH_SHORT).show();
                }
            } catch (Exception ex) {
                ex.printStackTrace();
            }
        }

        @Override
        public void onFailure(int statusCode, Header[] headers, byte[] responseBody, Throwable error) {
            Utilz.closeProgressDialog();
            Toast.makeText(RegistrationActivity.this, getString(R.string.server_error), Toast.LENGTH_SHORT).show();
        }
    }

    private void clearData() {
        edFname.setText("");
        edPhoneNo.setText("");
        edCPassword.setText("");
        edLname.setText("");
        edPassword.setText("");
        edEmail.setText("");
    }

    public void showMessage2() {
        clearData();
        AlertDialog.Builder builder1 = new AlertDialog.Builder(RegistrationActivity.this);
        builder1.setMessage(getString(R.string.acc_deactive_new_msg));
        builder1.setCancelable(false);

        builder1.setPositiveButton(
                android.R.string.ok,
                new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int id) {
                        dialog.cancel();
                        startActivity(new Intent(RegistrationActivity.this, VerifyOtpActivity.class));
                    }
                });

        AlertDialog alert11 = builder1.create();
        alert11.show();
    }

    private void gotoScheduleActivity() {
        Intent i = new Intent(RegistrationActivity.this, ScheduleActivity.class);
        Utilz.saveBooleanToUserDefaults(RegistrationActivity.this, Constants.MM_is_new_register, true);
        i.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
        startActivity(i);
        overridePendingTransition(R.anim.anim_slide_in_left,
                R.anim.anim_slide_out_left);
        finish();
    }

//    TextView tvSendOtp;
//    Dialog insertOtpDialog;
//    Button btnOTP;
//    int resendOtpCount = 0;
//
//    public void insertOtpPopUp() {
//        insertOtpDialog = new Dialog(RegistrationActivity.this);
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
//        tvTitle.setTypeface(Utilz.getTypefaceBold(RegistrationActivity.this));
//
//        final EditText edOtp = (EditText) insertOtpDialog.findViewById(R.id.edOtp);
//        edOtp.setTypeface(Utilz.getTypefaceBold(RegistrationActivity.this));
//        Button cancel = (Button) insertOtpDialog.findViewById(R.id.btnCancel);
//        cancel.setTypeface(Utilz.getTypefaceBold(RegistrationActivity.this));
//        Button continuebtn = (Button) insertOtpDialog.findViewById(R.id.btnContinue);
//        continuebtn.setTypeface(Utilz.getTypefaceBold(RegistrationActivity.this));
//
//        btnOTP = (Button) insertOtpDialog.findViewById(R.id.btnOTP);
//        btnOTP.setTypeface(Utilz.getTypefaceBold(RegistrationActivity.this));
//
//        tvSendOtp = (TextView) insertOtpDialog.findViewById(R.id.tvSendOtp);
//        tvSendOtp.setTypeface(Utilz.getTypefaceBold(RegistrationActivity.this));
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
//                Utilz.setUserDetails(RegistrationActivity.this, null);
//                insertOtpDialog.dismiss();
//                Toast.makeText(getApplicationContext(), getString(R.string.invalid_otp3),Toast.LENGTH_SHORT).show();
//                finish();
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
//                if( otp.length() != 6){
//                    edOtp.setError(getString(R.string.invalid_otp2));
//                    edOtp.requestFocus();
//                    return;
//                }
//
//                if (Utilz.isNetworkAvailable(RegistrationActivity.this)) {
//                    verifyPhone(edOtp.getText().toString());
//                    Utilz.hideSoftKeyboard(RegistrationActivity.this);
//                } else {
//                    Toast.makeText(getApplicationContext(), getString(R.string.network_error), Toast.LENGTH_SHORT).show();
//                }
//            }
//        });
//
//        insertOtpDialog.show();
//        startTimer();
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
//        Utilz.showProgressDialog(RegistrationActivity.this);
//
//        AsyncHttpClient client = new AsyncHttpClient();
//        JSONObject objMain = new JSONObject();
//        try {
//            objMain.put(Constants.MM_user_id, Integer.parseInt(Utilz.getUserDetails(RegistrationActivity.this).get(Constants.MM_user_id)));
//
//            StringEntity entity = new StringEntity(objMain.toString());
//            client.post(RegistrationActivity.this, Constants.Base_url + Constants.API_resend_otp, entity, "application/json", new ResendOtpHandler());
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
////                makeDDbEntryForSubscription(response, mainPurchase);
//                Log.i("response.) ", response.toString());
//                if (response.has(Constants.MM_STATUS_CODE) && response.getString(Constants.MM_STATUS_CODE).equalsIgnoreCase(Constants.MM_STATUS_CODE_SUCCESS)) {
//                    Toast.makeText(RegistrationActivity.this, response.getString(Constants.MM_msg), Toast.LENGTH_SHORT).show();
//                    startTimer();
//                } else {
//                    AlertDialog.Builder builder1 = new AlertDialog.Builder(RegistrationActivity.this);
//                    builder1.setMessage(response.getString(Constants.MM_msg));
//                    builder1.setCancelable(false);
//
//                    builder1.setPositiveButton(
//                            android.R.string.ok,
//                            new DialogInterface.OnClickListener() {
//                                public void onClick(DialogInterface dialog, int id) {
//                                    Utilz.setUserDetails(RegistrationActivity.this, null);
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
//            Toast.makeText(RegistrationActivity.this, getString(R.string.server_error), Toast.LENGTH_SHORT).show();
//        }
//    }
//
//
//    private void verifyPhone(String otp) {
//        Utilz.showProgressDialog(RegistrationActivity.this);
//
//        AsyncHttpClient client = new AsyncHttpClient();
//        JSONObject objMain = new JSONObject();
//        try {
//            objMain.put(Constants.MM_user_id, Integer.parseInt(Utilz.getUserDetails(RegistrationActivity.this).get(Constants.MM_user_id)));
//            objMain.put(Constants.MM_otp, otp);
//
//            StringEntity entity = new StringEntity(objMain.toString());
//            client.post(RegistrationActivity.this, Constants.Base_url + Constants.API_verify_phone_no, entity, "application/json", new VerifyPhoneNumberHandler());
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
//                    Toast.makeText(RegistrationActivity.this, response.getString(Constants.MM_msg), Toast.LENGTH_SHORT).show();
//                }
//            } catch (Exception ex) {
//                ex.printStackTrace();
//            }
//        }
//
//        @Override
//        public void onFailure(int statusCode, Header[] headers, byte[] responseBody, Throwable error) {
//            Utilz.closeProgressDialog();
//            Toast.makeText(RegistrationActivity.this, getString(R.string.server_error), Toast.LENGTH_SHORT).show();
//        }
//    }

}
