package com.grace.book;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.util.Log;
import android.view.View;
import android.widget.ImageView;
import android.widget.LinearLayout;

import androidx.appcompat.app.AppCompatActivity;

import com.bogdwellers.pinchtozoom.ImageMatrixTouchHandler;
import com.bumptech.glide.Glide;
import com.google.android.gms.common.util.DataUtils;
import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.grace.book.callbackinterface.ServerResponse;
import com.grace.book.model.BannerList;
import com.grace.book.model.FeedList;
import com.grace.book.networkcalls.ServerCallsProvider;
import com.grace.book.utils.AllUrls;
import com.grace.book.utils.BusyDialog;
import com.grace.book.utils.ConstantFunctions;
import com.grace.book.utils.DateUtility;
import com.grace.book.utils.Helpers;
import com.grace.book.utils.Logger;
import com.grace.book.utils.PersistentUser;

import org.json.JSONArray;
import org.json.JSONObject;

import java.io.File;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;

public class DailyverseActivity extends AppCompatActivity {
    private static final String TAG = DailyverseActivity.class.getSimpleName();
    private Context mContext;
    private LinearLayout layoutBack;
    private ImageView imagedummyverse;
    private BusyDialog mBusyDialog;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_dailyverse);
        mContext = this;
        initUi();
    }

    private void initUi() {
        imagedummyverse = (ImageView) this.findViewById(R.id.imagedummyverse);
        String getCurrentdayFor = DateUtility.getCurrentdayFor();
        if (PersistentUser.getVerseDate(mContext).equalsIgnoreCase(getCurrentdayFor)) {
            if (PersistentUser.getVerse(mContext).equalsIgnoreCase("")) {
                ServerRequest();
            } else {
                showData();
            }
        } else {
            ServerRequest();
        }

        layoutBack = (LinearLayout) this.findViewById(R.id.layoutBack);
        layoutBack.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                finish();
            }
        });
    }

    private void ServerRequest() {
        if (!Helpers.isNetworkAvailable(mContext)) {
            Helpers.showOkayDialog(mContext, R.string.no_internet_connection);
            return;
        }
        mBusyDialog = new BusyDialog(mContext);
        mBusyDialog.show();

        HashMap<String, String> allHashMap = new HashMap<>();
        HashMap<String, String> allHashMapHeader = new HashMap<>();
        allHashMapHeader.put("appKey", AllUrls.APP_KEY);
        allHashMapHeader.put("authToken", PersistentUser.getUserToken(mContext));
        final String url = AllUrls.BASEURL + "versesData";
        ServerCallsProvider.volleyPostRequest(url, allHashMap, allHashMapHeader, TAG, new ServerResponse() {
            @Override
            public void onSuccess(String statusCode, String responseServer) {
                try {
                    mBusyDialog.dismis();
                    Logger.debugLog("responseServer", responseServer);
                    JSONObject mJsonObject = new JSONObject(responseServer);
                    if (mJsonObject.getBoolean("success")) {
                        PersistentUser.setVerse(mContext, responseServer);
                        String getCurrentdayFor = DateUtility.getCurrentdayFor();
                        PersistentUser.setVerseDate(mContext, getCurrentdayFor);
                        showData();

                    }
                } catch (Exception e) {
                    Logger.debugLog("Exception", e.getMessage());

                }
            }

            @Override
            public void onFailed(String statusCode, String serverResponse) {
                mBusyDialog.dismis();

            }
        });
    }

    public void showData() {
        try {
            JSONObject mJsonObject = new JSONObject(PersistentUser.getVerse(mContext));
            if (mJsonObject.getBoolean("success")) {
                JSONObject data = mJsonObject.getJSONObject("data");
                String mMywallpaperList = data.getString("image");
                Log.e("mMywallpaperList", mMywallpaperList);
                ConstantFunctions.loadImageNomal(mMywallpaperList, imagedummyverse);
                imagedummyverse.setOnTouchListener(new ImageMatrixTouchHandler(mContext));

            }
        } catch (Exception e) {
            Logger.debugLog("Exception", e.getMessage());

        }


    }

}
