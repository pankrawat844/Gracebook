package com.grace.book;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.View;
import android.widget.EditText;

import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.grace.book.adapter.SearchbelieverListAdapter;
import com.grace.book.callbackinterface.FilterItemCallback;
import com.grace.book.callbackinterface.ServerResponse;
import com.grace.book.customview.VerticalSpaceItemDecoration;
import com.grace.book.model.Usersdata;
import com.grace.book.networkcalls.ServerCallsProvider;
import com.grace.book.utils.AllUrls;
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

public class SearchBelievertActivity extends AppCompatActivity {
    private static final String TAG = SearchBelievertActivity.class.getSimpleName();
    private Context mContext;
    private final int VERTICAL_ITEM_SPACE = 20;
    private RecyclerView recycler_feed;
    private SearchbelieverListAdapter mSearchbelieverListAdapter;
    private EditText edittextFname;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_search_believers);
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
        edittextFname = (EditText) this.findViewById(R.id.edittextFname);
        recycler_feed = (RecyclerView) this.findViewById(R.id.recycler_search);
        mSearchbelieverListAdapter = new SearchbelieverListAdapter(mContext, new ArrayList<Usersdata>());
        LinearLayoutManager mLinearLayoutManager = new LinearLayoutManager(mContext, RecyclerView.VERTICAL, false);
        recycler_feed.setLayoutManager(mLinearLayoutManager);
        recycler_feed.addItemDecoration(new VerticalSpaceItemDecoration(VERTICAL_ITEM_SPACE));

        recycler_feed.setAdapter(mSearchbelieverListAdapter);
        mSearchbelieverListAdapter.addFilterItemCallback(lFilterItemCallback);

        HashMap<String, String> allHashMap = new HashMap<>();
        serverRequest(allHashMap);

        findViewById(R.id.btnsearch).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                String textName = edittextFname.getText().toString().trim();
                if (textName.equalsIgnoreCase("")) {

                } else {
                    mSearchbelieverListAdapter.removeAllData();
                    HashMap<String, String> allMap = new HashMap<>();
                    allMap.put("search_text", textName);
                    serverRequest(allMap);
                }
            }
        });
        edittextFname.addTextChangedListener(new TextWatcher() {

            @Override
            public void afterTextChanged(Editable s) {
            }

            @Override
            public void beforeTextChanged(CharSequence s, int start,
                                          int count, int after) {
            }

            @Override
            public void onTextChanged(CharSequence s, int start,
                                      int before, int count) {
                if (s.length() == 0) {
                    mSearchbelieverListAdapter.removeAllData();
                    HashMap<String, String> allHashMap = new HashMap<>();
                    serverRequest(allHashMap);
                }
            }
        });


    }

    public FilterItemCallback lFilterItemCallback = new FilterItemCallback() {
        @Override
        public void ClickFilterItemCallback(int type, int position) {
            Usersdata mUsersdata = mSearchbelieverListAdapter.getModelAt(position);

            if (type == 0) {
                Intent mm = new Intent(SearchBelievertActivity.this, UserprofileActivity.class);
                startActivity(mm);
            } else {
                HashMap<String, String> allHashMap = new HashMap<>();
                allHashMap.put("user_id", mUsersdata.getId());
                addFriendserverRequest(allHashMap, position);
            }

        }
    };

    private void serverRequest(HashMap<String, String> allHashMap) {
        if (!Helpers.isNetworkAvailable(mContext)) {
            Helpers.showOkayDialog(mContext, R.string.no_internet_connection);
            return;
        }

        HashMap<String, String> allHashMapHeader = new HashMap<>();
        allHashMapHeader.put("appKey", AllUrls.APP_KEY);
        allHashMapHeader.put("authToken", PersistentUser.getUserToken(mContext));
        final String url = AllUrls.BASEURL + "searchbeliever";
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
                        List<Usersdata> posts = new ArrayList<Usersdata>();
                        posts = Arrays.asList(mGson.fromJson(jsonArray.toString(), Usersdata[].class));
                        ArrayList<Usersdata> allLists = new ArrayList<Usersdata>(posts);
                        mSearchbelieverListAdapter.addAllList(allLists);

                    }

                } catch (Exception e) {

                }
            }

            @Override
            public void onFailed(String statusCode, String serverResponse) {
                ToastHelper.showToast(mContext, serverResponse);
                Logger.debugLog("onFailed serverResponse", serverResponse);
            }
        });
    }

    private void addFriendserverRequest(HashMap<String, String> allHashMap, final int position) {
        if (!Helpers.isNetworkAvailable(mContext)) {
            Helpers.showOkayDialog(mContext, R.string.no_internet_connection);
            return;
        }
        HashMap<String, String> allHashMapHeader = new HashMap<>();
        allHashMapHeader.put("appKey", AllUrls.APP_KEY);
        allHashMapHeader.put("authToken", PersistentUser.getUserToken(mContext));
        final String url = AllUrls.BASEURL + "addFriend";
        ServerCallsProvider.volleyPostRequest(url, allHashMap, allHashMapHeader, TAG, new ServerResponse() {
            @Override
            public void onSuccess(String statusCode, String responseServer) {
                try {
                    Logger.debugLog("responseServer", responseServer);
                    JSONObject mJsonObject = new JSONObject(responseServer);
                    if (mJsonObject.getBoolean("success")) {
                        mSearchbelieverListAdapter.deletePostion(position);
                    }

                } catch (Exception e) {

                }
            }

            @Override
            public void onFailed(String statusCode, String serverResponse) {
                ToastHelper.showToast(mContext, serverResponse);
                Logger.debugLog("onFailed serverResponse", serverResponse);
            }
        });
    }

}
