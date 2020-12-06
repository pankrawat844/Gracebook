package com.zocia.book.fragment;


import android.content.Intent;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.zocia.book.ChatActivity;
import com.zocia.book.LoginActivity;
import com.zocia.book.MessageUserListActivity;
import com.zocia.book.R;
import com.zocia.book.UserprofileActivity;
import com.zocia.book.adapter.FriendListAdapter;
import com.zocia.book.callbackinterface.FilterItemCallback;
import com.zocia.book.callbackinterface.ServerResponse;
import com.zocia.book.customview.VerticalSpaceItemDecoration;
import com.zocia.book.model.FriendList;
import com.zocia.book.model.Usersdata;
import com.zocia.book.networkcalls.ServerCallsProvider;
import com.zocia.book.utils.AllUrls;
import com.zocia.book.utils.Helpers;
import com.zocia.book.utils.Logger;
import com.zocia.book.utils.PersistentUser;

import org.json.JSONArray;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;

public class FriendsFragment extends BaseFragment {
    private static final String TAG = FriendsFragment.class.getSimpleName();
    private final int VERTICAL_ITEM_SPACE = 20;
    private RecyclerView recycler_feed;
    private FriendListAdapter mFriendListAdapter;
    Usersdata mUsersdata;
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_freind, container, false);
        viewScreen(view);
        return view;
    }

    public void viewScreen(View view) {
        recycler_feed = (RecyclerView) view.findViewById(R.id.recycler_feed);
        mFriendListAdapter = new FriendListAdapter(getActivity(), new ArrayList<FriendList>());
        LinearLayoutManager mLinearLayoutManager = new LinearLayoutManager(getActivity(), RecyclerView.VERTICAL, false);
        recycler_feed.setLayoutManager(mLinearLayoutManager);
        recycler_feed.addItemDecoration(new VerticalSpaceItemDecoration(VERTICAL_ITEM_SPACE));
        recycler_feed.setAdapter(mFriendListAdapter);
        mFriendListAdapter.addFilterItemCallback(lFilterItemCallback);
    }

    @Override
    protected void onVisible() {
        serverRequest();
    }

    @Override
    protected void onInvisible() {

    }

    public FilterItemCallback lFilterItemCallback = new FilterItemCallback() {
        @Override
        public void ClickFilterItemCallback(int type, int position) {
            mUsersdata = mFriendListAdapter.getModelAt(position).getmUsersdata();
            if (type == 1) {
//                final Dialog dialog= new Dialog(getActivity());
//                dialog.setContentView(R.layout.dialog_email);
//                final TextView email=dialog.findViewById(R.id.email);
//                TextView btn=dialog.findViewById(R.id.Submit_btn);
//                btn.setOnClickListener(new View.OnClickListener() {
//                    @Override
//                    public void onClick(View view) {
//                       serverEmailRequest(email.getText().toString());
//                       dialog.dismiss();
//                    }
//                });
//                dialog.show();
//                Intent mIntent = new Intent(getActivity(), ChatActivity.class);
//                Bundle extra = new Bundle();
//                extra.putSerializable("objects", mUsersdata);
//                mIntent.putExtra("extra", extra);
//                startActivity(mIntent);
                Intent intent = new Intent(getActivity(), MessageUserListActivity.class);
                intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP | Intent.FLAG_ACTIVITY_NEW_TASK);
                startActivity(intent);
                getActivity().finish();
            } else {
                Intent mIntent = new Intent(getActivity(), UserprofileActivity.class);
                Bundle extra = new Bundle();
                extra.putSerializable("objects", mUsersdata);
                mIntent.putExtra("extra", extra);
                startActivity(mIntent);

            }

        }
    };

    private void serverRequest() {
        if (!Helpers.isNetworkAvailable(getActivity())) {
            Helpers.showOkayDialog(getActivity(), R.string.no_internet_connection);
            return;
        }
        mFriendListAdapter.removeAllData();
        HashMap<String, String> allHashMap = new HashMap<>();
        HashMap<String, String> allHashMapHeader = new HashMap<>();
        allHashMapHeader.put("appKey", AllUrls.APP_KEY);
        allHashMapHeader.put("authToken", PersistentUser.getUserToken(getActivity()));
        final String url = AllUrls.BASEURL + "alreadyFriend";
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
                        mFriendListAdapter.addAllList(allLists);


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

    private void serverEmailRequest(String email) {
        if (!Helpers.isNetworkAvailable(getActivity())) {
            Helpers.showOkayDialog(getActivity(), R.string.no_internet_connection);
            return;
        }
        HashMap<String, String> allHashMap = new HashMap<>();
        HashMap<String, String> allHashMapHeader = new HashMap<>();
        allHashMapHeader.put("appKey", AllUrls.APP_KEY);
        allHashMapHeader.put("authToken", PersistentUser.getUserToken(getActivity()));
        allHashMap.put("id", PersistentUser.getUserID(getActivity()));
        allHashMap.put("email", email);
        final String url = AllUrls.BASEURL + "emailverification";
        ServerCallsProvider.volleyPostRequest(url, allHashMap, allHashMapHeader, TAG, new ServerResponse() {
            @Override
            public void onSuccess(String statusCode, String responseServer) {
                try {


                    Intent mIntent = new Intent(getActivity(), ChatActivity.class);
                    Bundle extra = new Bundle();
                    extra.putSerializable("objects", mUsersdata);
                    mIntent.putExtra("extra", extra);
                    startActivity(mIntent);


                } catch (Exception e) {

                }
            }

            @Override
            public void onFailed(String statusCode, String serverResponse) {
                if (statusCode.equalsIgnoreCase("500")) {
                    Intent mIntent = new Intent(getActivity(), ChatActivity.class);
                    Bundle extra = new Bundle();
                    extra.putSerializable("objects", mUsersdata);
                    mIntent.putExtra("extra", extra);
                    startActivity(mIntent);
                }
                Logger.debugLog("onFailed serverResponse", serverResponse);
            }
        });
    }

}
