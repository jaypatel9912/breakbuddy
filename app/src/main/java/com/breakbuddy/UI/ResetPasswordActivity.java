package com.breakbuddy.UI;

import android.app.Dialog;
import android.content.Intent;
import android.graphics.drawable.ColorDrawable;
import android.net.Uri;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.Gravity;
import android.view.View;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import com.breakbuddy.R;
import com.loopj.android.http.AsyncHttpClient;
import com.loopj.android.http.AsyncHttpResponseHandler;

import org.json.JSONObject;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.Set;

import Utils.Constants;
import Utils.Utilz;
import cz.msebera.android.httpclient.Header;
import cz.msebera.android.httpclient.entity.StringEntity;

public class ResetPasswordActivity extends AppCompatActivity {

    ArrayList<String> params;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_link_test);

        Intent intent = getIntent();
        if (Intent.ACTION_VIEW.equals(intent.getAction())) {
            final Uri myURI = intent.getData();
            if (myURI != null) {
                params = new ArrayList<>();
                Set<String> str = myURI.getQueryParameterNames();
                for (Iterator<String> it = str.iterator(); it.hasNext(); ) {
                    params.add(it.next());
                }
                if (params.size() == 0) {
                    moveToSplash();
                } else if (params.size() == 1) {
                    verifyAccount(params.get(0));
                    moveToSplash();
                } else {
                    if (params.get(0).equalsIgnoreCase(Utilz.getPreference(ResetPasswordActivity.this, Constants.MM_reset_password_token))) {
                        showResetPasswordDialog();
                    } else {

                        Utilz.setPreference(ResetPasswordActivity.this, Constants.MM_reset_password_token, "");
                        moveToLogin();
                    }
                }
            } else {
                moveToLogin();
            }
        } else {
            moveToLogin();
        }
    }

    private void showResetPasswordDialog() {
        final Dialog dialog = new Dialog(ResetPasswordActivity.this);
        dialog.setContentView(R.layout.change_password_layout);
        dialog.getWindow().setBackgroundDrawable(new ColorDrawable(android.graphics.Color.TRANSPARENT));

        final EditText edOldPassword = (EditText) dialog.findViewById(R.id.edOldPassword);
        final EditText edNewPassword = (EditText) dialog.findViewById(R.id.edNewPassword);
        final EditText edNewConfirmPassword = (EditText) dialog.findViewById(R.id.edNewConfirmPassword);
        Button cancel = (Button) dialog.findViewById(R.id.btnCancel);
        Button btnChange = (Button) dialog.findViewById(R.id.btnChange);

        dialog.findViewById(R.id.llCurrent).setVisibility(View.GONE);

        cancel.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                dialog.dismiss();
                Utilz.setPreference(ResetPasswordActivity.this, Constants.MM_reset_password_token, "");
                moveToLogin();
            }
        });

        btnChange.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

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

                if (!newcpass.equalsIgnoreCase(newpass)) {
                    edNewConfirmPassword.setError(getString(R.string.error_new_confirm_password));
                    edNewConfirmPassword.requestFocus();
                    return;
                }
                dialog.dismiss();

                if (Utilz.isNetworkAvailable(ResetPasswordActivity.this)) {
                    resetPasswordRequest(newcpass);
                } else {
                    Utilz.showMessage2(ResetPasswordActivity.this, getString(R.string.network_error));
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


    private void resetPasswordRequest(String newPass) {
        Utilz.showProgressDialog(ResetPasswordActivity.this);
        AsyncHttpClient client = new AsyncHttpClient();
        JSONObject objMain = new JSONObject();
        try {
            objMain.put(Constants.MM_email, params.get(1));
            objMain.put(Constants.MM_password, newPass);

            StringEntity entity = new StringEntity(objMain.toString());
            client.post(ResetPasswordActivity.this, Constants.Base_url + Constants.API_reset_password, entity, "application/json", new resetPasswordHandler());

        } catch (Exception e) {
            e.printStackTrace();
            Utilz.closeProgressDialog();
        }
    }

    private class resetPasswordHandler extends AsyncHttpResponseHandler {

        @Override
        public void onSuccess(int statusCode, Header[] headers, byte[] responseBody) {
            Utilz.closeProgressDialog();
            JSONObject response = null;
            try {
                response = new JSONObject(new String(responseBody));
                Log.i("response.) ", response.toString());
                if (response.has(Constants.MM_STATUS_CODE) && response.getString(Constants.MM_STATUS_CODE).equalsIgnoreCase(Constants.MM_STATUS_CODE_SUCCESS)) {
                    Toast.makeText(getApplicationContext(), response.get(Constants.MM_msg).toString(), Toast.LENGTH_SHORT).show();
                } else {
                    Toast.makeText(getApplicationContext(), response.get(Constants.MM_msg).toString(), Toast.LENGTH_SHORT).show();
                }

                Utilz.setPreference(ResetPasswordActivity.this, Constants.MM_reset_password_token, "");
                moveToLogin();

            } catch (Exception ex) {
                ex.printStackTrace();
            }
        }

        @Override
        public void onFailure(int statusCode, Header[] headers, byte[] responseBody, Throwable error) {
            Utilz.closeProgressDialog();
            Toast.makeText(ResetPasswordActivity.this, getString(R.string.server_error), Toast.LENGTH_SHORT).show();
        }
    }

    private void moveToLogin() {
        Intent i;
        if (Utilz.getUserDetails(ResetPasswordActivity.this) != null)
            i = new Intent(ResetPasswordActivity.this, SplashActivity.class);
        else
            i = new Intent(ResetPasswordActivity.this, LoginActivity.class);

        i.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
        startActivity(i);
        finish();
    }


    private void moveToSplash() {
        Intent i = new Intent(ResetPasswordActivity.this, SplashActivity.class);
        i.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
        startActivity(i);
        finish();
    }

    private void verifyAccount(String token) {
        AsyncHttpClient client = new AsyncHttpClient();
        JSONObject objMain = new JSONObject();
        try {
            objMain.put(Constants.MM_verification_token, token);

            StringEntity entity = new StringEntity(objMain.toString());
            client.post(ResetPasswordActivity.this, Constants.Base_url + Constants.API_verify_account, entity, "application/json", new VerifyAccountHandler());

        } catch (Exception e) {
            e.printStackTrace();
            Utilz.closeProgressDialog();
        }
    }


    private class VerifyAccountHandler extends AsyncHttpResponseHandler {

        @Override
        public void onSuccess(int statusCode, Header[] headers, byte[] responseBody) {
            JSONObject response = null;
            try {
                response = new JSONObject(new String(responseBody));
                Log.i("response.) ", response.toString());
            } catch (Exception ex) {
                ex.printStackTrace();
            }
        }

        @Override
        public void onFailure(int statusCode, Header[] headers, byte[] responseBody, Throwable error) {
            Log.i("response.) ", new String(responseBody));
        }
    }


}
