package com.grace.book;

import android.Manifest;
import android.annotation.SuppressLint;
import android.content.Context;
import android.content.Intent;
import android.content.IntentSender;
import android.content.pm.PackageManager;
import android.graphics.drawable.ColorDrawable;
import android.os.Build;
import android.os.Bundle;
import android.os.Looper;
import android.view.LayoutInflater;
import android.view.View;
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
import com.grace.book.utils.Logger;
import com.grace.book.utils.PersistentUser;

import java.util.ArrayList;
import java.util.List;

public class LoginActivity extends AppCompatActivity {
    private static final String TAG = LoginActivity.class.getSimpleName();
    final private int REQUEST_CODE_ASK_PERMISSIONS_AGENT = 100;
    private List<String> permissions = new ArrayList<String>();
    private FusedLocationProviderClient mFusedLocationClient;
    private LocationRequest mLocationRequest;
    private LocationSettingsRequest mLocationSettingsRequest;
    private LocationCallback mLocationCallback;
    private Context mContext;
    private SettingsClient mSettingsClient;
    private GoogleApiClient mGoogleApiClient;
    private double Latitude = 0.0, Longitude = 0.0;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);
        mContext=this;
        initUi();
    }

    private void initUi() {
        findViewById(R.id.tvSignup).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                checkLocationPermissionsInitial();


            }
        });
        findViewById(R.id.tvForgotpassword).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                showDialogForVideo();
            }
        });
        findViewById(R.id.tvLoginSubmit).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(LoginActivity.this, HomeActivity.class);
                intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP | Intent.FLAG_ACTIVITY_NEW_TASK);
                startActivity(intent);
            }
        });




    }

    public void showDialogForVideo() {
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        LayoutInflater inflater = getLayoutInflater();
        View mView = inflater.inflate(R.layout.dialog_forgotpassword, null);
        builder.setView(mView);
        builder.setCancelable(true);
        final AlertDialog alertDialog = builder.create();
        alertDialog.getWindow().setBackgroundDrawable(new ColorDrawable(android.graphics.Color.TRANSPARENT));
        alertDialog.show();
        final TextView Submit_btn = (TextView) mView.findViewById(R.id.Submit_btn);
        Submit_btn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                alertDialog.dismiss();
                Intent intent = new Intent(LoginActivity.this, VerificationActivity.class);
                intent.putExtra("screen",1);
                startActivity(intent);

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
                                            rae.startResolutionForResult(LoginActivity.this, REQUEST_CODE_ASK_PERMISSIONS_AGENT);
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

                    Intent mm = new Intent(LoginActivity.this, SignupActivity.class);
                    startActivity(mm);


                    //verification();

                }
            }
        };
    }

    @SuppressLint("MissingPermission")
    public void requestLocationUpdate() {
        mFusedLocationClient.requestLocationUpdates(mLocationRequest, mLocationCallback, Looper.myLooper());
    }
}
