package com.zocia.book;

import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.util.Log;
import android.view.View;

import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.SwitchCompat;

import com.zocia.book.callbackinterface.ServerResponse;
import com.zocia.book.networkcalls.ServerCallsProvider;
import com.zocia.book.utils.AllUrls;
import com.zocia.book.utils.BusyDialog;
import com.zocia.book.utils.Helpers;
import com.zocia.book.utils.Logger;
import com.zocia.book.utils.PersistentUser;

import org.json.JSONObject;

import java.util.HashMap;

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

        myswitch.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                HashMap<String, String> allHashMap = new HashMap<>();
                if (myswitch.isChecked()) {
                    allHashMap.put("isnotification", "1");
                } else {
                    allHashMap.put("isnotification", "0");
                }
                ServerRequest(allHashMap);
            }
        });
        findViewById(R.id.layoutRateUs).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(Intent.ACTION_VIEW);
                intent.setData(Uri.parse("market://details?id=com.zocia.book"));
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
        Logger.debugLog("allHashMap", allHashMap.toString());

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
                        PersistentUser.setUserDetails(mContext, responseServer);

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
