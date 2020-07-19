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
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;

import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.grace.book.adapter.MyPostListAdapter;
import com.grace.book.callbackinterface.FilterItemCallback;
import com.grace.book.callbackinterface.ServerResponse;
import com.grace.book.customview.VerticalSpaceItemDecoration;
import com.grace.book.model.FeedList;
import com.grace.book.model.Usersdata;
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

public class UserprofileActivity extends AppCompatActivity {
    private static final String TAG = UserprofileActivity.class.getSimpleName();
    private BusyDialog mBusyDialog;
    private Context mContext;
    private LinearLayout layoutBack;
    private final int VERTICAL_ITEM_SPACE = 20;
    private RecyclerView recycler_feed;
    private MyPostListAdapter mMyPostListAdapter;
    private Usersdata mUsersdata;
    private ImageView userprofole_pic;
    private TextView userprofole_name;
    private TextView userprofole_bio;
    private TextView userprofole_member;
    private TextView userprofole_location;
    private LinearLayout layoutAddFRIEND;
    private RelativeLayout layout_allreadFriend;
    private LinearLayout layoutMessage;
    private LinearLayout layout_unfriend;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_useprofile);
        Bundle extra = getIntent().getBundleExtra("extra");
        mUsersdata = (Usersdata) extra.getSerializable("objects");
        mContext = this;
        initUi();
    }

    private void initUi() {
        layoutBack = (LinearLayout) this.findViewById(R.id.layoutBack);
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

        LayoutInflater inflater = (LayoutInflater) getContext().getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        View view = inflater.inflate(R.layout.view_of_profile, null);


        SmartRecyclerAdapter smartRecyclerAdapter = new SmartRecyclerAdapter(mMyPostListAdapter);
        smartRecyclerAdapter.setHeaderView(view);

        recycler_feed.setAdapter(smartRecyclerAdapter);

        userprofole_pic = (ImageView) view.findViewById(R.id.userprofole_pic);
        userprofole_bio = (TextView) view.findViewById(R.id.userprofole_bio);
        userprofole_member = (TextView) view.findViewById(R.id.userprofole_member);
        userprofole_location = (TextView) view.findViewById(R.id.userprofole_location);
        userprofole_name = (TextView) view.findViewById(R.id.userprofole_name);


        layoutAddFRIEND = (LinearLayout) view.findViewById(R.id.layoutAddFRIEND);
        layoutMessage = (LinearLayout) view.findViewById(R.id.layoutMessage);
        layout_unfriend = (LinearLayout) view.findViewById(R.id.layout_unfriend);
        layout_allreadFriend = (RelativeLayout) view.findViewById(R.id.layout_allreadFriend);

        layoutAddFRIEND.setVisibility(View.GONE);
        layout_allreadFriend.setVisibility(View.GONE);

        mMyPostListAdapter.addClickListiner(lFilterItemCallback);

        layoutAddFRIEND.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                HashMap<String, String> allHashMap = new HashMap<>();
                allHashMap.put("user_id", mUsersdata.getId());
                allHashMap.put("duration", DateUtility.getCurrentTimeForsend());
                addFriendserverRequest(allHashMap);
            }
        });
        layoutMessage.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (mUsersdata.getFriend_type().equalsIgnoreCase("1")) {
                    Intent mIntent = new Intent(mContext, ChatActivity.class);
                    Bundle extra = new Bundle();
                    extra.putSerializable("objects", mUsersdata);
                    mIntent.putExtra("extra", extra);
                    startActivity(mIntent);
                } else {
                    ToastHelper.showToast(mContext, "Your friend don't accept yet your request");
                    return;
                }
            }
        });
        layout_unfriend.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                HashMap<String, String> allHashMap = new HashMap<>();
                allHashMap.put("user_id", mUsersdata.getId());
                allHashMap.put("duration", DateUtility.getCurrentTimeForsend());
                removeFriendserverRequest(allHashMap);
            }
        });
        serverRequest("0");
        ConstantFunctions.loadImageForCircel(mUsersdata.getProfile_pic(), userprofole_pic);
        userprofole_name.setText(mUsersdata.getFname() + " " + mUsersdata.getLname());
        userprofole_bio.setText(Logger.EmptyString(mUsersdata.getBio()));
        userprofole_member.setText("Member of " + mUsersdata.getChurch()+" church");
        userprofole_location.setText(mUsersdata.getCity() + ", " + mUsersdata.getCountry());
    }

    public void userInformationShow() {

        if (!mUsersdata.getId().equalsIgnoreCase(PersistentUser.getUserID(mContext))) {
            layout_allreadFriend.setVisibility(View.GONE);
            layoutAddFRIEND.setVisibility(View.GONE);
            if (mUsersdata.getIs_friend().equalsIgnoreCase("1")) {
                layout_allreadFriend.setVisibility(View.VISIBLE);
            } else if (mUsersdata.getIs_friend().equalsIgnoreCase("0")) {
                layoutAddFRIEND.setVisibility(View.VISIBLE);
            }
        }
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
                Intent mIntent = new Intent(UserprofileActivity.this, CommentActivity.class);
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
                Intent mIntent = new Intent(UserprofileActivity.this, VideoPlayertActivity.class);
                mIntent.putExtra("url", mFeedList.getPost_path());
                startActivity(mIntent);
            }
        }
    };

    private void serverRequest(final String limit) {
        if (!Helpers.isNetworkAvailable(mContext)) {
            Helpers.showOkayDialog(mContext, R.string.no_internet_connection);
            return;
        }
        mBusyDialog = new BusyDialog(mContext);
        mBusyDialog.show();

        HashMap<String, String> allHashMap = new HashMap<>();
        allHashMap.put("limit", limit);
        allHashMap.put("user_id", mUsersdata.getId());

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
                        if (limit.equalsIgnoreCase("0")) {
                            JSONObject users = mJsonObject.getJSONObject("users");
                            GsonBuilder builder = new GsonBuilder();
                            Gson mGson = builder.create();
                            mUsersdata = mGson.fromJson(users.toString(), Usersdata.class);
                            userInformationShow();
                        }
                        JSONArray jsonArray = mJsonObject.getJSONArray("data");
                        GsonBuilder builder = new GsonBuilder();
                        Gson mGson = builder.create();
                        List<FeedList> posts = new ArrayList<FeedList>();
                        posts = Arrays.asList(mGson.fromJson(jsonArray.toString(), FeedList[].class));
                        ArrayList<FeedList> allLists = new ArrayList<FeedList>(posts);
                        mMyPostListAdapter.addAllList(allLists);


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
            }

        });
    }

    private void addFriendserverRequest(HashMap<String, String> allHashMap) {
        if (!Helpers.isNetworkAvailable(mContext)) {
            Helpers.showOkayDialog(mContext, R.string.no_internet_connection);
            return;
        }
        mBusyDialog = new BusyDialog(mContext);
        mBusyDialog.show();

        HashMap<String, String> allHashMapHeader = new HashMap<>();
        allHashMapHeader.put("appKey", AllUrls.APP_KEY);
        allHashMapHeader.put("authToken", PersistentUser.getUserToken(mContext));
        final String url = AllUrls.BASEURL + "addFriend";
        ServerCallsProvider.volleyPostRequest(url, allHashMap, allHashMapHeader, TAG, new ServerResponse() {
            @Override
            public void onSuccess(String statusCode, String responseServer) {
                try {
                    mBusyDialog.dismis();
                    Logger.debugLog("responseServer", responseServer);
                    JSONObject mJsonObject = new JSONObject(responseServer);
                    if (mJsonObject.getBoolean("success")) {
                        mUsersdata.setIs_friend("1");
                        mUsersdata.setFriend_type("0");
                        userInformationShow();
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

    private void removeFriendserverRequest(HashMap<String, String> allHashMap) {
        if (!Helpers.isNetworkAvailable(mContext)) {
            Helpers.showOkayDialog(mContext, R.string.no_internet_connection);
            return;
        }
        mBusyDialog = new BusyDialog(mContext);
        mBusyDialog.show();

        HashMap<String, String> allHashMapHeader = new HashMap<>();
        allHashMapHeader.put("appKey", AllUrls.APP_KEY);
        allHashMapHeader.put("authToken", PersistentUser.getUserToken(mContext));
        final String url = AllUrls.BASEURL + "removeFriend";
        ServerCallsProvider.volleyPostRequest(url, allHashMap, allHashMapHeader, TAG, new ServerResponse() {
            @Override
            public void onSuccess(String statusCode, String responseServer) {
                try {
                    mBusyDialog.dismis();
                    Logger.debugLog("responseServer", responseServer);
                    JSONObject mJsonObject = new JSONObject(responseServer);
                    if (mJsonObject.getBoolean("success")) {
                        mUsersdata.setIs_friend("0");
                        mUsersdata.setFriend_type("0");
                        userInformationShow();
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
        AlertDialog.Builder builder = new AlertDialog.Builder(UserprofileActivity.this);
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
