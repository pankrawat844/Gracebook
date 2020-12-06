package com.zocia.book;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.TextView;

import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import androidx.swiperefreshlayout.widget.SwipeRefreshLayout;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.zocia.book.adapter.GroupAddfreindListAdapter;
import com.zocia.book.callbackinterface.FilterItemCallback;
import com.zocia.book.callbackinterface.ServerResponse;
import com.zocia.book.customview.VerticalSpaceItemDecoration;
import com.zocia.book.model.FriendList;
import com.zocia.book.model.GroupList;
import com.zocia.book.model.Usersdata;
import com.zocia.book.networkcalls.ServerCallsProvider;
import com.zocia.book.utils.AllUrls;
import com.zocia.book.utils.BusyDialog;
import com.zocia.book.utils.DateUtility;
import com.zocia.book.utils.Helpers;
import com.zocia.book.utils.Logger;
import com.zocia.book.utils.PersistentUser;
import com.zocia.book.utils.ToastHelper;

import org.json.JSONArray;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;

public class AddGroupFriendActivity extends BaseActivity {
    private static final String TAG = AddGroupFriendActivity.class.getSimpleName();
    private final int VERTICAL_ITEM_SPACE = 20;
    private GroupAddfreindListAdapter mGroupAddfreindListAdapter;
    private RecyclerView recycler_feed;
    private Context mContext;
    private BusyDialog mBusyDialog;
    private GroupList mGroupList;
    private TextView chatuser;
    private SwipeRefreshLayout swipeRefreshLayout;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_group_friend);
        Bundle extra = getIntent().getBundleExtra("extra");
        mGroupList = (GroupList) extra.getSerializable("objects");
        mContext = this;
        initUi();
    }

    private void initUi() {
        findViewById(R.id.layoutBack).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                finish();
            }
        });
        swipeRefreshLayout = (SwipeRefreshLayout) findViewById(R.id.swipe_refresh_layout);
        recycler_feed = (RecyclerView) this.findViewById(R.id.recycler_feed_group);
        mGroupAddfreindListAdapter = new GroupAddfreindListAdapter(mContext, new ArrayList<FriendList>());
        LinearLayoutManager mLinearLayoutManager = new LinearLayoutManager(mContext, RecyclerView.VERTICAL, false);
        recycler_feed.setLayoutManager(mLinearLayoutManager);
        recycler_feed.addItemDecoration(new VerticalSpaceItemDecoration(VERTICAL_ITEM_SPACE));
        recycler_feed.setAdapter(mGroupAddfreindListAdapter);
        mGroupAddfreindListAdapter.addFilterItemCallback(lFilterItemCallback);
        chatuser = (TextView) this.findViewById(R.id.chatuser);
        chatuser.setText(mGroupList.getGroup_name());
        serverRequest();
        swipeRefreshLayout.setOnRefreshListener(new SwipeRefreshLayout.OnRefreshListener() {
            @Override
            public void onRefresh() {
                swipeRefreshLayout.setRefreshing(false);
                serverRequest();
            }
        });

    }

    public FilterItemCallback lFilterItemCallback = new FilterItemCallback() {
        @Override
        public void ClickFilterItemCallback(int type, int position) {
            Usersdata mUsersdata = mGroupAddfreindListAdapter.getModelAt(position).getmUsersdata();
            if (type == 0) {
                Intent mIntent = new Intent(mContext, UserprofileActivity.class);
                Bundle extra = new Bundle();
                extra.putSerializable("objects", mUsersdata);
                mIntent.putExtra("extra", extra);
                startActivity(mIntent);

            } else {
                HashMap<String, String> allHashMap = new HashMap<>();
                allHashMap.put("user_id", mUsersdata.getId());
                allHashMap.put("group_id", mGroupList.getGroup_id());
                allHashMap.put("duration", DateUtility.getCurrentTimeForsend());

                addFriendserverRequest(allHashMap, position);
            }
        }
    };

    private void addFriendserverRequest(HashMap<String, String> allHashMap, final int position) {
        if (!Helpers.isNetworkAvailable(mContext)) {
            Helpers.showOkayDialog(mContext, R.string.no_internet_connection);
            return;
        }
        Logger.debugLog("allHashMap", allHashMap.toString());
        HashMap<String, String> allHashMapHeader = new HashMap<>();
        allHashMapHeader.put("appKey", AllUrls.APP_KEY);
        allHashMapHeader.put("authToken", PersistentUser.getUserToken(mContext));
        mBusyDialog = new BusyDialog(mContext);
        mBusyDialog.show();
        final String url = AllUrls.BASEURL + "groupmemebradd";
        ServerCallsProvider.volleyPostRequest(url, allHashMap, allHashMapHeader, TAG, new ServerResponse() {
            @Override
            public void onSuccess(String statusCode, String responseServer) {
                mBusyDialog.dismis();
                try {
                    Logger.debugLog("responseServer", responseServer);
                    JSONObject mJsonObject = new JSONObject(responseServer);
                    if (mJsonObject.getBoolean("success")) {
                        ToastHelper.showToast(mContext, "Add  group successfully");
                        mGroupAddfreindListAdapter.deletePostion(position);
                        mGroupAddfreindListAdapter.notifyDataSetChanged();

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

    private void serverRequest() {
        if (!Helpers.isNetworkAvailable(mContext)) {
            Helpers.showOkayDialog(mContext, R.string.no_internet_connection);
            return;
        }
        mGroupAddfreindListAdapter.removeAllData();
        HashMap<String, String> allHashMap = new HashMap<>();
        allHashMap.put("group_id", mGroupList.getGroup_id());
        allHashMap.put("duration", DateUtility.getCurrentTimeForsend());

        HashMap<String, String> allHashMapHeader = new HashMap<>();
        allHashMapHeader.put("appKey", AllUrls.APP_KEY);
        allHashMapHeader.put("authToken", PersistentUser.getUserToken(mContext));
        final String url = AllUrls.BASEURL + "groupFriend";
        ServerCallsProvider.volleyPostRequest(url, allHashMap, allHashMapHeader, TAG, new ServerResponse() {
            @Override
            public void onSuccess(String statusCode, String responseServer) {
                try {
                    Logger.debugLog("responseServer", responseServer);
                    JSONObject mJsonObject = new JSONObject(responseServer);
                    if (mJsonObject.getBoolean("success")) {
                        JSONArray jsonArray = mJsonObject.getJSONArray("data");
                        GsonBuilder builder = new GsonBuilder();
                        Gson mGson = builder.create();
                        List<FriendList> posts = new ArrayList<FriendList>();
                        posts = Arrays.asList(mGson.fromJson(jsonArray.toString(), FriendList[].class));
                        ArrayList<FriendList> allLists = new ArrayList<FriendList>(posts);
                        mGroupAddfreindListAdapter.addAllList(allLists);


                    }

                } catch (Exception e) {

                }
            }

            @Override
            public void onFailed(String statusCode, String serverResponse) {
                if (statusCode.equalsIgnoreCase("404")) {
                    PersistentUser.resetAllData(mContext);
                    Intent intent = new Intent(mContext, LoginActivity.class);
                    intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP | Intent.FLAG_ACTIVITY_NEW_TASK);
                    startActivity(intent);
                    finish();
                }
                Logger.debugLog("onFailed serverResponse", serverResponse);
            }
        });
    }
}
