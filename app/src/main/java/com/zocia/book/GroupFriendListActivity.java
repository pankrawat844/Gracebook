package com.zocia.book;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.TextView;

import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import androidx.swiperefreshlayout.widget.SwipeRefreshLayout;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.zocia.book.adapter.GroupFreindListAdapter;
import com.zocia.book.callbackinterface.FilterItemCallback;
import com.zocia.book.callbackinterface.ServerResponse;
import com.zocia.book.customview.VerticalSpaceItemDecoration;
import com.zocia.book.model.GroupFriendList;
import com.zocia.book.model.GroupList;
import com.zocia.book.model.Usersdata;
import com.zocia.book.networkcalls.ServerCallsProvider;
import com.zocia.book.utils.AllUrls;
import com.zocia.book.utils.BusyDialog;
import com.zocia.book.utils.Helpers;
import com.zocia.book.utils.Logger;
import com.zocia.book.utils.PersistentUser;

import org.json.JSONArray;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;

public class GroupFriendListActivity extends BaseActivity {
    private static final String TAG = GroupFriendListActivity.class.getSimpleName();
    private final int VERTICAL_ITEM_SPACE = 20;
    private GroupFreindListAdapter mGroupFreindListAdapter;
    private RecyclerView recycler_feed;
    private Context mContext;
    private BusyDialog mBusyDialog;
    private GroupList mGroupList;
    private SwipeRefreshLayout swipeRefreshLayout;
    private TextView chatuser;

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
        recycler_feed = (RecyclerView) this.findViewById(R.id.recycler_feed_group);
        mGroupFreindListAdapter = new GroupFreindListAdapter(mContext, new ArrayList<GroupFriendList>());
        LinearLayoutManager mLinearLayoutManager = new LinearLayoutManager(mContext, RecyclerView.VERTICAL, false);
        recycler_feed.setLayoutManager(mLinearLayoutManager);
        recycler_feed.addItemDecoration(new VerticalSpaceItemDecoration(VERTICAL_ITEM_SPACE));
        recycler_feed.setAdapter(mGroupFreindListAdapter);
        chatuser = (TextView) this.findViewById(R.id.chatuser);
        mGroupFreindListAdapter.addFilterItemCallback(lFilterItemCallback);
        swipeRefreshLayout = (SwipeRefreshLayout) findViewById(R.id.swipe_refresh_layout);

        swipeRefreshLayout.setOnRefreshListener(new SwipeRefreshLayout.OnRefreshListener() {
            @Override
            public void onRefresh() {
                swipeRefreshLayout.setRefreshing(false);
                serverRequest();
            }
        });
        chatuser.setText(mGroupList.getGroup_name());

        serverRequest();

    }

    public FilterItemCallback lFilterItemCallback = new FilterItemCallback() {
        @Override
        public void ClickFilterItemCallback(int type, int position) {
            Usersdata mUsersdata = mGroupFreindListAdapter.getModelAt(position).getmUsersdata();
            Intent mIntent = new Intent(mContext, UserprofileActivity.class);
            Bundle extra = new Bundle();
            extra.putSerializable("objects", mUsersdata);
            mIntent.putExtra("extra", extra);
            startActivity(mIntent);
        }
    };

    private void serverRequest() {
        if (!Helpers.isNetworkAvailable(mContext)) {
            Helpers.showOkayDialog(mContext, R.string.no_internet_connection);
            return;
        }
        mGroupFreindListAdapter.removeAllData();
        HashMap<String, String> allHashMap = new HashMap<>();
        allHashMap.put("group_id", mGroupList.getGroup_id());

        mBusyDialog = new BusyDialog(mContext);
        mBusyDialog.show();
        HashMap<String, String> allHashMapHeader = new HashMap<>();
        allHashMapHeader.put("appKey", AllUrls.APP_KEY);
        allHashMapHeader.put("authToken", PersistentUser.getUserToken(mContext));
        final String url = AllUrls.BASEURL + "groupmemebrList";
        ServerCallsProvider.volleyPostRequest(url, allHashMap, allHashMapHeader, TAG, new ServerResponse() {
            @Override
            public void onSuccess(String statusCode, String responseServer) {
                try {
                    mBusyDialog.dismis();
                    JSONObject mJsonObject = new JSONObject(responseServer);
                    if (mJsonObject.getBoolean("success")) {
                        mGroupFreindListAdapter.addGroupList(mGroupList);

                        JSONArray jsonArray = mJsonObject.getJSONArray("data");
                        GsonBuilder builder = new GsonBuilder();
                        Gson mGson = builder.create();
                        List<GroupFriendList> posts = new ArrayList<GroupFriendList>();
                        posts = Arrays.asList(mGson.fromJson(jsonArray.toString(), GroupFriendList[].class));
                        ArrayList<GroupFriendList> allLists = new ArrayList<GroupFriendList>(posts);
                        mGroupFreindListAdapter.addAllList(allLists);

                        Logger.debugLog("responseServer", "are" + allLists.size());

                    }

                } catch (Exception e) {
                    Log.e("Exception", e.getMessage());

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
                Logger.debugLog("onFailed serverResponse", serverResponse);
            }
        });
    }
}
