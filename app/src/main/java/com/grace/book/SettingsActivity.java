package com.grace.book;

import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.os.Handler;
import android.util.Log;
import android.view.MotionEvent;
import android.view.View;
import android.widget.LinearLayout;

import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.SwitchCompat;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.grace.book.adapter.MyPostListAdapter;
import com.grace.book.callbackinterface.ServerResponse;
import com.grace.book.customview.VerticalSpaceItemDecoration;
import com.grace.book.model.BannerList;
import com.grace.book.model.FeedList;
import com.grace.book.networkcalls.ServerCallsProvider;
import com.grace.book.utils.AllUrls;
import com.grace.book.utils.BusyDialog;
import com.grace.book.utils.ConstantFunctions;
import com.grace.book.utils.Helpers;
import com.grace.book.utils.Logger;
import com.grace.book.utils.PersistentUser;

import org.json.JSONArray;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;

public class SettingsActivity extends AppCompatActivity {
    private static final String TAG = SettingsActivity.class.getSimpleName();
    private Context mContext;
    private SwitchCompat myswitch;
    private BusyDialog mBusyDialog;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_settings);
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
        findViewById(R.id.layoutEditprofile).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent mm = new Intent(SettingsActivity.this, EditProfileActivity.class);
                startActivity(mm);
            }
        });
        myswitch = (SwitchCompat) this.findViewById(R.id.myswitch);
        userDataList();
        myswitch.setOnTouchListener(new View.OnTouchListener() {
            @Override
            public boolean onTouch(View view, MotionEvent motionEvent) {
                HashMap<String, String> allHashMap = new HashMap<>();
                if (myswitch.isChecked()) {
                    allHashMap.put("isnotification", "1");
                } else {
                    allHashMap.put("isnotification", "0");
                }
                ServerRequest(allHashMap);
                return false;
            }
        });
        findViewById(R.id.layoutRateUs).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(Intent.ACTION_VIEW);
                intent.setData(Uri.parse("market://details?id=com.grace.book"));
                startActivity(intent);
            }
        });
        findViewById(R.id.layoutContactUs).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent emailIntent = new Intent(Intent.ACTION_SENDTO, Uri.fromParts(
                        "mailto", "gracebuk2020@gmail.com", null));
                emailIntent.putExtra(Intent.EXTRA_SUBJECT, "Contact Us");
                emailIntent.putExtra(Intent.EXTRA_TEXT, "");
                startActivity(Intent.createChooser(emailIntent, "Send email..."));
            }
        });

    }

    public void userDataList() {
        try {

            JSONObject mJsonObject = new JSONObject(PersistentUser.getUserDetails(mContext));
            if (mJsonObject.getBoolean("success")) {

                JSONObject data = mJsonObject.getJSONObject("data");
                String isnotification = data.getString("isnotification");
                if (isnotification.equalsIgnoreCase("1")) {
                    myswitch.setChecked(true);
                } else {
                    myswitch.setChecked(false);
                }


            }
        } catch (Exception ex) {
            Log.e("Exception", ex.getMessage());

        }

    }

    @Override
    protected void onResume() {
        super.onResume();
        userDataList();
    }

    private void ServerRequest(HashMap<String, String> allHashMap) {
        if (!Helpers.isNetworkAvailable(mContext)) {
            Helpers.showOkayDialog(mContext, R.string.no_internet_connection);
            return;
        }
        HashMap<String, String> allHashMapHeader = new HashMap<>();
        allHashMapHeader.put("appKey", AllUrls.APP_KEY);
        allHashMapHeader.put("authToken", PersistentUser.getUserToken(mContext));
        final String url = AllUrls.BASEURL + "updateMobileUser";
        ServerCallsProvider.volleyPostRequest(url, allHashMap, allHashMapHeader, TAG, new ServerResponse() {
            @Override
            public void onSuccess(String statusCode, String responseServer) {
                try {
                    Logger.debugLog("responseServer", responseServer);
                    JSONObject mJsonObject = new JSONObject(responseServer);
                    if (mJsonObject.getBoolean("success")) {


                    }
                } catch (Exception e) {
                    Logger.debugLog("Exception", e.getMessage());

                }
            }

            @Override
            public void onFailed(String statusCode, String serverResponse) {

            }
        });
    }
}
