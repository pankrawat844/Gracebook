package com.zocia.book;

import android.content.Context;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.ImageView;
import android.widget.LinearLayout;

import androidx.appcompat.app.AppCompatActivity;

import com.bogdwellers.pinchtozoom.ImageMatrixTouchHandler;
import com.bumptech.glide.Glide;
import com.zocia.book.callbackinterface.ServerResponse;
import com.zocia.book.myapplication.Myapplication;
import com.zocia.book.networkcalls.ServerCallsProvider;
import com.zocia.book.utils.AllUrls;
import com.zocia.book.utils.BusyDialog;
import com.zocia.book.utils.ConstantFunctions;
import com.zocia.book.utils.DateUtility;
import com.zocia.book.utils.Helpers;
import com.zocia.book.utils.Logger;
import com.zocia.book.utils.PersistentUser;

import org.json.JSONObject;

import java.util.HashMap;

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
//                ConstantFunctions.loadImageNomal(mMywallpaperList, imagedummyverse);
//                imagedummyverse.setOnTouchListener(new ImageMatrixTouchHandler(mContext));
                Glide.with(Myapplication.getContext()).asBitmap()
                        .load(mMywallpaperList)
                        .error(R.drawable.user_icon)
                        .into(imagedummyverse);
            }
        } catch (Exception e) {
            Logger.debugLog("Exception", e.getMessage());

        }


    }

}
