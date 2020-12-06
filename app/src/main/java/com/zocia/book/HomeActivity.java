package com.zocia.book;

import androidx.annotation.Nullable;
import androidx.cardview.widget.CardView;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import androidx.swiperefreshlayout.widget.SwipeRefreshLayout;
import androidx.viewpager.widget.ViewPager;

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

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.zocia.book.adapter.FeedListAdapter;
import com.zocia.book.adapter.HomeadvertismentAdapter;
import com.zocia.book.callbackinterface.FilterItemCallback;
import com.zocia.book.callbackinterface.ServerResponse;
import com.zocia.book.customview.EndlessRecyclerViewScrollListener;
import com.zocia.book.customview.VerticalSpaceItemDecoration;
import com.zocia.book.model.BannerList;
import com.zocia.book.model.FeedList;
import com.zocia.book.myapplication.Myapplication;
import com.zocia.book.networkcalls.ServerCallsProvider;
import com.zocia.book.pageIndicator.CirclePageIndicator;
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

public class HomeActivity extends BaseActivity {
    private static final String TAG = HomeActivity.class.getSimpleName();
    private final int VERTICAL_ITEM_SPACE = 20;
    private RecyclerView recycler_feed;
    private FeedListAdapter mFeedListAdapter;
    private Context mContext;
    private ViewPager pager;
    private CirclePageIndicator viewpager_indicator;
    private HomeadvertismentAdapter mSliderAdapter;
    public FilterItemCallback lFilterItemCallback = new FilterItemCallback() {
        @Override
        public void ClickFilterItemCallback(int type, int position) {
            FeedList mFeedList = mFeedListAdapter.getModelAt(position);

            if (type == 0) {
                Intent mIntent = new Intent(mContext, UserprofileActivity.class);
                Bundle extra = new Bundle();
                extra.putSerializable("objects", mFeedList.getmUsersdata());
                mIntent.putExtra("extra", extra);
                startActivity(mIntent);

            } else if (type == 1) {

            } else if (type == 2) {
                Intent mIntent = new Intent(HomeActivity.this, CommentActivity.class);
                Bundle extra = new Bundle();
                extra.putSerializable("objects", mFeedList);
                extra.putInt("screen", 1);
                extra.putInt("position", position);
                mIntent.putExtra("extra", extra);
                startActivityForResult(mIntent, 102);

            } else if (type == 3) {
                String text = Logger.EmptyString(mFeedList.getDetails());
                if (mFeedList.getPost_type().equalsIgnoreCase("0")) {
                    ConstantFunctions.openIntentForShare(mContext, text.replace(" ", "%20"));
                } else {
                    text = text + " \n\n" + mFeedList.getPost_path();
                    ConstantFunctions.openIntentForShare(mContext, text.replace(" ", "%20"));

                }

            } else if (type == 4) {
                alertfornewuser(position);
            } else if (type == 5) {
                Intent mIntent = new Intent(HomeActivity.this, VideoPlayertActivity.class);
                mIntent.putExtra("url", mFeedList.getPost_path());
                startActivity(mIntent);
            } else if (type == 6) {
                Intent mIntent = new Intent(HomeActivity.this, LikedListActivity.class);
                Bundle extra = new Bundle();
                extra.putSerializable("objects", mFeedList);
                extra.putInt("screen", 1);
                mIntent.putExtra("extra", extra);
                startActivityForResult(mIntent, 102);

            }
        }
    };
    private BusyDialog mBusyDialog;
    private SwipeRefreshLayout swipeRefreshLayout;
    private CardView headeAdvertisemnetLayout;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        getLayoutInflater().inflate(R.layout.activity_home, frameLayout);
        Myapplication.selection = 0;
        mContext = this;
        selectedDeselectedLayut();
        initUI();
        titile.setText("Zocia");

    }

    private void initUI() {
        swipeRefreshLayout = (SwipeRefreshLayout) findViewById(R.id.swipe_refresh_layout);
        recycler_feed = (RecyclerView) this.findViewById(R.id.recycler_feed);
        mFeedListAdapter = new FeedListAdapter(mContext, new ArrayList<FeedList>());
        LinearLayoutManager mLinearLayoutManager = new LinearLayoutManager(mContext, RecyclerView.VERTICAL, false);
        recycler_feed.setLayoutManager(mLinearLayoutManager);
        recycler_feed.addItemDecoration(new VerticalSpaceItemDecoration(VERTICAL_ITEM_SPACE));
        SmartRecyclerAdapter smartRecyclerAdapter = new SmartRecyclerAdapter(mFeedListAdapter);
        LayoutInflater inflater = (LayoutInflater) getContext().getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        View view = inflater.inflate(R.layout.header_home_screen, null);
        headeAdvertisemnetLayout = (CardView) view.findViewById(R.id.headeAdvertisemnetLayout);
        pager = (ViewPager) view.findViewById(R.id.pager);
        viewpager_indicator = (CirclePageIndicator) view.findViewById(R.id.viewpager_indicator);
        mSliderAdapter = new HomeadvertismentAdapter(getSupportFragmentManager());
        pager.setAdapter(mSliderAdapter);
        viewpager_indicator.setViewPager(pager);
        viewpager_indicator.setOnPageChangeListener(new ViewPager.OnPageChangeListener() {
            @Override
            public void onPageScrolled(int position, float positionOffset, int positionOffsetPixels) {

            }

            @Override
            public void onPageSelected(int position) {
            }

            @Override
            public void onPageScrollStateChanged(int state) {

            }
        });
        recycler_feed.setAdapter(smartRecyclerAdapter);
        smartRecyclerAdapter.setHeaderView(view);

        LinearLayout edittextHomePost = (LinearLayout) this.findViewById(R.id.edittextHomePost);
        edittextHomePost.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent mIntent = new Intent(HomeActivity.this, PostallActivity.class);
                mIntent.putExtra("screenType", 0);
                startActivityForResult(mIntent, 101);
            }
        });
        mFeedListAdapter.addClickListiner(lFilterItemCallback);
        mFeedListAdapter.removeAllData();

        swipeRefreshLayout.setOnRefreshListener(new SwipeRefreshLayout.OnRefreshListener() {
            @Override
            public void onRefresh() {
                mFeedListAdapter.removeAllData();
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
    }

    @Override
    protected void onResume() {
        super.onResume();
        ServerRequest("0");
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == 101) {
            mFeedListAdapter.removeAllData();
//            ServerRequest("0");
        } else if (requestCode == 102) {
            mFeedListAdapter.notifyDataSetChanged();
        }
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
        allHashMap.put("id", PersistentUser.getUserID(this));
        HashMap<String, String> allHashMapHeader = new HashMap<>();
        allHashMapHeader.put("appKey", AllUrls.APP_KEY);
        allHashMapHeader.put("authToken", PersistentUser.getUserToken(mContext));
        final String url = AllUrls.BASEURL + "homePost";
        ServerCallsProvider.volleyPostRequest(url, allHashMap, allHashMapHeader, TAG, new ServerResponse() {
            @Override
            public void onSuccess(String statusCode, String responseServer) {
                try {
                    mBusyDialog.dismis();

                    Log.w("responseServer", responseServer);
                    JSONObject mJsonObject = new JSONObject(responseServer);
                    if (mJsonObject.getBoolean("success")) {

                        if (limit.equalsIgnoreCase("0")) {
                            JSONArray jsonArray = mJsonObject.getJSONArray("banner");
                            GsonBuilder builder = new GsonBuilder();
                            Gson mGson = builder.create();
                            List<BannerList> postsBannerList = new ArrayList<BannerList>();
                            postsBannerList = Arrays.asList(mGson.fromJson(jsonArray.toString(), BannerList[].class));
                            ArrayList<BannerList> allLists = new ArrayList<BannerList>(postsBannerList);

                            mSliderAdapter.addAdvertisementList(allLists);
                            if (allLists.size() == 0) {
                                headeAdvertisemnetLayout.setVisibility(View.GONE);
                            } else {
                                headeAdvertisemnetLayout.setVisibility(View.VISIBLE);

                            }

                        }
                        JSONArray jsonArray = mJsonObject.getJSONArray("data");
                        GsonBuilder builder = new GsonBuilder();
                        Gson mGson = builder.create();
                        List<FeedList> posts = new ArrayList<FeedList>();
                        posts = Arrays.asList(mGson.fromJson(jsonArray.toString(), FeedList[].class));
                        ArrayList<FeedList> allLists = new ArrayList<FeedList>(posts);
                        if (limit.equalsIgnoreCase("0")) {
                            mFeedListAdapter.removeAllData();
                        }
                        mFeedListAdapter.addAllList(allLists);
                        startTherHandelar();
                        mFeedListAdapter.notifyDataSetChanged();
                    }
                } catch (Exception e) {
                    mBusyDialog.dismis();
                }
            }

            @Override
            public void onFailed(String statusCode, String serverResponse) {
                mBusyDialog.dismis();
                headeAdvertisemnetLayout.setVisibility(View.GONE);
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
        AlertDialog.Builder builder = new AlertDialog.Builder(HomeActivity.this);
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
        FeedList mFeedList = mFeedListAdapter.getModelAt(position);

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
                        mFeedListAdapter.remvoeList(position);

                    } else {
                        String message = mJsonObject.getString("message");
                        ToastHelper.showToast(mContext, message);
                    }
                } catch (Exception e) {

                    mBusyDialog.dismis();

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

    private Handler mHandler = new Handler();
    public void startTherHandelar() {
        mHandler.removeCallbacks(mUpdateTimeTask);
        mHandler.postDelayed(mUpdateTimeTask, 10);
    }

    private Runnable mUpdateTimeTask = new Runnable() {
        public void run() {
            try {
                if (pager != null) {
                    int currentPostion = pager.getCurrentItem();
                    int totalPage = pager.getAdapter().getCount() - 1;
                    if (currentPostion >= totalPage) {
                        pager.setCurrentItem(0);
                    } else {
                        currentPostion = currentPostion + 1;
                        pager.setCurrentItem(currentPostion);
                    }
                }
            }
            catch (Exception ex){}
            mHandler.postDelayed(this, 2000);
        }
    };

}
