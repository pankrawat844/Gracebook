package com.grace.book;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;

import androidx.appcompat.app.AppCompatActivity;

import com.grace.book.callbackinterface.ServerResponse;
import com.grace.book.networkcalls.ServerCallsProvider;
import com.grace.book.utils.AllUrls;
import com.grace.book.utils.BusyDialog;
import com.grace.book.utils.Helpers;
import com.grace.book.utils.Logger;
import com.grace.book.utils.ToastHelper;
import com.poovam.pinedittextfield.LinePinField;

import org.json.JSONObject;

import java.util.HashMap;

public class VerificationActivity extends AppCompatActivity {
    private static final String TAG = VerificationActivity.class.getSimpleName();
    private int screen = 0;
    private String otp_code = "", country_code = "", phone = "";
    private LinePinField lineField;
    private Context mContext;
    private BusyDialog mBusyDialog;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_verification);
        screen = getIntent().getIntExtra("screen", 0);
        otp_code = getIntent().getStringExtra("otp_code");
        phone = getIntent().getStringExtra("phone");
        country_code = getIntent().getStringExtra("country_code");
        mContext = this;
        initUi();
    }

    private void initUi() {
        lineField = (LinePinField) this.findViewById(R.id.lineField);
        lineField.setText(otp_code);
        findViewById(R.id.Submit).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                vaidation();
            }
        });
        findViewById(R.id.tvResend).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                HashMap<String, String> allHashMap = new HashMap<>();
                allHashMap.put("phone", phone);
                allHashMap.put("country_code", country_code);
                resendServerRequest(allHashMap);
            }
        });
    }

    public void vaidation() {
        String otp_code = lineField.getText().toString();
        if (otp_code.equalsIgnoreCase("")) {
            ToastHelper.showToast(mContext, "Ener pin code");
            return;
        } else {
            HashMap<String, String> allHashMap = new HashMap<>();
            allHashMap.put("phone", phone);
            allHashMap.put("country_code", country_code);
            allHashMap.put("otp_code", otp_code);
            sigupServerRequest(allHashMap);
        }

    }

    private void sigupServerRequest(HashMap<String, String> allHashMap) {
        if (!Helpers.isNetworkAvailable(mContext)) {
            Helpers.showOkayDialog(mContext, R.string.no_internet_connection);
            return;
        }
        HashMap<String, String> allHashMapHeader = new HashMap<>();
        allHashMapHeader.put("appKey", AllUrls.APP_KEY);
        mBusyDialog = new BusyDialog(mContext);
        mBusyDialog.show();
        final String url = AllUrls.BASEURL + "verification";
        ServerCallsProvider.volleyPostRequest(url, allHashMap, allHashMapHeader, TAG, new ServerResponse() {
            @Override
            public void onSuccess(String statusCode, String responseServer) {
                mBusyDialog.dismis();
                try {
                    Logger.debugLog("responseServer", responseServer);
                    JSONObject mJsonObject = new JSONObject(responseServer);
                    if (mJsonObject.getBoolean("success")) {

                        if (screen == 0) {
                            ToastHelper.showToast(mContext,"Account varification successfully");
                            Intent mIntent = new Intent(VerificationActivity.this, LoginActivity.class);
                            mIntent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP | Intent.FLAG_ACTIVITY_NEW_TASK);
                            startActivity(mIntent);
                        } else {
                            JSONObject data=mJsonObject.getJSONObject("data");
                            String auth_token= data.getString("auth_token");
                            Intent mIntent = new Intent(VerificationActivity.this, PasswordresetActivity.class);
                            mIntent.putExtra("auth_token",auth_token);
                            startActivity(mIntent);
                        }

                    }
                } catch (Exception e) {

                }
            }

            @Override
            public void onFailed(String statusCode, String serverResponse) {
                mBusyDialog.dismis();
                ToastHelper.showToast(mContext, serverResponse);
                Logger.debugLog("onFailed serverResponse", serverResponse);
            }
        });
    }
    private void resendServerRequest(HashMap<String, String> allHashMap) {
        if (!Helpers.isNetworkAvailable(mContext)) {
            Helpers.showOkayDialog(mContext, R.string.no_internet_connection);
            return;
        }
        HashMap<String, String> allHashMapHeader = new HashMap<>();
        allHashMapHeader.put("appKey", AllUrls.APP_KEY);
        mBusyDialog = new BusyDialog(mContext);
        mBusyDialog.show();
        final String url = AllUrls.BASEURL + "verification";
        ServerCallsProvider.volleyPostRequest(url, allHashMap, allHashMapHeader, TAG, new ServerResponse() {
            @Override
            public void onSuccess(String statusCode, String responseServer) {
                mBusyDialog.dismis();
                try {
                    Logger.debugLog("responseServer", responseServer);
                    JSONObject mJsonObject = new JSONObject(responseServer);
                    if (mJsonObject.getBoolean("success")) {
                        ToastHelper.showToast(mContext,"Otp code send successfully");
                        JSONObject data = mJsonObject.getJSONObject("data");
                        String otp_code =data.getString("otp_code");
                        lineField.setText(otp_code);

                    }
                    else {
                        String message= mJsonObject.getString("message");
                        ToastHelper.showToast(mContext,message);

                    }
                } catch (Exception e) {

                }
            }

            @Override
            public void onFailed(String statusCode, String serverResponse) {
                mBusyDialog.dismis();
                ToastHelper.showToast(mContext, serverResponse);
                Logger.debugLog("onFailed serverResponse", serverResponse);
            }
        });
    }
}
