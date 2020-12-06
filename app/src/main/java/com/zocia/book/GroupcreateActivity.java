package com.zocia.book;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.EditText;

import androidx.appcompat.app.AppCompatActivity;

import com.zocia.book.callbackinterface.ServerResponse;
import com.zocia.book.networkcalls.ServerCallsProvider;
import com.zocia.book.utils.AllUrls;
import com.zocia.book.utils.BusyDialog;
import com.zocia.book.utils.Helpers;
import com.zocia.book.utils.Logger;
import com.zocia.book.utils.PersistentUser;
import com.zocia.book.utils.ToastHelper;

import org.json.JSONObject;

import java.util.HashMap;

public class GroupcreateActivity extends AppCompatActivity {
    private static final String TAG = GroupcreateActivity.class.getSimpleName();
    private Context mContext;
    private BusyDialog mBusyDialog;
    private EditText editGroupNmae;
    private EditText editGroupDetails;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_create_group);
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
        findViewById(R.id.layoutAddGroup).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                validation();
            }
        });
        editGroupNmae = (EditText) this.findViewById(R.id.editGroupNmae);
        editGroupDetails = (EditText) this.findViewById(R.id.editGroupDetails);

    }

    public void validation() {
        String group_name = editGroupNmae.getText().toString().trim();
        String group_details = editGroupDetails.getText().toString().trim();
        if (group_name.equalsIgnoreCase("")) {

            ToastHelper.showToast(mContext, "Enter group name");
            return;
        } else if (group_name.equalsIgnoreCase("")) {
            ToastHelper.showToast(mContext, "Enter group details");
            return;
        } else {
            HashMap<String, String> allHashMap = new HashMap<>();
            allHashMap.put("group_name", group_name);
            allHashMap.put("group_details", group_details);
            forgorpasswordServerRequest(allHashMap);
        }


    }

    private void forgorpasswordServerRequest(HashMap<String, String> allHashMap) {
        if (!Helpers.isNetworkAvailable(mContext)) {
            Helpers.showOkayDialog(mContext, R.string.no_internet_connection);
            return;
        }
        HashMap<String, String> allHashMapHeader = new HashMap<>();
        allHashMapHeader.put("appKey", AllUrls.APP_KEY);
        allHashMapHeader.put("authToken", PersistentUser.getUserToken(mContext));
        mBusyDialog = new BusyDialog(mContext);
        mBusyDialog.show();
        final String url = AllUrls.BASEURL + "groupAdd";
        ServerCallsProvider.volleyPostRequest(url, allHashMap, allHashMapHeader, TAG, new ServerResponse() {
            @Override
            public void onSuccess(String statusCode, String responseServer) {
                mBusyDialog.dismis();
                try {
                    Logger.debugLog("responseServer", responseServer);
                    JSONObject mJsonObject = new JSONObject(responseServer);
                    if (mJsonObject.getBoolean("success")) {

                        Intent mIntent = getIntent();
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
}
