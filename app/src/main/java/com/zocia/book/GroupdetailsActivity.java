package com.zocia.book;

import android.app.AlertDialog;
import android.content.Context;
import android.content.Intent;
import android.graphics.drawable.ColorDrawable;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.ImageView;
import android.widget.PopupMenu;
import android.widget.TextView;

import androidx.annotation.Nullable;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import androidx.swiperefreshlayout.widget.SwipeRefreshLayout;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.zocia.book.adapter.GroupPostAdapter;
import com.zocia.book.callbackinterface.FilterItemCallback;
import com.zocia.book.callbackinterface.ServerResponse;
import com.zocia.book.customview.EndlessRecyclerViewScrollListener;
import com.zocia.book.customview.VerticalSpaceItemDecoration;
import com.zocia.book.model.FeedList;
import com.zocia.book.model.GroupList;
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

public class GroupdetailsActivity extends BaseActivity {
    private static final String TAG = UserprofileActivity.class.getSimpleName();
    private final int VERTICAL_ITEM_SPACE = 20;
    private RecyclerView recycler_feed;
    private GroupPostAdapter mGroupListAdapter;
    private Context mContext;
    private ImageView moreInformation;
    private GroupList mGroupList;
    private BusyDialog mBusyDialog;
    private SwipeRefreshLayout swipeRefreshLayout;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_group_post);
        Bundle extra = getIntent().getBundleExtra("extra");
        mGroupList = (GroupList) extra.getSerializable("objects");
        mContext = this;
        initUI();
    }

    private void initUI() {
        findViewById(R.id.layoutBack).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                finish();
            }
        });
        swipeRefreshLayout = (SwipeRefreshLayout) findViewById(R.id.swipe_refresh_layout);
        moreInformation = (ImageView) this.findViewById(R.id.moreInformation);
        recycler_feed = (RecyclerView) this.findViewById(R.id.recycler_feed);
        mGroupListAdapter = new GroupPostAdapter(mContext, new ArrayList<FeedList>());
        LinearLayoutManager mLinearLayoutManager = new LinearLayoutManager(mContext, RecyclerView.VERTICAL, false);
        recycler_feed.setLayoutManager(mLinearLayoutManager);
        recycler_feed.addItemDecoration(new VerticalSpaceItemDecoration(VERTICAL_ITEM_SPACE));


        LayoutInflater inflater = (LayoutInflater) getContext().getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        View view = inflater.inflate(R.layout.header_group_screen, null);

        TextView groupTextname = (TextView) view.findViewById(R.id.groupTextname);
        groupTextname.setText(mGroupList.getGroup_name());

        SmartRecyclerAdapter smartRecyclerAdapter = new SmartRecyclerAdapter(mGroupListAdapter);
        recycler_feed.setAdapter(smartRecyclerAdapter);
        smartRecyclerAdapter.setHeaderView(view);


        findViewById(R.id.edittextHomePost).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent mIntent = new Intent(GroupdetailsActivity.this, PostallActivity.class);
                mIntent.putExtra("screenType", 3);
                mIntent.putExtra("group_id", mGroupList.getGroup_id());
                startActivityForResult(mIntent, 101);
            }
        });
        moreInformation.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                popupMenu();
            }
        });

        mGroupListAdapter.addClickListiner(lFilterItemCallback);
        mGroupListAdapter.removeAllData();
        ServerRequest("0");
        swipeRefreshLayout.setOnRefreshListener(new SwipeRefreshLayout.OnRefreshListener() {
            @Override
            public void onRefresh() {
                mGroupListAdapter.removeAllData();
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

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == 101) {
            mGroupListAdapter.removeAllData();
            ServerRequest("0");
        }
    }

    public FilterItemCallback lFilterItemCallback = new FilterItemCallback() {
        @Override
        public void ClickFilterItemCallback(int type, int position) {
            FeedList mFeedList = mGroupListAdapter.getModelAt(position);

            if (type == 0) {
                Intent mIntent = new Intent(mContext, UserprofileActivity.class);
                Bundle extra = new Bundle();
                extra.putSerializable("objects", mFeedList.getmUsersdata());
                mIntent.putExtra("extra", extra);
                startActivity(mIntent);

            } else if (type == 1) {

            } else if (type == 2) {
                Intent mIntent = new Intent(GroupdetailsActivity.this, CommentActivity.class);
                Bundle extra = new Bundle();
                extra.putSerializable("objects", mFeedList);
                extra.putInt("screen", 3);
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
                deleteGrouProst(position);
            } else if (type == 5) {
                Intent mIntent = new Intent(GroupdetailsActivity.this, VideoPlayertActivity.class);
                mIntent.putExtra("url", mFeedList.getPost_path());
                startActivity(mIntent);
            } else if (type == 6) {
                Intent mIntent = new Intent(GroupdetailsActivity.this, LikedListActivity.class);
                Bundle extra = new Bundle();
                extra.putSerializable("objects", mFeedList);
                extra.putInt("screen", 3);
                mIntent.putExtra("extra", extra);
                startActivityForResult(mIntent, 102);

            }
        }
    };


    public void popupMenu() {
        PopupMenu popup = new PopupMenu(GroupdetailsActivity.this, moreInformation);
        popup.getMenuInflater().inflate(R.menu.menu_doctors, popup.getMenu());
        popup.setOnMenuItemClickListener(new PopupMenu.OnMenuItemClickListener() {
            public boolean onMenuItemClick(MenuItem item) {
                int id = item.getItemId();
                if (id == R.id.action_booknow) {
                    alertfornewuser(0);
                    return true;
                } else if (id == R.id.action_chat) {
                    alertfornewuser(1);
                    return true;
                } else if (id == R.id.action_addfriend) {
                    Intent mIntent = new Intent(GroupdetailsActivity.this, AddGroupFriendActivity.class);
                    Bundle extra = new Bundle();
                    extra.putSerializable("objects", mGroupList);
                    mIntent.putExtra("extra", extra);
                    startActivityForResult(mIntent, 1010);
                    return true;
                } else if (id == R.id.action_members) {
                    Intent mIntent = new Intent(GroupdetailsActivity.this, GroupFriendListActivity.class);
                    Bundle extra = new Bundle();
                    extra.putSerializable("objects", mGroupList);
                    mIntent.putExtra("extra", extra);
                    startActivityForResult(mIntent, 1010);
                    return true;
                }

                return true;
            }
        });
        Menu popupMenu = popup.getMenu();
        if (!mGroupList.getGroup_owner().equalsIgnoreCase(PersistentUser.getUserID(mContext)))
            popupMenu.findItem(R.id.action_booknow).setVisible(false);


        popup.show();
    }

    private void leaverServerRequest(int position) {
        if (!Helpers.isNetworkAvailable(mContext)) {
            Helpers.showOkayDialog(mContext, R.string.no_internet_connection);
            return;
        }
        String url = AllUrls.BASEURL + "groupAdd";
        if (position == 0) {
            url = AllUrls.BASEURL + "groupdelete";
        } else if (position == 1) {
            url = AllUrls.BASEURL + "groupmemebrremove";

        }

        HashMap<String, String> allHashMap = new HashMap<>();
        allHashMap.put("group_id", mGroupList.getGroup_id());

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
                        Intent mIntent = new Intent();
                        setResult(RESULT_OK, mIntent);
                        finish();

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
        AlertDialog.Builder builder = new AlertDialog.Builder(GroupdetailsActivity.this);
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
        if (postion == 0) {
            version_text.setText("Delete Group");
            version_text2.setText("Are you sure you want delete group");

        } else if (postion == 1) {
            version_text.setText("Leave  Group");
            version_text2.setText("Are you sure you want leave group");
        }


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

    private void ServerRequest(final String limit) {
        if (!Helpers.isNetworkAvailable(mContext)) {
            Helpers.showOkayDialog(mContext, R.string.no_internet_connection);
            return;
        }
        mBusyDialog = new BusyDialog(mContext);
        mBusyDialog.show();

        HashMap<String, String> allHashMap = new HashMap<>();
        allHashMap.put("limit", limit);
        allHashMap.put("group_id", mGroupList.getGroup_id());

        HashMap<String, String> allHashMapHeader = new HashMap<>();
        allHashMapHeader.put("appKey", AllUrls.APP_KEY);
        allHashMapHeader.put("authToken", PersistentUser.getUserToken(mContext));
        final String url = AllUrls.BASEURL + "groupPost";
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
                        mGroupListAdapter.addAllList(allLists);


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

    public void deleteGrouProst(final int postion) {
        AlertDialog.Builder builder = new AlertDialog.Builder(GroupdetailsActivity.this);
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
                deletePostData(postion);
            }
        });
    }

    private void deletePostData(final int position) {
        if (!Helpers.isNetworkAvailable(mContext)) {
            Helpers.showOkayDialog(mContext, R.string.no_internet_connection);
            return;
        }
        FeedList mFeedList = mGroupListAdapter.getModelAt(position);

        String url = AllUrls.BASEURL + "grouppostDelete";
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
                        mGroupListAdapter.remvoeList(position);

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
