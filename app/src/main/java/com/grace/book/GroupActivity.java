package com.grace.book;

import android.app.Notification;
import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.graphics.drawable.ColorDrawable;
import android.os.Build;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.TextView;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AlertDialog;
import androidx.core.app.NotificationCompat;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.grace.book.adapter.GroupListAdapter;
import com.grace.book.callbackinterface.FilterItemCallback;
import com.grace.book.callbackinterface.ServerResponse;
import com.grace.book.customview.VerticalSpaceItemDecoration;
import com.grace.book.model.GroupList;
import com.grace.book.myapplication.Myapplication;
import com.grace.book.networkcalls.ServerCallsProvider;
import com.grace.book.utils.AllUrls;
import com.grace.book.utils.BusyDialog;
import com.grace.book.utils.Helpers;
import com.grace.book.utils.Logger;
import com.grace.book.utils.PersistentUser;
import com.grace.book.utils.ToastHelper;

import org.json.JSONArray;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;

public class GroupActivity extends BaseActivity {
    private static final String TAG = GroupActivity.class.getSimpleName();
    private final int VERTICAL_ITEM_SPACE = 0;
    private GroupListAdapter mGroupListAdapter;
    private RecyclerView recycler_feed;
    private Context mContext;
    private LinearLayout add_new_group;
    private BusyDialog mBusyDialog;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        getLayoutInflater().inflate(R.layout.activity_group, frameLayout);
        Myapplication.selection = 5;
        selectedDeselectedLayut();
        mContext = this;
        initUi();

    }

    private void initUi() {
        recycler_feed = (RecyclerView) this.findViewById(R.id.recycler_feed);
        mGroupListAdapter = new GroupListAdapter(mContext, new ArrayList<GroupList>());
        LinearLayoutManager mLinearLayoutManager = new LinearLayoutManager(mContext, RecyclerView.VERTICAL, false);
        recycler_feed.setLayoutManager(mLinearLayoutManager);
        recycler_feed.addItemDecoration(new VerticalSpaceItemDecoration(VERTICAL_ITEM_SPACE));
        recycler_feed.setAdapter(mGroupListAdapter);
        add_new_group = (LinearLayout) this.findViewById(R.id.add_new_group);
        add_new_group.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent mIntent = new Intent(GroupActivity.this, GroupcreateActivity.class);
                Bundle extra = new Bundle();
                mIntent.putExtra("extra", extra);
                startActivityForResult(mIntent, 1011);
            }
        });

        mGroupListAdapter.addFilterItemCallback(lFilterItemCallback);
        groupList();

    }

    public FilterItemCallback lFilterItemCallback = new FilterItemCallback() {
        @Override
        public void ClickFilterItemCallback(int type, int position) {

            GroupList mUsersdata = mGroupListAdapter.getModelAt(position);
            Intent mIntent = new Intent(GroupActivity.this, GroupdetailsActivity.class);
            Bundle extra = new Bundle();
            extra.putSerializable("objects", mUsersdata);
            mIntent.putExtra("extra", extra);
            startActivityForResult(mIntent, 1010);
        }
    };

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        groupList();
    }

    private void groupList() {
        if (!Helpers.isNetworkAvailable(mContext)) {
            Helpers.showOkayDialog(mContext, R.string.no_internet_connection);
            return;
        }
        HashMap<String, String> allHashMap = new HashMap<>();

        HashMap<String, String> allHashMapHeader = new HashMap<>();
        allHashMapHeader.put("appKey", AllUrls.APP_KEY);
        allHashMapHeader.put("authToken", PersistentUser.getUserToken(mContext));
        mBusyDialog = new BusyDialog(mContext);
        mBusyDialog.show();
        final String url = AllUrls.BASEURL + "groupList";
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
                        List<GroupList> posts = new ArrayList<GroupList>();
                        posts = Arrays.asList(mGson.fromJson(jsonArray.toString(), GroupList[].class));
                        ArrayList<GroupList> allLists = new ArrayList<GroupList>(posts);
                        mGroupListAdapter.addallList(allLists);


                    } else {
                        String message = mJsonObject.getString("message");
                        ToastHelper.showToast(mContext, message);

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
