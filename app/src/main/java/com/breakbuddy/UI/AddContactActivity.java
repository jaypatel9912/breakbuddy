package com.breakbuddy.UI;

import android.Manifest;
import android.annotation.TargetApi;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.Typeface;
import android.net.Uri;
import android.os.Build;
import android.provider.ContactsContract;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AlertDialog;
import android.os.Bundle;
import android.support.v7.widget.DefaultItemAnimator;
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

import org.json.JSONArray;
import org.json.JSONObject;

import java.lang.reflect.Type;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;

import API.GetContacts;
import Adapters.BBContactAdapter;
import Adapters.ContactAdapter;
import Model.Contact;
import Model.Friend;
import Utils.Constants;
import Utils.FontManager;
import Utils.Utilz;
import cz.msebera.android.httpclient.Header;
import cz.msebera.android.httpclient.entity.StringEntity;

public class AddContactActivity extends BaseActivity implements ContactAdapter.InviteListener, GetContacts.OnContactLoad {

    private RecyclerView rlContacts, rlBBContacts;
    TextView tvNoConatcs, tvBBMyFriends, tvAddByPhone;
    ContactAdapter mAdapter;
    BBContactAdapter mBBAdapter;
    Typeface iconFont, bold, medium;
    ContactAdapter.InviteListener listener;
    List<Friend> friendArrayList, BBfriendArrayList;
    EditText edSearch;
    public static final int REQUEST_ID_MULTIPLE_PERMISSIONS = 1;
    GetContacts.OnContactLoad contactLoadListener;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        getWindow().setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_STATE_ALWAYS_HIDDEN);
        setContentView(R.layout.activity_add_contact);

        listener = this;
        contactLoadListener = this;
        friendArrayList = new ArrayList<>();
        BBfriendArrayList = new ArrayList<>();

        android.support.v7.app.ActionBar mActionBar = getSupportActionBar();
        mActionBar.setDisplayShowHomeEnabled(false);
        mActionBar.setDisplayShowTitleEnabled(false);
        LayoutInflater mInflater = LayoutInflater.from(this);

        View mCustomView = mInflater.inflate(R.layout.toolbar, null);
        TextView mTitleTextView = (TextView) mCustomView.findViewById(R.id.title_text);
        mTitleTextView.setText(getString(R.string.add_frnds));
        mTitleTextView.setTypeface(Utilz.getTypefaceBold(AddContactActivity.this));

        iconFont = FontManager.getTypeface(getApplicationContext(), FontManager.FONTAWESOME);
        bold = Utilz.getTypefaceBold(AddContactActivity.this);
        medium = Utilz.getTypefaceMedium(AddContactActivity.this);

        TextView tvBack = (TextView) mCustomView.findViewById(R.id.font_awesome_android_icon);
        tvBack.setVisibility(View.VISIBLE);
        tvBack.setTypeface(iconFont);
        tvBack.setOnClickListener(backClickListener);

        mActionBar.setCustomView(mCustomView);
        mActionBar.setDisplayShowCustomEnabled(true);

        initViews();
        AllPermisssionGranted();

    }

    private void AllPermisssionGranted() {
        int permissionPhonestate = ContextCompat.checkSelfPermission(this, Manifest.permission.READ_CONTACTS);
        int writePermission = ContextCompat.checkSelfPermission(this, Manifest.permission.GET_ACCOUNTS);

        List<String> listPermissionsNeeded = new ArrayList<>();
        if (permissionPhonestate != PackageManager.PERMISSION_GRANTED) {
            listPermissionsNeeded.add(Manifest.permission.READ_CONTACTS);
        }
        if (writePermission != PackageManager.PERMISSION_GRANTED) {
            listPermissionsNeeded.add(Manifest.permission.GET_ACCOUNTS);
        }

        if (!listPermissionsNeeded.isEmpty()) {
            ActivityCompat.requestPermissions(this, listPermissionsNeeded.toArray(new String[listPermissionsNeeded.size()]), REQUEST_ID_MULTIPLE_PERMISSIONS);
        } else {
            if (Constants.contacts.size() <= 0) {
                new GetContacts(AddContactActivity.this, contactLoadListener).execute();
            } else {
                prepareContacts(true);
            }
        }
    }

    @TargetApi(Build.VERSION_CODES.M)
    @Override
    public void onRequestPermissionsResult(int requestCode, String[] permissions, int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        Log.e("requestcode", String.valueOf(grantResults));

        switch (requestCode) {
            case REQUEST_ID_MULTIPLE_PERMISSIONS: {
                if (permissions.length == grantResults.length) {
                    if (ContextCompat.checkSelfPermission(this, Manifest.permission.READ_CONTACTS) == PackageManager.PERMISSION_GRANTED &&
                            ContextCompat.checkSelfPermission(this, Manifest.permission.GET_ACCOUNTS) == PackageManager.PERMISSION_GRANTED) {
                        if (Constants.contacts.size() <= 0) {
                            new GetContacts(AddContactActivity.this, contactLoadListener).execute();
                        } else {
                            prepareContacts(true);
                        }
                    } else {
                        finish();
                    }
                } else {
                    finish();
                }
            }
            return;
        }
    }

    @Override
    public void successToLoadContacts() {
        prepareContacts(true);
    }

    View.OnClickListener backClickListener = new View.OnClickListener() {
        @Override
        public void onClick(View v) {
            finish();
        }
    };

    private void initViews() {
        tvNoConatcs = (TextView) findViewById(R.id.tvNoConatcs);
        tvNoConatcs.setTypeface(medium);

        tvBBMyFriends = (TextView) findViewById(R.id.tvBBMyFriends);
        tvBBMyFriends.setTypeface(bold);
        tvAddByPhone = (TextView) findViewById(R.id.tvAddByPhone);
        tvAddByPhone.setTypeface(bold);
        edSearch = (EditText) findViewById(R.id.edSearch);
        edSearch.setTypeface(medium);

        rlContacts = (RecyclerView) findViewById(R.id.rlContacts);
        mAdapter = new ContactAdapter(AddContactActivity.this, listener);
        RecyclerView.LayoutManager mLayoutManager = new LinearLayoutManager(getApplicationContext());
        rlContacts.setLayoutManager(mLayoutManager);
        rlContacts.addItemDecoration(new Utils.DividerItemDecoration(AddContactActivity.this, R.drawable.rcl_divider));
        rlContacts.setItemAnimator(new DefaultItemAnimator());
        rlContacts.setAdapter(mAdapter);

        rlBBContacts = (RecyclerView) findViewById(R.id.rlBBContacts);
        mBBAdapter = new BBContactAdapter(AddContactActivity.this, listener);
        RecyclerView.LayoutManager mLayoutManager2 = new LinearLayoutManager(getApplicationContext());
        rlBBContacts.setLayoutManager(mLayoutManager2);
        rlBBContacts.addItemDecoration(new Utils.DividerItemDecoration(AddContactActivity.this, R.drawable.rcl_divider));
        rlBBContacts.setItemAnimator(new DefaultItemAnimator());
        rlBBContacts.setAdapter(mBBAdapter);


        edSearch.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                if (s.length() <= 0) {
                    setAdapter();
                } else {
                    List<Friend> list = filter(BBfriendArrayList, s.toString());
                    if (list.size() > 0) {
                        mBBAdapter.refreshList(list);
                        tvBBMyFriends.setVisibility(View.VISIBLE);
                        rlBBContacts.setVisibility(View.VISIBLE);
                    } else {
                        rlBBContacts.setVisibility(View.GONE);
                        tvBBMyFriends.setVisibility(View.GONE);
                    }

                    List<Friend> list2 = filter(friendArrayList, s.toString());
                    if (list2.size() > 0) {
                        mAdapter.refreshList(list2);
                        tvAddByPhone.setVisibility(View.VISIBLE);
                        rlContacts.setVisibility(View.VISIBLE);
                    } else {
                        rlContacts.setVisibility(View.GONE);
                        tvAddByPhone.setVisibility(View.GONE);
                    }

                    if (list.size() <= 0 && list2.size() <= 0)
                        tvNoConatcs.setVisibility(View.VISIBLE);
                    else
                        tvNoConatcs.setVisibility(View.GONE);
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
            final String text = model.getName().toLowerCase();
            if (text.contains(lowerCaseQuery)) {
                filteredModelList.add(model);
            }
        }
        return filteredModelList;
    }

    private void prepareContacts(boolean showProgress) {

        if (!Utilz.isNetworkAvailable(AddContactActivity.this)) {

            for (Contact contact : Constants.contacts) {
                Friend friend = new Friend();
                friend.setEmail(contact.getEmail());
                friend.setStatus("0");
                friend.setPhone_no(contact.getPhoneNo());
                friend.setName(contact.getName());
                friendArrayList.add(friend);
            }
            setAdapter();
            Utilz.closeProgressDialog();

        } else {
            String myEmail = Utilz.getUserDetails(AddContactActivity.this).get(Constants.MM_email);
            String myPhoneNo = Utilz.getUserDetails(AddContactActivity.this).get(Constants.MM_phone_no);

            JSONArray arrayContacts = new JSONArray();

            for (Contact contact : Constants.contacts) {
                if (contact.getEmail() != null && !contact.getEmail().isEmpty() && contact.getPhoneNo() != null && !contact.getPhoneNo().isEmpty()) {
                    if (!(myEmail.trim().equalsIgnoreCase(contact.getEmail().trim()) || myPhoneNo.trim().equalsIgnoreCase(contact.getPhoneNo().trim()))) {
                        try {
                            JSONObject obj = new JSONObject();
                            obj.put(Constants.MM_email, contact.getEmail() == null ? "" : contact.getEmail());
                            obj.put(Constants.MM_phone_no, contact.getPhoneNo() == null ? "" : contact.getPhoneNo());
                            obj.put(Constants.MM_status, "0");
                            obj.put(Constants.MM_user_id, "");
                            obj.put(Constants.MM_friend_user_id, "");

                            arrayContacts.put(obj);
                        } catch (Exception ex) {
                            ex.printStackTrace();
                        }
                    }
                } else if (contact.getEmail() != null) {
                    if (!contact.getEmail().trim().equalsIgnoreCase(myEmail)) {
                        try {
                            JSONObject obj = new JSONObject();
                            obj.put(Constants.MM_email, contact.getEmail() == null ? "" : contact.getEmail());
                            obj.put(Constants.MM_phone_no, contact.getPhoneNo() == null ? "" : contact.getPhoneNo());
                            obj.put(Constants.MM_status, "0");
                            obj.put(Constants.MM_user_id, "");
                            obj.put(Constants.MM_friend_user_id, "");

                            arrayContacts.put(obj);
                        } catch (Exception ex) {
                            ex.printStackTrace();
                        }
                    }
                } else if (contact.getPhoneNo() != null) {
                    if (!contact.getPhoneNo().trim().equalsIgnoreCase(myPhoneNo)) {
                        try {
                            JSONObject obj = new JSONObject();
                            obj.put(Constants.MM_email, contact.getEmail() == null ? "" : contact.getEmail());
                            obj.put(Constants.MM_phone_no, contact.getPhoneNo() == null ? "" : contact.getPhoneNo());
                            obj.put(Constants.MM_status, "0");
                            obj.put(Constants.MM_user_id, "");
                            obj.put(Constants.MM_friend_user_id, "");

                            arrayContacts.put(obj);
                        } catch (Exception ex) {
                            ex.printStackTrace();
                        }
                    }
                }
            }

            if (!Utilz.isNetworkAvailable(AddContactActivity.this)) {
                Utilz.showMessage2(AddContactActivity.this, getString(R.string.network_error));
                return;
            }

            findBreakBuddyFriends(arrayContacts, showProgress);
        }


    }


    private void findBreakBuddyFriends(JSONArray jsonArray, boolean showProgress) {
        if (showProgress)
            Utilz.showProgressDialog(AddContactActivity.this);
        AsyncHttpClient client = new AsyncHttpClient();
        JSONObject objMain = new JSONObject();
        try {
            objMain.put(Constants.MM_user_id, Utilz.getUserDetails(AddContactActivity.this).get(Constants.MM_user_id));
            objMain.put(Constants.MM_users, jsonArray);

            StringEntity entity = new StringEntity(objMain.toString());
            client.post(AddContactActivity.this, Constants.Base_url + Constants.API_find_breakbuddy_friends, entity, "application/json", new FindFriendsResponseHandler());

        } catch (Exception e) {
            e.printStackTrace();
            Utilz.closeProgressDialog();
        }
    }


    private class FindFriendsResponseHandler extends AsyncHttpResponseHandler {

        @Override
        public void onSuccess(int statusCode, Header[] headers, byte[] responseBody) {
            JSONObject response = null;
            try {
                response = new JSONObject(new String(responseBody));
                Log.i("response.) ", response.toString());
                if (response.has(Constants.MM_STATUS_CODE) && response.getString(Constants.MM_STATUS_CODE).equalsIgnoreCase(Constants.MM_STATUS_CODE_SUCCESS)) {


                    JSONArray jsonUnfriend = response.getJSONArray(Constants.MM_unregistered);
                    JSONArray jsonFriend = response.getJSONArray(Constants.MM_registered);
                    Type listType = new TypeToken<List<Friend>>() {
                    }.getType();

                    BBfriendArrayList = (List<Friend>) new Gson().fromJson(jsonFriend.toString(), listType);
//
                    friendArrayList = ((List<Friend>) new Gson().fromJson(jsonUnfriend.toString(), listType));

                    setupNames();
                    Collections.sort(friendArrayList, new CustomComparator());
                    Collections.sort(BBfriendArrayList, new CustomComparator());
                    setAdapter();


                } else {
                    Utilz.closeProgressDialog();
                    Utilz.showMessage2(AddContactActivity.this, response.get(Constants.MM_msg).toString());
                }

            } catch (Exception ex) {
                ex.printStackTrace();
            }
        }

        @Override
        public void onFailure(int statusCode, Header[] headers, byte[] responseBody, Throwable error) {
            Utilz.closeProgressDialog();
            Toast.makeText(AddContactActivity.this, getString(R.string.server_error), Toast.LENGTH_SHORT).show();
        }
    }

    public class CustomComparator implements Comparator<Friend> {
        @Override
        public int compare(Friend o1, Friend o2) {
            return o1.getName().compareTo(o2.getName());
        }
    }

    private void setupNames() {

        for (Contact contact : Constants.contacts) {
            for (int i = 0; i < friendArrayList.size(); i++) {
                Friend friend = friendArrayList.get(i);
                if (friend.getEmail() != null && !friend.getEmail().isEmpty() && friend.getEmail().equalsIgnoreCase(contact.getEmail())) {
                    friend.setName(contact.getName());
                } else if (friend.getPhone_no() != null && !friend.getPhone_no().isEmpty() && friend.getPhone_no().equalsIgnoreCase(contact.getPhoneNo())) {
                    friend.setName(contact.getName());
                }
                friendArrayList.set(i, friend);
            }
        }

        for (Contact contact : Constants.contacts) {
            for (int i = 0; i < BBfriendArrayList.size(); i++) {
                Friend friend = BBfriendArrayList.get(i);
                if (friend.getEmail() != null && !friend.getEmail().isEmpty() && friend.getEmail().equalsIgnoreCase(contact.getEmail())) {
                    friend.setName(contact.getName());
                } else if (friend.getPhone_no() != null && !friend.getPhone_no().isEmpty() && friend.getPhone_no().equalsIgnoreCase(contact.getPhoneNo())) {
                    friend.setName(contact.getName());
                }
                BBfriendArrayList.set(i, friend);
            }
        }
    }

    private void setAdapter() {
        mAdapter.refreshList(friendArrayList);
        mBBAdapter.refreshList(BBfriendArrayList);

        if (friendArrayList.size() > 0) {
            tvNoConatcs.setVisibility(View.GONE);
            rlContacts.setVisibility(View.VISIBLE);
            tvAddByPhone.setVisibility(View.VISIBLE);
        } else {
            rlContacts.setVisibility(View.GONE);
            tvAddByPhone.setVisibility(View.GONE);
        }

        if (BBfriendArrayList.size() > 0) {
            tvNoConatcs.setVisibility(View.GONE);
            rlBBContacts.setVisibility(View.VISIBLE);
            tvBBMyFriends.setVisibility(View.VISIBLE);
        } else {
            rlBBContacts.setVisibility(View.GONE);
            tvBBMyFriends.setVisibility(View.GONE);
        }

        if (friendArrayList.size() <= 0 && BBfriendArrayList.size() <= 0) {
            tvNoConatcs.setVisibility(View.VISIBLE);
            rlContacts.setVisibility(View.GONE);
            tvAddByPhone.setVisibility(View.GONE);
            rlBBContacts.setVisibility(View.GONE);
            tvBBMyFriends.setVisibility(View.GONE);
        }
//        edSearch.setText("");
        rlBBContacts.setNestedScrollingEnabled(false);
        rlContacts.setNestedScrollingEnabled(false);
        Utilz.closeProgressDialog();
    }


    @Override
    public void inviteClicked(boolean ifBB, int pos, int status, boolean accept, Friend friend) {

        if (!ifBB && status == 0) {
            if (friend.getPhone_no() != null && !friend.getPhone_no().isEmpty()) {
                Intent sendIntent = new Intent(Intent.ACTION_VIEW);
                sendIntent.setData(Uri.parse("smsto:" + friend.getPhone_no()));
                sendIntent.putExtra("sms_body", Constants.INVITE_TEXT);
                startActivity(sendIntent);
            } else {
                Intent intent = new Intent(Intent.ACTION_INSERT);
                intent.setType(ContactsContract.Contacts.CONTENT_TYPE);

                intent.putExtra(ContactsContract.Intents.Insert.NAME, friend.getName());
                intent.putExtra(ContactsContract.Intents.Insert.PHONE, "");
                intent.putExtra(ContactsContract.Intents.Insert.EMAIL, friend.getEmail());

                startActivity(intent);
            }
        } else if (status == 0) {
            if (!Utilz.isNetworkAvailable(AddContactActivity.this)) {
                Utilz.showMessage2(AddContactActivity.this, getString(R.string.network_error));
                return;
            } else {
                manageRequest(friend, 1);
            }
        } else if (status == 1) {
            if (!Utilz.isNetworkAvailable(AddContactActivity.this)) {
                Utilz.showMessage2(AddContactActivity.this, getString(R.string.network_error));
                return;
            } else {
                if (accept) {
                    //            call accept_friend_request service to send request
                    manageRequest(friend, 2);
                } else {
                    //            call reject_friend_request service to send request
                    manageRequest(friend, 3);
                }
            }
        }
    }

    private void manageRequest(Friend friend, int service) {

        Utilz.showProgressDialog(AddContactActivity.this);
        AsyncHttpClient client = new AsyncHttpClient();
        JSONObject objMain = new JSONObject();

        String friend_user_id = "";
        String user_id = "";
        if (service == 1) {
            friend_user_id = friend.getFriend_user_id();
            user_id = Utilz.getUserDetails(AddContactActivity.this).get(Constants.MM_user_id);
        } else if (service == 2 || service == 3) {
            friend_user_id = Utilz.getUserDetails(AddContactActivity.this).get(Constants.MM_user_id);
            user_id = friend.getUser_id();
        }

        String serviceName = "";
        if (service == 1)
            serviceName = Constants.API_send_friend_request;
        else if (service == 2)
            serviceName = Constants.API_accept_friend_request;
        else if (service == 3)
            serviceName = Constants.API_reject_friend_request;

        try {
            objMain.put(Constants.MM_user_id, user_id);
            objMain.put(Constants.MM_friend_user_id, friend_user_id);

            StringEntity entity = new StringEntity(objMain.toString());
            client.post(AddContactActivity.this, Constants.Base_url + serviceName, entity, "application/json", new RequestOperationHandler());

        } catch (Exception e) {
            e.printStackTrace();
            Utilz.closeProgressDialog();
        }
    }


    private class RequestOperationHandler extends AsyncHttpResponseHandler {

        @Override
        public void onSuccess(int statusCode, Header[] headers, byte[] responseBody) {
            Utilz.closeProgressDialog();
            JSONObject response = null;
            try {
                response = new JSONObject(new String(responseBody));
                Log.i("response.) ", response.toString());
                if (response.has(Constants.MM_STATUS_CODE) && response.getString(Constants.MM_STATUS_CODE).equalsIgnoreCase(Constants.MM_STATUS_CODE_SUCCESS)) {
                    AlertDialog.Builder builder1 = new AlertDialog.Builder(AddContactActivity.this);
                    builder1.setMessage(response.get(Constants.MM_msg).toString());
                    builder1.setCancelable(false);

                    builder1.setPositiveButton(
                            android.R.string.ok,
                            new DialogInterface.OnClickListener() {
                                public void onClick(DialogInterface dialog, int id) {
                                    dialog.cancel();
                                    prepareContacts(false);
                                }
                            });

                    AlertDialog alert11 = builder1.create();
                    alert11.show();

                } else {
                    Utilz.showMessage2(AddContactActivity.this, response.get(Constants.MM_msg).toString());
                }

            } catch (Exception ex) {
                ex.printStackTrace();
            }
        }

        @Override
        public void onFailure(int statusCode, Header[] headers, byte[] responseBody, Throwable error) {
            Utilz.closeProgressDialog();
            Toast.makeText(AddContactActivity.this, getString(R.string.server_error), Toast.LENGTH_SHORT).show();
        }
    }


}
