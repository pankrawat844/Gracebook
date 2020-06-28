package com.grace.book;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.EditText;

import androidx.appcompat.app.AppCompatActivity;

import com.grace.book.callbackinterface.ServerResponse;
import com.grace.book.networkcalls.ServerCallsProvider;
import com.grace.book.utils.AllUrls;
import com.grace.book.utils.BusyDialog;
import com.grace.book.utils.Helpers;
import com.grace.book.utils.Logger;
import com.grace.book.utils.ToastHelper;

import org.json.JSONObject;

import java.util.HashMap;

public class PasswordresetActivity extends AppCompatActivity {
    private static final String TAG = PasswordresetActivity.class.getSimpleName();
    private String auth_token = "";
    private Context mContext;
    private BusyDialog mBusyDialog;
    private EditText edittextPassword;
    private EditText editConfirmpassword;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_passwordreset);
        mContext = this;
        auth_token = getIntent().getStringExtra("auth_token");
        initUi();
    }

    private void initUi() {
        findViewById(R.id.tvSubmit).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                vaidation();
            }
        });
        edittextPassword = (EditText) this.findViewById(R.id.edittextPassword);
        editConfirmpassword = (EditText) this.findViewById(R.id.editConfirmpassword);

    }

    public void vaidation() {
        String password = edittextPassword.getText().toString().trim();
        String confirmpassword = editConfirmpassword.getText().toString().trim();

        if (password.equalsIgnoreCase("")) {
            edittextPassword.setError("Enter password");
            return;
        } else if (confirmpassword.equalsIgnoreCase("")) {
            editConfirmpassword.setError("Enter confirm password");
            return;
        } else if (confirmpassword.equalsIgnoreCase(password)) {
            editConfirmpassword.setError("Enter correct password");
            return;
        } else {
            HashMap<String, String> allHashMap = new HashMap<>();
            allHashMap.put("password", password);
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
        allHashMapHeader.put("authToken",auth_token);
        mBusyDialog = new BusyDialog(mContext);
        mBusyDialog.show();
        final String url = AllUrls.BASEURL + "resetPassword";
        ServerCallsProvider.volleyPostRequest(url, allHashMap, allHashMapHeader, TAG, new ServerResponse() {
            @Override
            public void onSuccess(String statusCode, String responseServer) {
                mBusyDialog.dismis();
                try {
                    Logger.debugLog("responseServer", responseServer);
                    JSONObject mJsonObject = new JSONObject(responseServer);
                    if (mJsonObject.getBoolean("success")) {
                        ToastHelper.showToast(mContext,"Password reset successfully");
                        Intent intent = new Intent(PasswordresetActivity.this, LoginActivity.class);
                        intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP | Intent.FLAG_ACTIVITY_NEW_TASK);
                        startActivity(intent);
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
