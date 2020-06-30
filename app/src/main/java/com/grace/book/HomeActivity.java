package com.grace.book;

import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import androidx.viewpager.widget.ViewPager;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.RelativeLayout;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.grace.book.adapter.FeedListAdapter;
import com.grace.book.adapter.HomeadvertismentAdapter;
import com.grace.book.callbackinterface.FilterItemCallback;
import com.grace.book.callbackinterface.ServerResponse;
import com.grace.book.customview.VerticalSpaceItemDecoration;
import com.grace.book.model.BannerList;
import com.grace.book.model.FeedList;
import com.grace.book.myapplication.Myapplication;
import com.grace.book.networkcalls.ServerCallsProvider;
import com.grace.book.pageIndicator.CirclePageIndicator;
import com.grace.book.utils.AllUrls;
import com.grace.book.utils.Helpers;
import com.grace.book.utils.Logger;
import com.grace.book.utils.PersistentUser;
import com.headerfooter.songhang.library.SmartRecyclerAdapter;

import org.json.JSONArray;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;

import static com.grace.book.myapplication.Myapplication.getContext;

public class HomeActivity extends BaseActivity {
    private static final String TAG = HomeActivity.class.getSimpleName();
    private final int VERTICAL_ITEM_SPACE = 20;
    private RecyclerView recycler_feed;
    private FeedListAdapter mFeedListAdapter;
    private Context mContext;
    private ViewPager pager;
    private CirclePageIndicator viewpager_indicator;
    private HomeadvertismentAdapter mSliderAdapter;

    private RelativeLayout headeAdvertisemnetLayout;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        getLayoutInflater().inflate(R.layout.activity_home, frameLayout);
        Myapplication.selection = 0;
        mContext = this;
        selectedDeselectedLayut();
        initUI();
    }

    private void initUI() {
        recycler_feed = (RecyclerView) this.findViewById(R.id.recycler_feed);
        mFeedListAdapter = new FeedListAdapter(mContext, new ArrayList<FeedList>());
        LinearLayoutManager mLinearLayoutManager = new LinearLayoutManager(mContext, RecyclerView.VERTICAL, false);
        recycler_feed.setLayoutManager(mLinearLayoutManager);
        recycler_feed.addItemDecoration(new VerticalSpaceItemDecoration(VERTICAL_ITEM_SPACE));
        SmartRecyclerAdapter smartRecyclerAdapter = new SmartRecyclerAdapter(mFeedListAdapter);
        LayoutInflater inflater = (LayoutInflater) getContext().getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        View view = inflater.inflate(R.layout.header_home_screen, null);
        headeAdvertisemnetLayout = (RelativeLayout) view.findViewById(R.id.headeAdvertisemnetLayout);
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

        EditText edittextHomePost = (EditText) view.findViewById(R.id.edittextHomePost);
        edittextHomePost.setFocusable(false);
        edittextHomePost.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent mIntent = new Intent(HomeActivity.this, PostallActivity.class);
                mIntent.putExtra("screenType", 0);
                startActivity(mIntent);
            }
        });
        mFeedListAdapter.addClickListiner(lFilterItemCallback);
        ServerRequest("0");
    }

    public FilterItemCallback lFilterItemCallback = new FilterItemCallback() {
        @Override
        public void ClickFilterItemCallback(int type, int position) {
            if (type == 0) {
                Intent mm = new Intent(HomeActivity.this, UserprofileActivity.class);
                startActivity(mm);
            } else if (type == 1) {
                Intent mm = new Intent(HomeActivity.this, CommentActivity.class);
                startActivity(mm);
            }
        }
    };

    private void ServerRequest(final String limit) {
        if (!Helpers.isNetworkAvailable(mContext)) {
            Helpers.showOkayDialog(mContext, R.string.no_internet_connection);
            return;
        }
        HashMap<String, String> allHashMap = new HashMap<>();
        allHashMap.put("limit", limit);
        HashMap<String, String> allHashMapHeader = new HashMap<>();
        allHashMapHeader.put("appKey", AllUrls.APP_KEY);
        allHashMapHeader.put("authToken", PersistentUser.getUserToken(mContext));
        final String url = AllUrls.BASEURL + "homePost";
        ServerCallsProvider.volleyPostRequest(url, allHashMap, allHashMapHeader, TAG, new ServerResponse() {
            @Override
            public void onSuccess(String statusCode, String responseServer) {
                try {
                    Logger.debugLog("responseServer", responseServer);
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
                        mFeedListAdapter.addAllList(allLists);


                    }
                } catch (Exception e) {
                    Logger.debugLog("Exception", e.getMessage());

                }
            }

            @Override
            public void onFailed(String statusCode, String serverResponse) {
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
}
