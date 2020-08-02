package com.grace.book;

import android.app.AlertDialog;
import android.content.Context;
import android.content.Intent;
import android.graphics.drawable.ColorDrawable;
import android.os.Bundle;
import android.os.Handler;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.LinearLayout;
import android.widget.TextView;

import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import androidx.swiperefreshlayout.widget.SwipeRefreshLayout;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.grace.book.adapter.FeedListAdapter;
import com.grace.book.adapter.MyPostListAdapter;
import com.grace.book.callbackinterface.FilterItemCallback;
import com.grace.book.callbackinterface.ServerResponse;
import com.grace.book.customview.EndlessRecyclerViewScrollListener;
import com.grace.book.customview.VerticalSpaceItemDecoration;
import com.grace.book.model.BannerList;
import com.grace.book.model.FeedList;
import com.grace.book.networkcalls.ServerCallsProvider;
import com.grace.book.utils.AllUrls;
import com.grace.book.utils.BusyDialog;
import com.grace.book.utils.ConstantFunctions;
import com.grace.book.utils.DateUtility;
import com.grace.book.utils.Helpers;
import com.grace.book.utils.Logger;
import com.grace.book.utils.PersistentUser;
import com.grace.book.utils.ToastHelper;
import com.headerfooter.songhang.library.SmartRecyclerAdapter;

import org.json.JSONArray;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;

public class MyPostActivity extends AppCompatActivity {
    private static final String TAG = MyPostActivity.class.getSimpleName();
    private Context mContext;
    private LinearLayout layoutBack;
    private final int VERTICAL_ITEM_SPACE = 20;
    private RecyclerView recycler_feed;
    private MyPostListAdapter mMyPostListAdapter;
    private BusyDialog mBusyDialog;
    private SwipeRefreshLayout swipeRefreshLayout;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_my_post);
        mContext = this;
        initUi();
    }

    private void initUi() {
        layoutBack=(LinearLayout)this.findViewById(R.id.layoutBack);
        layoutBack.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                finish();
            }
        });
        recycler_feed = (RecyclerView) this.findViewById(R.id.recycler_feed);
        mMyPostListAdapter = new MyPostListAdapter(mContext, new ArrayList<FeedList>());
        LinearLayoutManager mLinearLayoutManager = new LinearLayoutManager(mContext, RecyclerView.VERTICAL, false);
        recycler_feed.setLayoutManager(mLinearLayoutManager);
        recycler_feed.addItemDecoration(new VerticalSpaceItemDecoration(VERTICAL_ITEM_SPACE));
        recycler_feed.setAdapter(mMyPostListAdapter);
        mMyPostListAdapter.addClickListiner(lFilterItemCallback);
        ServerRequest("0");

        swipeRefreshLayout = (SwipeRefreshLayout) findViewById(R.id.swipe_refresh_layout);
        swipeRefreshLayout.setOnRefreshListener(new SwipeRefreshLayout.OnRefreshListener() {
            @Override
            public void onRefresh() {
                mMyPostListAdapter.removeAllData();
                ServerRequest("0");
                swipeRefreshLayout.setRefreshing(false);
            }
        });
        recycler_feed.addOnScrollListener(new EndlessRecyclerViewScrollListener(mLinearLayoutManager) {
            @Override
            public void onLoadMore(int page, int totalItemsCount) {
                Log.e("totalItemsCount","are"+totalItemsCount);
                if(totalItemsCount>20){
                    int itemPages=totalItemsCount/20;
                    itemPages=itemPages+1;
                    ServerRequest(""+itemPages);
                }

            }
        });

    }
    public FilterItemCallback lFilterItemCallback = new FilterItemCallback() {
        @Override
        public void ClickFilterItemCallback(int type, int position) {
            FeedList mFeedList = mMyPostListAdapter.getModelAt(position);

            if (type == 0) {
                Intent mIntent = new Intent(mContext, UserprofileActivity.class);
                Bundle extra = new Bundle();
                extra.putSerializable("objects", mFeedList.getmUsersdata());
                mIntent.putExtra("extra", extra);
                startActivity(mIntent);

            } else if (type == 1) {

            } else if (type == 2) {
                Intent mIntent = new Intent(MyPostActivity.this, CommentActivity.class);
                Bundle extra = new Bundle();
                extra.putSerializable("objects", mFeedList);
                extra.putInt("screen", 1);
                mIntent.putExtra("extra", extra);
                startActivityForResult(mIntent, 102);

            } else if (type == 3) {
                String text = Logger.EmptyString(mFeedList.getDetails());
                if (mFeedList.getPost_type().equalsIgnoreCase("0")) {
                    ConstantFunctions.openIntentForShare(mContext, text);
                } else {
                    text = text + " \n\n" + mFeedList.getPost_path();
                    ConstantFunctions.openIntentForShare(mContext, text);

                }


            } else if (type == 4) {
                alertfornewuser(position);
            } else if (type == 5) {
                Intent mIntent = new Intent(MyPostActivity.this, VideoPlayertActivity.class);
                mIntent.putExtra("url", mFeedList.getPost_path());
                startActivity(mIntent);
            }
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
        allHashMap.put("user_id", PersistentUser.getUserID(mContext));

        HashMap<String, String> allHashMapHeader = new HashMap<>();
        allHashMapHeader.put("appKey", AllUrls.APP_KEY);
        allHashMapHeader.put("authToken", PersistentUser.getUserToken(mContext));
        final String url = AllUrls.BASEURL + "userPost";
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
                        List<FeedList> posts = new ArrayList<FeedList>();
                        posts = Arrays.asList(mGson.fromJson(jsonArray.toString(), FeedList[].class));
                        ArrayList<FeedList> allLists = new ArrayList<FeedList>(posts);
                        mMyPostListAdapter.addAllList(allLists);


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

    AlertDialog alertDialog = null;
    public void alertfornewuser(final int postion) {
        AlertDialog.Builder builder = new AlertDialog.Builder(MyPostActivity.this);
        LayoutInflater inflater = getLayoutInflater();
        View mView = inflater.inflate(R.layout.dialog_app_logout, null);
        builder.setView(mView);
        builder.setCancelable(false);
        alertDialog = builder.create();
        alertDialog.getWindow().setBackgroundDrawable(new ColorDrawable(android.graphics.Color.TRANSPARENT));
        alertDialog.show();

        TextView no_botton = (TextView) mView.findViewById(R.id.no_botton);
        TextView yes_btn = (TextView) mView.findViewById(R.id.yes_btn);
        TextView version_text = (TextView) mView.findViewById(R.id.version_text);
        TextView version_text2 = (TextView) mView.findViewById(R.id.version_text2);
        version_text.setText("Remove  Post");
        version_text2.setText("Are you sure you want remove this post");
        no_botton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                alertDialog.dismiss();

            }
        });
        yes_btn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                alertDialog.dismiss();
                leaverServerRequest(postion);
            }
        });
    }

    private void leaverServerRequest(final int position) {
        if (!Helpers.isNetworkAvailable(mContext)) {
            Helpers.showOkayDialog(mContext, R.string.no_internet_connection);
            return;
        }
        FeedList mFeedList = mMyPostListAdapter.getModelAt(position);

        String url = AllUrls.BASEURL + "postDelete";
        HashMap<String, String> allHashMap = new HashMap<>();
        allHashMap.put("post_id", mFeedList.getId());
        allHashMap.put("duration", DateUtility.getCurrentTimeForsend());

        HashMap<String, String> allHashMapHeader = new HashMap<>();
        allHashMapHeader.put("appKey", AllUrls.APP_KEY);
        allHashMapHeader.put("authToken", PersistentUser.getUserToken(mContext));
        mBusyDialog = new BusyDialog(mContext);
        mBusyDialog.show();

        ServerCallsProvider.volleyPostRequest(url, allHashMap, allHashMapHeader, TAG, new ServerResponse() {
            @Override
            public void onSuccess(String statusCode, String responseServer) {
                mBusyDialog.dismis();
                try {
                    Logger.debugLog("responseServer", responseServer);
                    JSONObject mJsonObject = new JSONObject(responseServer);
                    if (mJsonObject.getBoolean("success")) {
                        mMyPostListAdapter.remvoeList(position);

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
