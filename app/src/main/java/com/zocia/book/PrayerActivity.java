package com.zocia.book;

import android.app.AlertDialog;
import android.content.Context;
import android.content.Intent;
import android.graphics.drawable.ColorDrawable;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.LinearLayout;
import android.widget.TextView;

import androidx.annotation.Nullable;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import androidx.swiperefreshlayout.widget.SwipeRefreshLayout;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.zocia.book.adapter.PrayRequestAdapter;
import com.zocia.book.callbackinterface.FilterItemCallback;
import com.zocia.book.callbackinterface.ServerResponse;
import com.zocia.book.customview.EndlessRecyclerViewScrollListener;
import com.zocia.book.customview.VerticalSpaceItemDecoration;
import com.zocia.book.model.FeedList;
import com.zocia.book.myapplication.Myapplication;
import com.zocia.book.networkcalls.ServerCallsProvider;
import com.zocia.book.utils.AllUrls;
import com.zocia.book.utils.BusyDialog;
import com.zocia.book.utils.ConstantFunctions;
import com.zocia.book.utils.Helpers;
import com.zocia.book.utils.Logger;
import com.zocia.book.utils.PersistentUser;
import com.zocia.book.utils.ToastHelper;
import com.headerfooter.songhang.library.SmartRecyclerAdapter;

import org.json.JSONArray;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;

import static com.zocia.book.myapplication.Myapplication.getContext;

public class PrayerActivity extends BaseActivity {
    private static final String TAG = PrayerActivity.class.getSimpleName();
    private final int VERTICAL_ITEM_SPACE = 20;
    private RecyclerView recycler_feed;
    private PrayRequestAdapter mPrayRequestAdapter;
    private Context mContext;
    private SwipeRefreshLayout swipeRefreshLayout;
    private BusyDialog mBusyDialog;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        getLayoutInflater().inflate(R.layout.activity_prayer, frameLayout);
        Myapplication.selection = 1;
        mContext = this;
        selectedDeselectedLayut();
        initUI();
        titile.setText("Forum");
    }

    private void initUI() {
        swipeRefreshLayout = (SwipeRefreshLayout) findViewById(R.id.swipe_refresh_layout);

        recycler_feed = (RecyclerView) this.findViewById(R.id.recycler_feed);
        mPrayRequestAdapter = new PrayRequestAdapter(mContext, new ArrayList<FeedList>());
        LinearLayoutManager mLinearLayoutManager = new LinearLayoutManager(mContext, RecyclerView.VERTICAL, false);
        recycler_feed.setLayoutManager(mLinearLayoutManager);
        recycler_feed.addItemDecoration(new VerticalSpaceItemDecoration(VERTICAL_ITEM_SPACE));



        LayoutInflater inflater = (LayoutInflater) getContext().getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        View view = inflater.inflate(R.layout.header_prayer_screen, null);
        LinearLayout edittextChat = (LinearLayout) this.findViewById(R.id.edittextHomePost);
        edittextChat.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent mIntent = new Intent(PrayerActivity.this, PostallActivity.class);
                mIntent.putExtra("screenType", 2);
                startActivityForResult(mIntent, 101);
            }
        });

        SmartRecyclerAdapter smartRecyclerAdapter = new SmartRecyclerAdapter(mPrayRequestAdapter);
//        smartRecyclerAdapter.setHeaderView(view);
        recycler_feed.setAdapter(smartRecyclerAdapter);
        
        mPrayRequestAdapter.addClickListiner(lFilterItemCallback);
        mPrayRequestAdapter.removeAllData();
        ServerRequest("0");
        swipeRefreshLayout.setOnRefreshListener(new SwipeRefreshLayout.OnRefreshListener() {
            @Override
            public void onRefresh() {
                mPrayRequestAdapter.removeAllData();
                ServerRequest("0");
                swipeRefreshLayout.setRefreshing(false);

            }
        });
        recycler_feed.addOnScrollListener(new EndlessRecyclerViewScrollListener(mLinearLayoutManager) {
            @Override
            public void onLoadMore(int page, int totalItemsCount) {
                Log.e("totalItemsCount", "are" + totalItemsCount);
                if (totalItemsCount > 20) {
                    int itemPages = totalItemsCount / 20;
                    itemPages = itemPages + 1;
                    ServerRequest("" + itemPages);
                }

            }
        });

        findViewById(R.id.starLayout).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent mIntent = new Intent(PrayerActivity.this, PrayerStarActivity.class);
                startActivity(mIntent);
            }
        });
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == 101) {
            mPrayRequestAdapter.removeAllData();
            ServerRequest("0");
        } else if (requestCode == 102) {
            mPrayRequestAdapter.getModelAt(Myapplication.selectionOrder).setLike_count("" + Myapplication.selectionComment);
            mPrayRequestAdapter.notifyDataSetChanged();
        }
    }

    public FilterItemCallback lFilterItemCallback = new FilterItemCallback() {
        @Override
        public void ClickFilterItemCallback(int type, int position) {
            FeedList mFeedList = mPrayRequestAdapter.getModelAt(position);

            if (type == 0) {
                Intent mIntent = new Intent(mContext, UserprofileActivity.class);
                Bundle extra = new Bundle();
                extra.putSerializable("objects", mFeedList.getmUsersdata());
                mIntent.putExtra("extra", extra);
                startActivity(mIntent);

            } else if (type == 1) {

            } else if (type == 2) {
                Myapplication.selectionOrder = position;
                Myapplication.selectionComment = Integer.parseInt(mFeedList.getComment_count());
                Intent mIntent = new Intent(PrayerActivity.this, CommentActivity.class);
                Bundle extra = new Bundle();
                extra.putSerializable("objects", mFeedList);
                extra.putInt("screen", 2);
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
                Intent mIntent = new Intent(PrayerActivity.this, VideoPlayertActivity.class);
                mIntent.putExtra("url", mFeedList.getPost_path());
                startActivity(mIntent);
            } else if (type == 6) {
                Intent mIntent = new Intent(PrayerActivity.this, LikedListActivity.class);
                Bundle extra = new Bundle();
                extra.putSerializable("objects", mFeedList);
                extra.putInt("screen", 2);
                mIntent.putExtra("extra", extra);
                startActivityForResult(mIntent, 102);

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
        allHashMap.put("id", PersistentUser.getUserID(this));

        HashMap<String, String> allHashMapHeader = new HashMap<>();
        allHashMapHeader.put("appKey", AllUrls.APP_KEY);
        allHashMapHeader.put("authToken", PersistentUser.getUserToken(mContext));
        final String url = AllUrls.BASEURL + "homePrayer";
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
                        mPrayRequestAdapter.addAllList(allLists);


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
        AlertDialog.Builder builder = new AlertDialog.Builder(PrayerActivity.this);
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
        FeedList mFeedList = mPrayRequestAdapter.getModelAt(position);

        String url = AllUrls.BASEURL + "postDelete";
        HashMap<String, String> allHashMap = new HashMap<>();
        allHashMap.put("post_id", mFeedList.getId());

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
                        mPrayRequestAdapter.remvoeList(position);
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
