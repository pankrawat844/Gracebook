package com.zocia.book;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;

import androidx.recyclerview.widget.ItemTouchHelper;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.zocia.book.adapter.NotificaitonListAdapter;
import com.zocia.book.callbackinterface.ServerResponse;
import com.zocia.book.customview.VerticalSpaceItemDecoration;
import com.zocia.book.model.NotificationList;
import com.zocia.book.myapplication.Myapplication;
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

public class NotificaionActivity extends BaseActivity {
    private static final String TAG = NotificaionActivity.class.getSimpleName();
    private final int VERTICAL_ITEM_SPACE = 0;
    private Context mContext;
    private RecyclerView recycler_feed;
    private NotificaitonListAdapter mNotificaitonListAdapter;
    private BusyDialog mBusyDialog;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        getLayoutInflater().inflate(R.layout.activity_notification, frameLayout);
        Myapplication.selection = 3;
        mContext = this;
        selectedDeselectedLayut();
        initUi();
    }

    private void initUi() {
        recycler_feed = (RecyclerView) this.findViewById(R.id.recycler_feed);
        mNotificaitonListAdapter = new NotificaitonListAdapter(mContext, new ArrayList<NotificationList>());
        LinearLayoutManager mLinearLayoutManager = new LinearLayoutManager(mContext, RecyclerView.VERTICAL, false);
        recycler_feed.setLayoutManager(mLinearLayoutManager);
        recycler_feed.addItemDecoration(new VerticalSpaceItemDecoration(VERTICAL_ITEM_SPACE));
        recycler_feed.setAdapter(mNotificaitonListAdapter);
        ItemTouchHelper itemTouchHelper = new ItemTouchHelper(simpleItemTouchCallback);
        itemTouchHelper.attachToRecyclerView(recycler_feed);
        ServerRequest("0");

    }

    ItemTouchHelper.SimpleCallback simpleItemTouchCallback = new ItemTouchHelper.SimpleCallback(0, ItemTouchHelper.LEFT | ItemTouchHelper.RIGHT | ItemTouchHelper.DOWN | ItemTouchHelper.UP) {

        @Override
        public boolean onMove(RecyclerView recyclerView, RecyclerView.ViewHolder viewHolder, RecyclerView.ViewHolder target) {
            return false;
        }

        @Override
        public void onSwiped(RecyclerView.ViewHolder viewHolder, int swipeDir) {
            int position = viewHolder.getAdapterPosition();
            removeServerRequest(position);
            //arrayList.remove(position);
            //adapter.notifyDataSetChanged();
        }
    };

    private void ServerRequest(final String limit) {
        if (!Helpers.isNetworkAvailable(mContext)) {
            Helpers.showOkayDialog(mContext, R.string.no_internet_connection);
            return;
        }
        mBusyDialog = new BusyDialog(mContext);
        mBusyDialog.show();

        HashMap<String, String> allHashMap = new HashMap<>();
        allHashMap.put("limit", limit);
        HashMap<String, String> allHashMapHeader = new HashMap<>();
        allHashMapHeader.put("appKey", AllUrls.APP_KEY);
        allHashMapHeader.put("authToken", PersistentUser.getUserToken(mContext));
        final String url = AllUrls.BASEURL + "mynotification";
        ServerCallsProvider.volleyPostRequest(url, allHashMap, allHashMapHeader, TAG, new ServerResponse() {
            @Override
            public void onSuccess(String statusCode, String responseServer) {
                try {
                    mBusyDialog.dismis();
                    Logger.debugLog("responseServer", responseServer);
                    JSONObject mJsonObject = new JSONObject(responseServer);
                    if (mJsonObject.getBoolean("success")) {
                        JSONArray jsonArray = mJsonObject.getJSONArray("data");
                        GsonBuilder builder = new GsonBuilder();
                        Gson mGson = builder.create();
                        List<NotificationList> postsBannerList = new ArrayList<NotificationList>();
                        postsBannerList = Arrays.asList(mGson.fromJson(jsonArray.toString(), NotificationList[].class));
                        ArrayList<NotificationList> allLists = new ArrayList<NotificationList>(postsBannerList);
                        mNotificaitonListAdapter.addAllList(allLists);

                    }
                } catch (Exception e) {
                    Logger.debugLog("Exception", e.getMessage());

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

    private void removeServerRequest(final int post_id) {
        if (!Helpers.isNetworkAvailable(mContext)) {
            Helpers.showOkayDialog(mContext, R.string.no_internet_connection);
            return;
        }
        mBusyDialog = new BusyDialog(mContext);
        mBusyDialog.show();

        NotificationList mNotificationList = mNotificaitonListAdapter.getModelAt(post_id);

        HashMap<String, String> allHashMap = new HashMap<>();
        allHashMap.put("notification_id", mNotificationList.getId());

        HashMap<String, String> allHashMapHeader = new HashMap<>();
        allHashMapHeader.put("appKey", AllUrls.APP_KEY);
        allHashMapHeader.put("authToken", PersistentUser.getUserToken(mContext));
        final String url = AllUrls.BASEURL + "removenotification";
        ServerCallsProvider.volleyPostRequest(url, allHashMap, allHashMapHeader, TAG, new ServerResponse() {
            @Override
            public void onSuccess(String statusCode, String responseServer) {
                try {
                    mBusyDialog.dismis();

                    Logger.debugLog("responseServer", responseServer);
                    JSONObject mJsonObject = new JSONObject(responseServer);
                    if (mJsonObject.getBoolean("success")) {
                        mNotificaitonListAdapter.removeaddList(post_id);

                    }
                } catch (Exception e) {
                    Logger.debugLog("Exception", e.getMessage());

                }
            }

            @Override
            public void onFailed(String statusCode, String serverResponse) {
                mBusyDialog.dismis();
                if (statusCode.equalsIgnoreCase("404")) {
                    PersistentUser.resetAllData(mContext);
                    Intent intent = new Intent(mContext, LoginActivity.class);
                    intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP | Intent.FLAG_ACTIVITY_NEW_TASK);
                    mContext.startActivity(intent);
                }
            }
        });
    }

}
