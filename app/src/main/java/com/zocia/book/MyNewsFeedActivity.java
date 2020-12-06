package com.zocia.book;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.LinearLayout;
import android.widget.TextView;

import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.viewpager2.widget.ViewPager2;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.zocia.book.adapter.NewsFragmentAdapter;
import com.zocia.book.adapter.NewsFragmentViewpage2Adapter;
import com.zocia.book.callbackinterface.ServerResponse;
import com.zocia.book.fragment.MyNewsFragment;
import com.zocia.book.fragment.NewsFragment;
import com.zocia.book.model.FeedList;
import com.zocia.book.model.News;
import com.zocia.book.myapplication.Myapplication;
import com.zocia.book.networkcalls.ServerCallsProvider;
import com.zocia.book.utils.AllUrls;
import com.zocia.book.utils.BusyDialog;
import com.zocia.book.utils.Helpers;
import com.zocia.book.utils.PersistentUser;
import com.zocia.book.utils.SliderTransformer;
import com.zocia.book.utils.SliderTransformerViewpage2;

import org.json.JSONArray;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;

public class MyNewsFeedActivity extends BaseActivity {
    private static final String TAG = "NewsActivity";
    public static NewsFragmentViewpage2Adapter adapter;
    public String screen;
    List<Fragment> list = new ArrayList<>();
    private Context mContext;
    private BusyDialog mBusyDialog;
    private ViewPager2 viewPager2;
    private TextView mypost;

    public static void refreshdata() {
        adapter.notifyDataSetChanged();
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        getLayoutInflater().inflate(R.layout.activity_mynewsfeed, frameLayout);
        Myapplication.selection = 5;
        selectedDeselectedLayut();
        titile.setText("REFLECTIONS");

        mContext = this;
        viewPager2 = findViewById(R.id.pager);
        mypost = findViewById(R.id.mypost);
        mypost.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(mContext, MyPostActivity.class);
                intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP | Intent.FLAG_ACTIVITY_NEW_TASK);
                startActivity(intent);
            }
        });
//        LinearLayout fabbuton=findViewById(R.id.fabbutton);
//        fabbuton.setOnClickListener(new View.OnClickListener() {
//            @Override
//            public void onClick(View view) {
//                Intent intent=new Intent(MyNewsFeedActivity.this,AddNewsActivity.class);
//                intent.putExtra("screen",getIntent().getStringExtra("screen"));
//                startActivityForResult(intent,1);
//            }
//        });
//        Fragment fragment= NewsFragment.newInstance("Happy Diwali 2020: Songs That Will Light Up Your Celebrations","November 14, 2020","The festival of Diwali is a time for joyous celebrations and meeting friends and family. This year, Diwali is being celebrated on Saturday, November 14. Diwali or Deepavali is a festival on which people perform poojas, catch up with their relatives, celebrate with card parties and indulge in sweets treats. With diyas lighting up every home and markets bustling with people - this time everyone practicing social distancing amid the pandemic - it's a festival that brings people together. \n" +
//                "Diwali is also a time for parties and gatherings, both big and small. It's a time when people light up their homes with candles, diyas and fairy lights, and remember friends by sending them Diwali wishes. And like any other celebration, the festival of Diwali is also incomplete without some good music. ");
//        Fragment fragment1= NewsFragment.newInstance("Coronavirus India Live Updates: India records nearly 45,000 new cases","November 14, 2020","India recorded 44,684 new cases of Covid-19 in the 24 hours ending 9 am Saturday. The country’s total tally currently stands at over 87.73 lakh cases. With 520 people succumbing to the virus Friday the death toll stands at 1,29,188. Out of the total cases, 4.80 lakh cases are active while 81.63 lakh people have recovered after testing positive.\n" +
//                "\n" +
//                "With over 7,800 cases, Delhi continues to the biggest contributor in the country’s total caseload. Haryana is in the midst of a surge and has become the fastest growing state after Delhi and Kerala. The state recorded nearly 2,700 cases on Friday.\n" +
//                "\n" +
//                "With Arvind Kejriwal Friday expressing concerns about the coronavirus surge in the national capital, the Delhi government is likely to put several Covid-19 prevention measures in place after Diwali –ensuring the strict implementation of social distancing and use of face coverings. On account of the winter season and the large influx of patients from festive gatherings, Delhi could experience about 15,000 cases in a day soon, a report by National Centre for Disease Control noted.");
//        Fragment fragment2= NewsFragment.newInstance("'Time will tell': Trump comes closest yet to admitting defeat","November 14, 2020","President Donald Trump says he refuses to have another lockdown as coronavirus cases surge across the country, but suggested one could be in the offing should he lose his legal challenges to overcome his election loss to President-elect Joe Biden. \"This administration will not be going to a lockdown,\" he said. \"Hopefully whatever happens in the future, who knows, which administration it will be I guess time will tell, but I can tell you this administration will not go to a lockdown.\"");
//        list.add(fragment);
//        list.add(fragment1);
//        list.add(fragment2);

        ServerRequest("0");
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
        String url = "";

        url = AllUrls.BASEURL + "userNews";


        ServerCallsProvider.volleyPostRequest(url, allHashMap, allHashMapHeader, TAG, new ServerResponse() {
            @Override
            public void onSuccess(String statusCode, String responseServer) {
                try {
                    mBusyDialog.dismis();

                    Log.w("responseServer", responseServer);
                    JSONObject mJsonObject = new JSONObject(responseServer);
                    if (mJsonObject.getBoolean("success")) {

                        if (limit.equalsIgnoreCase("0")) {
                            JSONArray jsonArray = mJsonObject.getJSONArray("data");
                            GsonBuilder builder = new GsonBuilder();
                            Gson mGson = builder.create();
                            List<News> postsBannerList = new ArrayList<News>();
                            postsBannerList = Arrays.asList(mGson.fromJson(jsonArray.toString(), News[].class));
//                            for (int i=0;i<postsBannerList.size();i++){
//                                MyNewsFragment fragment=MyNewsFragment.newInstance(postsBannerList.get(i).getHeading(),postsBannerList.get(i).getPostTime(),postsBannerList.get(i).getDetails(),postsBannerList.get(i).getCommentCount(),String.valueOf(postsBannerList.get(i).getId()),postsBannerList.get(i).getUserId(),i);
//                            list.add(fragment);
//                            }
                            for (int i = 0; i < jsonArray.length(); i++) {
                                JSONObject jsonObject = jsonArray.getJSONObject(i);
                                MyNewsFragment fragment = MyNewsFragment.newInstance(jsonObject.getString("heading"), jsonObject.getString("post_time"), jsonObject.getString("details"), jsonObject.getString("comment_count"), String.valueOf(jsonObject.getInt("id")), jsonObject.getString("user_id"), i);
                                list.add(fragment);
                            }
                            adapter = new NewsFragmentViewpage2Adapter(MyNewsFeedActivity.this, list);

                            viewPager2.setAdapter(adapter);
                            if (getIntent().hasExtra("position")) {
                                viewPager2.postDelayed(new Runnable() {

                                    @Override
                                    public void run() {
                                        viewPager2.setCurrentItem(Integer.valueOf(getIntent().getIntExtra("position", 0)), false);
                                    }
                                }, 100);
                            }
//                            viewPager2.setCurrentItem(Integer.valueOf(getIntent().getIntExtra("position", 0)), false);

                            viewPager2.setOffscreenPageLimit(3);
                            viewPager2.setPageTransformer(new SliderTransformerViewpage2(3));
                        }
                        JSONArray jsonArray = mJsonObject.getJSONArray("data");
                        GsonBuilder builder = new GsonBuilder();
                        Gson mGson = builder.create();
                        List<FeedList> posts = new ArrayList<FeedList>();
                        posts = Arrays.asList(mGson.fromJson(jsonArray.toString(), FeedList[].class));
                        ArrayList<FeedList> allLists = new ArrayList<FeedList>(posts);
//                        if(limit.equalsIgnoreCase("0")){
//                            mFeedListAdapter.removeAllData();
//                        }
//                        mFeedListAdapter.addAllList(allLists);
//                        startTherHandelar();
//                        mFeedListAdapter.notifyDataSetChanged();
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

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
//        adapter.notifyDataSetChanged();
        adapter.notifyDataSetChanged();
        ServerRequest("0");

    }
}