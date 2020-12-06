package com.zocia.book;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;

import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.zocia.book.adapter.LikedAdapter;
import com.zocia.book.callbackinterface.FilterItemCallback;
import com.zocia.book.callbackinterface.ServerResponse;
import com.zocia.book.customview.VerticalSpaceItemDecoration;
import com.zocia.book.model.CommentsList;
import com.zocia.book.model.FeedList;
import com.zocia.book.myapplication.Myapplication;
import com.zocia.book.networkcalls.ServerCallsProvider;
import com.zocia.book.utils.AllUrls;
import com.zocia.book.utils.BusyDialog;
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

public class LikedListActivity extends AppCompatActivity {
    private static final String TAG = LikedListActivity.class.getSimpleName();
    private final int VERTICAL_ITEM_SPACE = 20;
    private Context mContext;
    private RecyclerView recycler_feed;
    private LikedAdapter mLikedAdapter;
    private int screen = 0;
    private FeedList mFeedList;
    private BusyDialog mBusyDialog;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_liked);
        mContext = this;
        Bundle extra = getIntent().getBundleExtra("extra");
        screen = extra.getInt("screen", 0);
        if (screen == 1) {
            mFeedList = (FeedList) extra.getSerializable("objects");
        } else if (screen == 2) {
            mFeedList = (FeedList) extra.getSerializable("objects");
        } else if (screen == 3) {
            mFeedList = (FeedList) extra.getSerializable("objects");
        }
        initUi();
    }

    private void initUi() {
        findViewById(R.id.layoutBack).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent mIntent = getIntent();
                setResult(RESULT_OK, mIntent);
                finish();
            }
        });
        recycler_feed = (RecyclerView) this.findViewById(R.id.recycler_feed);
        mLikedAdapter = new LikedAdapter(mContext, new ArrayList<CommentsList>());
        LinearLayoutManager mLinearLayoutManager = new LinearLayoutManager(mContext, RecyclerView.VERTICAL, false);
        recycler_feed.setLayoutManager(mLinearLayoutManager);
        recycler_feed.addItemDecoration(new VerticalSpaceItemDecoration(VERTICAL_ITEM_SPACE));
        recycler_feed.setAdapter(mLikedAdapter);
        mLikedAdapter.addClickListiner(lFilterItemCallback);


        ServerRequest();
    }

    public FilterItemCallback lFilterItemCallback = new FilterItemCallback() {
        @Override
        public void ClickFilterItemCallback(int type, int position) {
            CommentsList mFeedList = mLikedAdapter.getModelAt(position);
            Intent mIntent = new Intent(mContext, UserprofileActivity.class);
            Bundle extra = new Bundle();
            extra.putSerializable("objects", mFeedList.getmUsersdata());
            mIntent.putExtra("extra", extra);
            startActivity(mIntent);
        }
    };

    private void ServerRequest() {
        if (!Helpers.isNetworkAvailable(mContext)) {
            Helpers.showOkayDialog(mContext, R.string.no_internet_connection);
            return;
        }
        HashMap<String, String> allHashMap = new HashMap<>();
        String url = "";

        if (screen == 1) {
            url = AllUrls.BASEURL + "postLikelist";
            allHashMap.put("post_id", mFeedList.getId());
        } else if (screen == 2) {
            url = AllUrls.BASEURL + "prayerLikelist";
            allHashMap.put("prayer_id", mFeedList.getId());
        } else if (screen == 3) {
            url = AllUrls.BASEURL + "grouppostLikelist";
            allHashMap.put("group_post_id", mFeedList.getId());

        }
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
                        mLikedAdapter.removeAllData();

                        JSONArray jsonArray = mJsonObject.getJSONArray("data");
                        GsonBuilder builder = new GsonBuilder();
                        Gson mGson = builder.create();
                        List<CommentsList> posts = new ArrayList<CommentsList>();
                        posts = Arrays.asList(mGson.fromJson(jsonArray.toString(), CommentsList[].class));
                        ArrayList<CommentsList> allLists = new ArrayList<CommentsList>(posts);
                        mLikedAdapter.addAllList(allLists);
                        Myapplication.selectionComment = allLists.size();

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
