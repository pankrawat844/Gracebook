package com.grace.book;

import android.app.AlertDialog;
import android.content.Context;
import android.content.Intent;
import android.graphics.drawable.ColorDrawable;
import android.os.Bundle;
import android.os.Handler;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.EditText;
import android.widget.TextView;

import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.grace.book.adapter.CommentsAdapter;
import com.grace.book.adapter.FeedListAdapter;
import com.grace.book.callbackinterface.FilterItemCallback;
import com.grace.book.callbackinterface.ServerResponse;
import com.grace.book.customview.VerticalSpaceItemDecoration;
import com.grace.book.model.CommentsList;
import com.grace.book.model.FeedList;
import com.grace.book.model.GroupList;
import com.grace.book.myapplication.Myapplication;
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

import static com.grace.book.myapplication.Myapplication.getContext;

public class CommentActivity extends AppCompatActivity {
    private static final String TAG = CommentActivity.class.getSimpleName();
    private final int VERTICAL_ITEM_SPACE = 20;
    private Context mContext;
    private RecyclerView recycler_feed;
    private CommentsAdapter mFeedListAdapter;
    private int screen = 0;
    private FeedList mFeedList;
    private BusyDialog mBusyDialog;
    private EditText edittextChat;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_comments);
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
        edittextChat = (EditText) this.findViewById(R.id.edittextChat);
        recycler_feed = (RecyclerView) this.findViewById(R.id.recycler_feed);
        mFeedListAdapter = new CommentsAdapter(mContext, new ArrayList<CommentsList>());
        LinearLayoutManager mLinearLayoutManager = new LinearLayoutManager(mContext, RecyclerView.VERTICAL, false);
        recycler_feed.setLayoutManager(mLinearLayoutManager);
        recycler_feed.addItemDecoration(new VerticalSpaceItemDecoration(VERTICAL_ITEM_SPACE));
        recycler_feed.setAdapter(mFeedListAdapter);
        mFeedListAdapter.addClickListiner(lFilterItemCallback);
        findViewById(R.id.filesend).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                String message = edittextChat.getText().toString();
                if (message.equalsIgnoreCase("")) {
                    ToastHelper.showToast(mContext, "Please enter comment");
                } else {
                    addPostServerRequest(message);

                }
            }
        });

        ServerRequest();
    }

    public FilterItemCallback lFilterItemCallback = new FilterItemCallback() {
        @Override
        public void ClickFilterItemCallback(int type, int position) {
            alertfornewuser(position);
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
            url = AllUrls.BASEURL + "postcommnetlist";
            allHashMap.put("post_id", mFeedList.getId());
        } else if (screen == 2) {
            url = AllUrls.BASEURL + "prayercommentList";
            allHashMap.put("prayer_id", mFeedList.getId());
        } else if (screen == 3) {
            url = AllUrls.BASEURL + "grouppostcommentList";
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
                        mFeedListAdapter.removeAllData();

                        JSONArray jsonArray = mJsonObject.getJSONArray("data");
                        GsonBuilder builder = new GsonBuilder();
                        Gson mGson = builder.create();
                        List<CommentsList> posts = new ArrayList<CommentsList>();
                        posts = Arrays.asList(mGson.fromJson(jsonArray.toString(), CommentsList[].class));
                        ArrayList<CommentsList> allLists = new ArrayList<CommentsList>(posts);
                        mFeedListAdapter.addAllList(allLists);
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

    private void addPostServerRequest(String message) {
        if (!Helpers.isNetworkAvailable(mContext)) {
            Helpers.showOkayDialog(mContext, R.string.no_internet_connection);
            return;
        }
        HashMap<String, String> allHashMap = new HashMap<>();
        String url = AllUrls.BASEURL + "postcommnet";
        if (screen == 1) {
            url = AllUrls.BASEURL + "postcommnet";
            allHashMap.put("post_id", mFeedList.getId());
        } else if (screen == 2) {
            url = AllUrls.BASEURL + "prayerComment";
            allHashMap.put("prayer_id", mFeedList.getId());
        } else if (screen == 3) {
            url = AllUrls.BASEURL + "groupppostaddComment";
            allHashMap.put("group_post_id", mFeedList.getId());

        }
        allHashMap.put("message", message);
        allHashMap.put("comment_time", DateUtility.getCurrentTimeForsend());

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
                        edittextChat.setText("");

                        ServerRequest();

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

    AlertDialog alertDialog = null;

    public void alertfornewuser(final int postion) {
        AlertDialog.Builder builder = new AlertDialog.Builder(CommentActivity.this);
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
        version_text.setText("Delete Comment");
        version_text2.setText("Are you sure you want delete comment");
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
        CommentsList mCommentsList = mFeedListAdapter.getModelAt(position);
        HashMap<String, String> allHashMap = new HashMap<>();
        String url = AllUrls.BASEURL + "postCommentDelete";
        if (screen == 1) {
            url = AllUrls.BASEURL + "postCommentDelete";
            allHashMap.put("comment_id", mCommentsList.getId());
        } else if (screen == 2) {
            url = AllUrls.BASEURL + "prayercommentDelete";
            allHashMap.put("comment_id", mCommentsList.getId());
        } else if (screen == 3) {
            url = AllUrls.BASEURL + "grouppostCommentDelete";
            allHashMap.put("comment_id", mCommentsList.getId());

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
                        mFeedListAdapter.deleteItem(position);
                        Myapplication.selectionComment = mFeedListAdapter.getItemCount();

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
