package com.grace.book;

import android.Manifest;
import android.annotation.SuppressLint;
import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.content.IntentSender;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import android.content.pm.Signature;
import android.graphics.drawable.ColorDrawable;
import android.os.Build;
import android.os.Bundle;
import android.os.Looper;
import android.util.Base64;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;

import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.api.ApiException;
import com.google.android.gms.common.api.GoogleApiClient;
import com.google.android.gms.common.api.ResolvableApiException;
import com.google.android.gms.location.FusedLocationProviderClient;
import com.google.android.gms.location.LocationCallback;
import com.google.android.gms.location.LocationRequest;
import com.google.android.gms.location.LocationResult;
import com.google.android.gms.location.LocationServices;
import com.google.android.gms.location.LocationSettingsRequest;
import com.google.android.gms.location.LocationSettingsResponse;
import com.google.android.gms.location.LocationSettingsStatusCodes;
import com.google.android.gms.location.SettingsClient;
import com.google.android.gms.tasks.OnCanceledListener;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.iid.FirebaseInstanceId;
import com.google.firebase.iid.InstanceIdResult;
import com.grace.book.callbackinterface.ServerResponse;
import com.grace.book.networkcalls.ServerCallsProvider;
import com.grace.book.utils.AllUrls;
import com.grace.book.utils.BusyDialog;
import com.grace.book.utils.Helpers;
import com.grace.book.utils.Logger;
import com.grace.book.utils.PersistentUser;
import com.grace.book.utils.ToastHelper;
import com.grace.book.utils.ValidateEmail;
import com.hbb20.CountryCodePicker;

import org.json.JSONObject;

import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

public class LoginActivity extends AppCompatActivity {
    private static final String TAG = LoginActivity.class.getSimpleName();
    private Context mContext;
    private BusyDialog mBusyDialog;
    private EditText editpassword;
    private EditText edittextEMail;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);
        mContext = this;
        FirebaseInstanceId.getInstance().getInstanceId()
                .addOnCompleteListener(new OnCompleteListener<InstanceIdResult>() {
                    @Override
                    public void onComplete(@NonNull Task<InstanceIdResult> task) {
                        if (!task.isSuccessful()) {
                            return;
                        }
                        String token = task.getResult().getToken();
                        PersistentUser.setPushToken(mContext, token);
                        Log.e("token", token);
                    }
                });

        initUi();
    }
    private void initUi() {
        edittextEMail = (EditText) this.findViewById(R.id.edittextEMail);
        editpassword = (EditText) this.findViewById(R.id.editpassword);

        findViewById(R.id.tvForgotpassword).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                showDialogForVideo();
            }
        });
        findViewById(R.id.tvLoginSubmit).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                vaidation();
            }
        });
        edittextEMail.setText("prosanto.mbstu@gmail.com");
        editpassword.setText("123");
    }

    public void vaidation() {



        String email = edittextEMail.getText().toString().trim();
        String password = editpassword.getText().toString().trim();

        if (email.equalsIgnoreCase("")) {
            edittextEMail.setError("Enter Email");
            return;
        } else if (!ValidateEmail.validateEmail(email)) {
            edittextEMail.setError("Enter valid Email");
            return;
        } else if (password.equalsIgnoreCase("")) {
            editpassword.setError("Enter confirm password");
            return;
        } else {
            HashMap<String, String> allHashMap = new HashMap<>();
            allHashMap.put("email", email);
            allHashMap.put("password", password);
            allHashMap.put("device_type", "1");
            allHashMap.put("device_token", PersistentUser.getPushToken(mContext));
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
        final String url = AllUrls.BASEURL + "login";
        ServerCallsProvider.volleyPostRequest(url, allHashMap, allHashMapHeader, TAG, new ServerResponse() {
            @Override
            public void onSuccess(String statusCode, String responseServer) {
                mBusyDialog.dismis();
                try {
                    Logger.debugLog("responseServer", responseServer);
                    JSONObject mJsonObject = new JSONObject(responseServer);
                    if (mJsonObject.getBoolean("success")) {
                        JSONObject data = mJsonObject.getJSONObject("data");
                        String is_verify = data.getString("is_verify");
                        if (is_verify.equalsIgnoreCase("1")) {
                            String userId = data.getString("id");
                            String auth_token = data.getString("auth_token");
                            PersistentUser.setUserID(mContext, userId);
                            PersistentUser.setUserToken(mContext, auth_token);
                            PersistentUser.setLogin(mContext);
                            PersistentUser.setUserDetails(mContext, responseServer);
                            Intent intent = new Intent(LoginActivity.this, HomeActivity.class);
                            intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP | Intent.FLAG_ACTIVITY_NEW_TASK);
                            startActivity(intent);
                        } else {
                            String message = "Your account not varification.";
                            ToastHelper.showToast(mContext, message);
                            String otp_code = data.getString("otp_code");
                            String phone = data.getString("phone");
                            String country_code = data.getString("country_code");
                            Intent intent = new Intent(LoginActivity.this, VerificationActivity.class);
                            intent.putExtra("screen", 0);
                            intent.putExtra("otp_code", otp_code);
                            intent.putExtra("phone", phone);
                            intent.putExtra("country_code", country_code);
                            startActivity(intent);
                        }

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
                ToastHelper.showToast(mContext, serverResponse);
                Logger.debugLog("onFailed serverResponse", serverResponse);
            }
        });
    }

    AlertDialog alertDialog;

    public void showDialogForVideo() {
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        LayoutInflater inflater = getLayoutInflater();
        View mView = inflater.inflate(R.layout.dialog_forgotpassword, null);
        builder.setView(mView);
        builder.setCancelable(true);
        alertDialog = builder.create();
        alertDialog.getWindow().setBackgroundDrawable(new ColorDrawable(android.graphics.Color.TRANSPARENT));
        alertDialog.show();
        final CountryCodePicker mCountryCodePicker = (CountryCodePicker) mView.findViewById(R.id.ccp);
        final EditText edittextphone = (EditText) mView.findViewById(R.id.edittextphone);
        final TextView Submit_btn = (TextView) mView.findViewById(R.id.Submit_btn);

        Submit_btn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                alertDialog.dismiss();

                String country_code = mCountryCodePicker.getSelectedCountryCode();
                String phone = edittextphone.getText().toString();

                if (phone.equalsIgnoreCase("")) {
                    ToastHelper.showToast(mContext, "Please enter phone");
                    return;
                } else {
                    HashMap<String, String> allHashMap = new HashMap<>();
                    allHashMap.put("country_code", country_code);
                    allHashMap.put("phone", phone);
                    forgorpasswordServerRequest(allHashMap);
                }

            }
        });
    }

    private void forgorpasswordServerRequest(HashMap<String, String> allHashMap) {
        if (!Helpers.isNetworkAvailable(mContext)) {
            Helpers.showOkayDialog(mContext, R.string.no_internet_connection);
            return;
        }
        HashMap<String, String> allHashMapHeader = new HashMap<>();
        allHashMapHeader.put("appKey", AllUrls.APP_KEY);
        mBusyDialog = new BusyDialog(mContext);
        mBusyDialog.show();
        final String url = AllUrls.BASEURL + "resendCode";
        ServerCallsProvider.volleyPostRequest(url, allHashMap, allHashMapHeader, TAG, new ServerResponse() {
            @Override
            public void onSuccess(String statusCode, String responseServer) {
                mBusyDialog.dismis();
                try {
                    Logger.debugLog("responseServer", responseServer);
                    JSONObject mJsonObject = new JSONObject(responseServer);
                    if (mJsonObject.getBoolean("success")) {
                        JSONObject data = mJsonObject.getJSONObject("data");
                        String otp_code = data.getString("otp_code");
                        String phone = data.getString("phone");
                        String country_code = data.getString("country_code");
                        PersistentUser.setUserDetails(mContext, responseServer);
                        Intent intent = new Intent(LoginActivity.this, VerificationActivity.class);
                        intent.putExtra("screen", 1);
                        intent.putExtra("otp_code", otp_code);
                        intent.putExtra("phone", phone);
                        intent.putExtra("country_code", country_code);
                        startActivity(intent);

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
                ToastHelper.showToast(mContext, serverResponse);
                Logger.debugLog("onFailed serverResponse", serverResponse);
            }
        });
    }



}
