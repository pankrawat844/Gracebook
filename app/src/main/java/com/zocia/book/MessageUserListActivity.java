package com.zocia.book;

import android.app.Dialog;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.LinearLayout;
import android.widget.TextView;

import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.android.volley.AuthFailureError;
import com.android.volley.DefaultRetryPolicy;
import com.android.volley.Request;
import com.android.volley.Response;
import com.android.volley.RetryPolicy;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.StringRequest;
import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.zocia.book.adapter.MessageFriendListAdapter;
import com.zocia.book.callbackinterface.FilterItemCallback;
import com.zocia.book.callbackinterface.ServerResponse;
import com.zocia.book.customview.VerticalSpaceItemDecoration;
import com.zocia.book.model.Usersdata;
import com.zocia.book.myapplication.Myapplication;
import com.zocia.book.networkcalls.ServerCallsProvider;
import com.zocia.book.utils.AllUrls;
import com.zocia.book.utils.BusyDialog;
import com.zocia.book.utils.Helpers;
import com.zocia.book.utils.Logger;
import com.zocia.book.utils.PersistentUser;
import com.zocia.book.utils.ToastHelper;

import org.chat21.android.ui.ChatUI;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class MessageUserListActivity extends BaseActivity {
    private static final String TAG = MessageUserListActivity.class.getSimpleName();
    private final int VERTICAL_ITEM_SPACE = 20;
    Usersdata mUsersdata;
    Dialog dialog;
    private MessageFriendListAdapter mMessageFriendListAdapter;
    private RecyclerView recycler_feed;
    private Context mContext;
    public FilterItemCallback lFilterItemCallback = new FilterItemCallback() {
        @Override
        public void ClickFilterItemCallback(int type, int position) {
            mUsersdata = mMessageFriendListAdapter.getModelAt(position);
            if (mUsersdata.isIsfriend()) {

//                JSONObject mJsonObject = null;
//                try {
//                    mJsonObject = new JSONObject(PersistentUser.getUserDetails(mContext));
//
//                if (mJsonObject.getBoolean("success")) {
//
//                    JSONObject data = mJsonObject.getJSONObject("data");
//                    String email = data.getString("email");
//                    serverEmailCheckRequest(email);
//
//
//                }
//                } catch (JSONException e) {
//                    e.printStackTrace();
//                }
                Intent mIntent = new Intent(mContext, ChatActivity.class);
                Bundle extra = new Bundle();
                extra.putSerializable("objects", mUsersdata);
                mIntent.putExtra("extra", extra);
                startActivity(mIntent);
            } else {
                ToastHelper.showToast(mContext, "Sorry you are not allow for send message");
                return;

            }

        }
    };
    private BusyDialog mBusyDialog;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        getLayoutInflater().inflate(R.layout.activity_chatsuser, frameLayout);
        Myapplication.selection = 4;
        selectedDeselectedLayut();
        mContext = this;
//        initUi();
        titile.setText("Chat");

        ChatUI.getInstance().openConversationsListFragment(getSupportFragmentManager(), R.id.container);
//        ChatUI.getInstance().openConversationMessagesActivity();
    }

    private void initUi() {
        recycler_feed = (RecyclerView) this.findViewById(R.id.recycler_feed);
        mMessageFriendListAdapter = new MessageFriendListAdapter(mContext, new ArrayList<Usersdata>());
        LinearLayoutManager mLinearLayoutManager = new LinearLayoutManager(mContext, RecyclerView.VERTICAL, false);
        recycler_feed.setLayoutManager(mLinearLayoutManager);
        recycler_feed.addItemDecoration(new VerticalSpaceItemDecoration(VERTICAL_ITEM_SPACE));
        recycler_feed.setAdapter(mMessageFriendListAdapter);
        mMessageFriendListAdapter.addFilterItemCallback(lFilterItemCallback);


        LinearLayout add_new_group = (LinearLayout) this.findViewById(R.id.add_new_group);
        add_new_group.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(mContext, FriendsActivity.class);
                intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP | Intent.FLAG_ACTIVITY_NEW_TASK);
                startActivity(intent);
                finish();
            }
        });
        serverRequest();
    }

    private void serverEmailCheckRequest(final String email) {
        if (!Helpers.isNetworkAvailable(this)) {
            Helpers.showOkayDialog(this, R.string.no_internet_connection);
            return;
        }
        mBusyDialog = new BusyDialog(mContext);
        mBusyDialog.show();
        HashMap<String, String> allHashMap = new HashMap<>();
        HashMap<String, String> allHashMapHeader = new HashMap<>();
        allHashMapHeader.put("appKey", AllUrls.APP_KEY);
        allHashMapHeader.put("authToken", PersistentUser.getUserToken(this));
        allHashMap.put("id", PersistentUser.getUserID(this));
        allHashMap.put("email", email);
        final String url = AllUrls.BASEURL + "emailverification";
        ServerCallsProvider.volleyPostRequest(url, allHashMap, allHashMapHeader, TAG, new ServerResponse() {
            @Override
            public void onSuccess(String statusCode, String responseServer) {
                try {

                    mBusyDialog.dismis();
                    JSONObject jsonObject = new JSONObject(responseServer);
                    if (jsonObject.getBoolean("success")) {
                        PersistentUser.setUserEmail(MessageUserListActivity.this, email);
                        Intent mIntent = new Intent(mContext, ChatActivity.class);
                        Bundle extra = new Bundle();
                        extra.putSerializable("objects", mUsersdata);
                        mIntent.putExtra("extra", extra);
                        startActivity(mIntent);
                    } else {
                        dialog = new Dialog(MessageUserListActivity.this, android.R.style.Theme_Light_NoTitleBar_Fullscreen);
                        dialog.setContentView(R.layout.dialog_email_verification);
                        final TextView emailtxt = dialog.findViewById(R.id.email);
                        emailtxt.setText(email);
                        TextView btn = dialog.findViewById(R.id.submit);
                        btn.setOnClickListener(new View.OnClickListener() {
                            @Override
                            public void onClick(View view) {
                                serversendverifyEmailRequest(emailtxt.getText().toString());

                            }
                        });
                        dialog.show();
                    }

                } catch (Exception e) {
                    mBusyDialog.dismis();
                }
            }

            @Override
            public void onFailed(String statusCode, String serverResponse) {
                mBusyDialog.dismis();
                if (statusCode.equalsIgnoreCase("500")) {
                    Intent mIntent = new Intent(mContext, ChatActivity.class);
                    Bundle extra = new Bundle();
                    extra.putSerializable("objects", mUsersdata);
                    mIntent.putExtra("extra", extra);
                    startActivity(mIntent);
                }
                Logger.debugLog("onFailed serverResponse", serverResponse);
            }
        });
    }

    private void serversendverifyEmailRequest(final String email) {
        if (!Helpers.isNetworkAvailable(mContext)) {
            Helpers.showOkayDialog(mContext, R.string.no_internet_connection);
            return;
        }
        JSONObject allHashMap = new JSONObject();
        try {
            allHashMap.put("email", email);

            mBusyDialog = new BusyDialog(mContext);
            mBusyDialog.show();
            HashMap<String, String> allHashMapHeader = new HashMap<>();
//        allHashMapHeader.put("appKey", AllUrls.APP_KEY);
//        allHashMapHeader.put("authToken", PersistentUser.getUserToken(mContext));
//        mBusyDialog = new BusyDialog(mContext);
//        mBusyDialog.show();
            final String url = "https://gracebookzone.com/gracebook_php/mail/sendmail.php";

            StringRequest postRequest = new StringRequest(Request.Method.POST,
                    url,
                    new Response.Listener<String>() {
                        @Override
                        public void onResponse(String response) {
                            String body = response.toString();
                            String statusCode = "200";
                            mBusyDialog.dismis();
                            try {
                                JSONObject jsonObject = new JSONObject(response);
                                if (jsonObject.getBoolean("success")) {
                                    if (dialog != null)
                                        dialog.dismiss();
                                    ToastHelper.showToast(MessageUserListActivity.this, jsonObject.getString("message"));
                                } else {
                                    ToastHelper.showToast(MessageUserListActivity.this, jsonObject.getString("message"));

                                }
                            } catch (Exception e) {
                                e.printStackTrace();
                            }
                        }
                    }, new Response.ErrorListener() {

                @Override
                public void onErrorResponse(VolleyError error) {
                    mBusyDialog.dismis();
                    String body = "";
                    String statusCode = "-1";
                    Logger.warnLog("VolleyError", "message #### " + error.getMessage());
                    ToastHelper.showToast(MessageUserListActivity.this, error.getLocalizedMessage());
                    try {
                        if (error.networkResponse.data != null) {
                            statusCode = String.valueOf(error.networkResponse.statusCode);
                            body = new String(error.networkResponse.data, "UTF-8");

                        } else {
                            JSONObject mObject = new JSONObject();
                            mObject.put("msg", "Network issue.Please checked your mobile data.");
                        }


                    } catch (Exception e) {
                        e.printStackTrace();
                    }
                }
            }) {
                @Override
                protected Map<String, String> getParams() throws AuthFailureError {
                    HashMap<String, String> hashMap = new HashMap<>();
                    hashMap.put("email", email);
                    return hashMap;
                }
            };

            Logger.debugLog(TAG, "volleyPostRequest  " + postRequest.getUrl());
            int socketTimeout = 50000;// 30 seconds - change to what you want
            RetryPolicy policy = new DefaultRetryPolicy(socketTimeout,
                    DefaultRetryPolicy.DEFAULT_MAX_RETRIES,
                    DefaultRetryPolicy.DEFAULT_BACKOFF_MULT);
            postRequest.setRetryPolicy(policy);
            Myapplication.getInstance().addToRequestQueue(postRequest, TAG);

        } catch (JSONException e) {
            e.printStackTrace();
        }


//        ServerCallsProvider.volleyPostRequest(url, allHashMap, allHashMapHeader, TAG, new ServerResponse() {
//            @Override
//            public void onSuccess(String statusCode, String responseServer) {
////                mBusyDialog.dismis();
//                try {
//                    Logger.debugLog("responseServer", responseServer);
//                    JSONObject mJsonObject = new JSONObject(responseServer);
//                    if (mJsonObject.getBoolean("success")) {
//
//                        JSONArray jsonArray = mJsonObject.getJSONArray("data");
//                        GsonBuilder builder = new GsonBuilder();
//                        Gson mGson = builder.create();
//                        List<Usersdata> posts = new ArrayList<Usersdata>();
//                        posts = Arrays.asList(mGson.fromJson(jsonArray.toString(), Usersdata[].class));
//                        ArrayList<Usersdata> allLists = new ArrayList<Usersdata>(posts);
//                        mMessageFriendListAdapter.addModelAt(allLists);
//
//                    } else {
//                        //String message = mJsonObject.getString("message");
//                        // ToastHelper.showToast(mContext, message);
//
//                    }
//                } catch (Exception e) {
//
//                }
//            }
//
//            @Override
//            public void onFailed(String statusCode, String serverResponse) {
////                mBusyDialog.dismis();
//                if (statusCode.equalsIgnoreCase("404")) {
//                    PersistentUser.resetAllData(mContext);
//                    Intent intent = new Intent(mContext, LoginActivity.class);
//                    intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP | Intent.FLAG_ACTIVITY_NEW_TASK);
//                    startActivity(intent);
//                    finish();
//                }
//            }
//        });
    }

    private void serverRequest() {
        if (!Helpers.isNetworkAvailable(mContext)) {
            Helpers.showOkayDialog(mContext, R.string.no_internet_connection);
            return;
        }
        HashMap<String, String> allHashMap = new HashMap<>();
        allHashMap.put("limit", "0");

        HashMap<String, String> allHashMapHeader = new HashMap<>();
        allHashMapHeader.put("appKey", AllUrls.APP_KEY);
        allHashMapHeader.put("authToken", PersistentUser.getUserToken(mContext));
        mBusyDialog = new BusyDialog(mContext);
        mBusyDialog.show();
        final String url = AllUrls.BASEURL + "mymessageuserlist";
        ServerCallsProvider.volleyPostRequest(url, allHashMap, allHashMapHeader, TAG, new ServerResponse() {
            @Override
            public void onSuccess(String statusCode, String responseServer) {
                mBusyDialog.dismis();
                try {
                    Logger.debugLog("responseServer", responseServer);
                    JSONObject mJsonObject = new JSONObject(responseServer);
                    if (mJsonObject.getBoolean("success")) {

                        JSONArray jsonArray = mJsonObject.getJSONArray("data");
                        GsonBuilder builder = new GsonBuilder();
                        Gson mGson = builder.create();
                        List<Usersdata> posts = new ArrayList<Usersdata>();
                        posts = Arrays.asList(mGson.fromJson(jsonArray.toString(), Usersdata[].class));
                        ArrayList<Usersdata> allLists = new ArrayList<Usersdata>(posts);
                        mMessageFriendListAdapter.addModelAt(allLists);

                    } else {
                        //String message = mJsonObject.getString("message");
                        // ToastHelper.showToast(mContext, message);

                    }
                } catch (Exception e) {

                }
            }

            @Override
            public void onFailed(String statusCode, String serverResponse) {
                mBusyDialog.dismis();
                if (statusCode.equalsIgnoreCase("404")) {
                    PersistentUser.resetAllData(mContext);
                    Intent intent = new Intent(mContext, LoginActivity.class);
                    intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP | Intent.FLAG_ACTIVITY_NEW_TASK);
                    startActivity(intent);
                    finish();
                }
            }
        });
    }

}
