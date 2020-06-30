package com.grace.book;

import android.content.Context;
import android.content.Intent;
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


        layoutAddFRIEND.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                HashMap<String, String> allHashMap = new HashMap<>();
                allHashMap.put("user_id", mUsersdata.getId());
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
                removeFriendserverRequest(allHashMap);
            }
        });
        serverRequest("0");
        ConstantFunctions.loadImageForCircel(mUsersdata.getProfile_pic(), userprofole_pic);
        userprofole_name.setText(mUsersdata.getFname() + " " + mUsersdata.getLname());
        userprofole_bio.setText(Logger.EmptyString(mUsersdata.getBio()));
        userprofole_member.setText("Member of " + mUsersdata.getChurch());
        userprofole_location.setText("Lives in " + mUsersdata.getCity() + ", " + mUsersdata.getCountry());
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
            if (position == 0) {
                Intent mm = new Intent(UserprofileActivity.this, UserprofileActivity.class);
                startActivity(mm);
            } else if (position == 1) {
                Intent mm = new Intent(UserprofileActivity.this, CommentActivity.class);
                startActivity(mm);
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

}
