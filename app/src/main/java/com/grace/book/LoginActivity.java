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
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
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
    final private int REQUEST_CODE_ASK_PERMISSIONS_AGENT = 100;
    final private int REQUEST_SET_ASK_PERMISSIONS_AGENT = 101;
    private List<String> permissions = new ArrayList<String>();
    private FusedLocationProviderClient mFusedLocationClient;
    private LocationRequest mLocationRequest;
    private LocationSettingsRequest mLocationSettingsRequest;
    private LocationCallback mLocationCallback;
    private Context mContext;
    private SettingsClient mSettingsClient;
    private GoogleApiClient mGoogleApiClient;
    private double Latitude = 0.0, Longitude = 0.0;
    private BusyDialog mBusyDialog;
    private EditText editpassword;
    private EditText edittextEMail;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);
        mContext = this;
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


    public void checkLocationPermissionsInitial() {
        permissions.clear();
        if (Build.VERSION.SDK_INT > 22) {
            String storagePermission = Manifest.permission.ACCESS_FINE_LOCATION;
            int hasstoragePermission = checkSelfPermission(storagePermission);
            if (hasstoragePermission != PackageManager.PERMISSION_GRANTED) {
                permissions.add(storagePermission);
            }
            if (!permissions.isEmpty()) {
                String[] params = permissions.toArray(new String[permissions.size()]);
                requestPermissions(params, REQUEST_CODE_ASK_PERMISSIONS_AGENT);
            } else {
                LocationGPSSetting();

            }
        } else {
            LocationGPSSetting();
        }
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, String[] permissions, int[] grantResults) {
        switch (requestCode) {
            case REQUEST_CODE_ASK_PERMISSIONS_AGENT:
                if (grantResults.length > 0) {
                    if (grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                        LocationGPSSetting();
                    }
                }
                break;
            default:
                super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        }

    }

    public void LocationGPSSetting() {
        mFusedLocationClient = LocationServices.getFusedLocationProviderClient(mContext);
        mSettingsClient = LocationServices.getSettingsClient(mContext);
        mGoogleApiClient = new GoogleApiClient.Builder(mContext)
                .addConnectionCallbacks(new GoogleApiClient.ConnectionCallbacks() {
                    @Override
                    public void onConnected(@Nullable Bundle bundle) {
                        mLocationRequest = new LocationRequest();
                        mLocationRequest.setInterval(1000);
                        mLocationRequest.setFastestInterval(5 * 100);
                        mLocationRequest.setPriority(LocationRequest.PRIORITY_HIGH_ACCURACY);
                        mLocationRequest.setSmallestDisplacement(0);
                        LocationSettingsRequest.Builder builder = new LocationSettingsRequest.Builder();
                        builder.addLocationRequest(mLocationRequest);
                        builder.setAlwaysShow(true);
                        mLocationSettingsRequest = builder.build();
                        mSettingsClient.checkLocationSettings(mLocationSettingsRequest)
                                .addOnSuccessListener(new OnSuccessListener<LocationSettingsResponse>() {
                                    @Override
                                    public void onSuccess(LocationSettingsResponse locationSettingsResponse) {
                                        requestLocationUpdate();
                                    }
                                }).addOnFailureListener(new OnFailureListener() {
                            @Override
                            public void onFailure(@NonNull Exception e) {
                                int statusCode = ((ApiException) e).getStatusCode();
                                switch (statusCode) {
                                    case LocationSettingsStatusCodes.RESOLUTION_REQUIRED:
                                        try {
                                            ResolvableApiException rae = (ResolvableApiException) e;
                                            rae.startResolutionForResult(LoginActivity.this, REQUEST_SET_ASK_PERMISSIONS_AGENT);
                                        } catch (IntentSender.SendIntentException e1) {
                                            e1.printStackTrace();
                                        }
                                        break;
                                    case LocationSettingsStatusCodes.SETTINGS_CHANGE_UNAVAILABLE:
                                        //showLog("Location Settings are Inadequate, and Cannot be fixed here. Fix in Settings");
                                }
                            }
                        }).addOnCanceledListener(new OnCanceledListener() {
                            @Override
                            public void onCanceled() {
                                Logger.debugLog("onConnectionFailed", "onCanceled");

                                // showLog("onCanceled");
                            }
                        });
                    }

                    @Override
                    public void onConnectionSuspended(int i) {
                        Logger.debugLog("onConnectionFailed", "onConnectionSuspended");
                        //connectGoogleClient();
                        // callback.onGoogleAPIClient(mGoogleApiClient, "Connection Suspended");
                    }
                })
                .addOnConnectionFailedListener(new GoogleApiClient.OnConnectionFailedListener() {
                    @Override
                    public void onConnectionFailed(@NonNull ConnectionResult connectionResult) {
                        Logger.debugLog("onConnectionFailed", "onConnectionFailed");
                        //  callback.onGoogleAPIClient(mGoogleApiClient, "" + connectionResult.getErrorCode() + " " + connectionResult.getErrorMessage());
                    }
                })
                .addApi(LocationServices.API)
                .build();
        mGoogleApiClient.connect();
        mLocationCallback = new LocationCallback() {
            @Override
            public void onLocationResult(final LocationResult locationResult) {
                super.onLocationResult(locationResult);
                Latitude = locationResult.getLastLocation().getLatitude();
                Longitude = locationResult.getLastLocation().getLongitude();
                if (Latitude == 0.0 && Longitude == 0.0) {
                    requestLocationUpdate();
                } else {
                    PersistentUser.setLatitude(mContext, "" + Latitude);
                    PersistentUser.setLongitude(mContext, "" + Longitude);
                    if (mFusedLocationClient != null) {
                        mFusedLocationClient.removeLocationUpdates(mLocationCallback);
                    }
                    if (mGoogleApiClient != null && mGoogleApiClient.isConnected()) {
                        mGoogleApiClient.disconnect();
                    }

                    Intent mIntent = new Intent(LoginActivity.this, SignupActivity.class);
                    startActivity(mIntent);

                }
            }
        };
    }

    @SuppressLint("MissingPermission")
    public void requestLocationUpdate() {
        mFusedLocationClient.requestLocationUpdates(mLocationRequest, mLocationCallback, Looper.myLooper());
    }

    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == REQUEST_SET_ASK_PERMISSIONS_AGENT) {
            if (resultCode == Activity.RESULT_OK) {
                requestLocationUpdate();
            } else {
                finish();
            }
        }
    }


}
