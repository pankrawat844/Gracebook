package com.grace.book.fragment;


import android.content.Intent;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.grace.book.BaseActivity;
import com.grace.book.LoginActivity;
import com.grace.book.R;
import com.grace.book.SearchBelievertActivity;
import com.grace.book.UserprofileActivity;
import com.grace.book.adapter.FriendRequestListAdapter;
import com.grace.book.adapter.MyPostListAdapter;
import com.grace.book.callbackinterface.FilterItemCallback;
import com.grace.book.callbackinterface.ServerResponse;
import com.grace.book.customview.VerticalSpaceItemDecoration;
import com.grace.book.model.FeedList;
import com.grace.book.model.FriendList;
import com.grace.book.model.Usersdata;
import com.grace.book.networkcalls.ServerCallsProvider;
import com.grace.book.utils.AllUrls;
import com.grace.book.utils.DateUtility;
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

public class FriendRequestFragment extends BaseFragment {
    private static final String TAG = FriendRequestFragment.class.getSimpleName();
    private final int VERTICAL_ITEM_SPACE = 20;
    private RecyclerView recycler_feed;
    private FriendRequestListAdapter mFriendRequestListAdapter;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_freind, container, false);
        viewScreen(view);
        return view;
    }

    public void viewScreen(View view) {
        recycler_feed = (RecyclerView) view.findViewById(R.id.recycler_feed);
        mFriendRequestListAdapter = new FriendRequestListAdapter(getActivity(), new ArrayList<FriendList>());
        LinearLayoutManager mLinearLayoutManager = new LinearLayoutManager(getActivity(), RecyclerView.VERTICAL, false);
        recycler_feed.setLayoutManager(mLinearLayoutManager);
        recycler_feed.addItemDecoration(new VerticalSpaceItemDecoration(VERTICAL_ITEM_SPACE));
        recycler_feed.setAdapter(mFriendRequestListAdapter);
        mFriendRequestListAdapter.addFilterItemCallback(lFilterItemCallback);

    }

    public FilterItemCallback lFilterItemCallback = new FilterItemCallback() {
        @Override
        public void ClickFilterItemCallback(int type, int position) {
            Usersdata mUsersdata = mFriendRequestListAdapter.getModelAt(position).getmUsersdata();
            if (type == 1) {
                HashMap<String, String> allHashMap = new HashMap<>();
                allHashMap.put("user_id", mUsersdata.getId());
                allHashMap.put("duration", DateUtility.getCurrentTimeForsend());

                acceptFriendserverRequest(allHashMap, position);
            } else if (type == 2) {
                HashMap<String, String> allHashMap = new HashMap<>();
                allHashMap.put("user_id", mUsersdata.getId());
                allHashMap.put("duration", DateUtility.getCurrentTimeForsend());

                rejectFriendserverRequest(allHashMap, position);
            } else {
                Intent mIntent = new Intent(getActivity(), UserprofileActivity.class);
                Bundle extra = new Bundle();
                extra.putSerializable("objects", mUsersdata);
                mIntent.putExtra("extra", extra);
                startActivity(mIntent);

            }

        }
    };

    @Override
    protected void onVisible() {
        serverRequest();
    }

    @Override
    protected void onInvisible() {

    }

    private void serverRequest() {
        if (!Helpers.isNetworkAvailable(getActivity())) {
            Helpers.showOkayDialog(getActivity(), R.string.no_internet_connection);
            return;
        }
        mFriendRequestListAdapter.removeAllData();
        HashMap<String, String> allHashMap = new HashMap<>();
        HashMap<String, String> allHashMapHeader = new HashMap<>();
        allHashMapHeader.put("appKey", AllUrls.APP_KEY);
        allHashMapHeader.put("authToken", PersistentUser.getUserToken(getActivity()));
        final String url = AllUrls.BASEURL + "invitedFriend";
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
                        //mSearchbelieverListAdapter.addAllList(allLists);
                        mFriendRequestListAdapter.addAllList(allLists);


                    }

                } catch (Exception e) {

                }
            }

            @Override
            public void onFailed(String statusCode, String serverResponse) {
                if (statusCode.equalsIgnoreCase("404")) {
                    PersistentUser.resetAllData(getActivity());
                    Intent intent = new Intent(getActivity(), LoginActivity.class);
                    intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP | Intent.FLAG_ACTIVITY_NEW_TASK);
                    startActivity(intent);
                    getActivity().finish();
                }
                Logger.debugLog("onFailed serverResponse", serverResponse);
            }
        });
    }

    private void rejectFriendserverRequest(HashMap<String, String> allHashMap, final int position) {
        if (!Helpers.isNetworkAvailable(getActivity())) {
            Helpers.showOkayDialog(getActivity(), R.string.no_internet_connection);
            return;
        }
        HashMap<String, String> allHashMapHeader = new HashMap<>();
        allHashMapHeader.put("appKey", AllUrls.APP_KEY);
        allHashMapHeader.put("authToken", PersistentUser.getUserToken(getActivity()));
        final String url = AllUrls.BASEURL + "removeFriend";
        ServerCallsProvider.volleyPostRequest(url, allHashMap, allHashMapHeader, TAG, new ServerResponse() {
            @Override
            public void onSuccess(String statusCode, String responseServer) {
                try {
                    Logger.debugLog("responseServer", responseServer);
                    JSONObject mJsonObject = new JSONObject(responseServer);
                    if (mJsonObject.getBoolean("success")) {
                        mFriendRequestListAdapter.deletePostion(position);
                    }

                } catch (Exception e) {

                }
            }

            @Override
            public void onFailed(String statusCode, String serverResponse) {
                if (statusCode.equalsIgnoreCase("404")) {
                    PersistentUser.resetAllData(getActivity());
                    Intent intent = new Intent(getActivity(), LoginActivity.class);
                    intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP | Intent.FLAG_ACTIVITY_NEW_TASK);
                    startActivity(intent);
                    getActivity().finish();
                }
            }
        });
    }


    private void acceptFriendserverRequest(HashMap<String, String> allHashMap, final int position) {
        if (!Helpers.isNetworkAvailable(getActivity())) {
            Helpers.showOkayDialog(getActivity(), R.string.no_internet_connection);
            return;
        }
        HashMap<String, String> allHashMapHeader = new HashMap<>();
        allHashMapHeader.put("appKey", AllUrls.APP_KEY);
        allHashMapHeader.put("authToken", PersistentUser.getUserToken(getActivity()));
        final String url = AllUrls.BASEURL + "acceptedFriend";
        ServerCallsProvider.volleyPostRequest(url, allHashMap, allHashMapHeader, TAG, new ServerResponse() {
            @Override
            public void onSuccess(String statusCode, String responseServer) {
                try {
                    Logger.debugLog("responseServer", responseServer);
                    JSONObject mJsonObject = new JSONObject(responseServer);
                    if (mJsonObject.getBoolean("success")) {
                        mFriendRequestListAdapter.deletePostion(position);
                    }

                } catch (Exception e) {

                }
            }

            @Override
            public void onFailed(String statusCode, String serverResponse) {
                if (statusCode.equalsIgnoreCase("404")) {
                    PersistentUser.resetAllData(getActivity());
                    Intent intent = new Intent(getActivity(), LoginActivity.class);
                    intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP | Intent.FLAG_ACTIVITY_NEW_TASK);
                    startActivity(intent);
                    getActivity().finish();
                }
            }
        });
    }

}
