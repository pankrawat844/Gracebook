package com.grace.book;

import android.app.AlertDialog;
import android.content.Context;
import android.content.Intent;
import android.graphics.drawable.ColorDrawable;
import android.os.Bundle;
import android.util.Log;
import android.view.KeyEvent;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import androidx.appcompat.app.ActionBarDrawerToggle;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.view.GravityCompat;
import androidx.drawerlayout.widget.DrawerLayout;

import com.google.android.material.navigation.NavigationView;
import com.grace.book.callbackinterface.ServerResponse;
import com.grace.book.myapplication.Myapplication;
import com.grace.book.networkcalls.ServerCallsProvider;
import com.grace.book.slidermenu.SlidingMenu;
import com.grace.book.utils.AllUrls;
import com.grace.book.utils.ConstantFunctions;
import com.grace.book.utils.Helpers;
import com.grace.book.utils.Logger;
import com.grace.book.utils.PersistentUser;
import com.grace.book.utils.ToastHelper;

import org.json.JSONObject;

import java.util.HashMap;


public class BaseActivity extends AppCompatActivity {
    private static final String TAG = BaseActivity.class.getSimpleName();
    public FrameLayout frameLayout;
    private Context mContext;
    private Intent intent = null;

    private LinearLayout layout_home;
    private LinearLayout layout_prayer;
    private LinearLayout layout_profile;
    private LinearLayout layout_notificaion;
    private LinearLayout layout_chat;
    private LinearLayout layout_group;
    private int selection = 0;
    private LinearLayout layoutdailyverse;
    private LinearLayout layoutmyposts;
    private LinearLayout searchbeliever;
    private LinearLayout layoutfeedback;
    private LinearLayout layoutsettings;
    private LinearLayout layoutLogout;
    private ImageView userImage;
    private TextView username;
    private DrawerLayout mDrawerLayout;
    private ActionBarDrawerToggle actionBarDrawerToggle;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_base);
        mContext = this;
        bottomView();
    }

    public void bottomView() {

        mDrawerLayout = findViewById(R.id.drawer_layout);
        frameLayout = (FrameLayout) this.findViewById(R.id.content_frame);
        layout_home = (LinearLayout) this.findViewById(R.id.layout_home);
        layout_prayer = (LinearLayout) this.findViewById(R.id.layout_prayer);
        layout_profile = (LinearLayout) this.findViewById(R.id.layout_profile);
        layout_chat = (LinearLayout) this.findViewById(R.id.layout_chat);
        layout_notificaion = (LinearLayout) this.findViewById(R.id.layout_notificaion);
        layout_group = (LinearLayout) this.findViewById(R.id.layout_group);


        layout_home.setOnClickListener(listenerForTab);
        layout_prayer.setOnClickListener(listenerForTab);
        layout_profile.setOnClickListener(listenerForTab);
        layout_notificaion.setOnClickListener(listenerForTab);
        layout_chat.setOnClickListener(listenerForTab);
        layout_group.setOnClickListener(listenerForTab);


        layoutdailyverse = (LinearLayout) this.findViewById(R.id.layoutdailyverse);
        layoutmyposts = (LinearLayout) this.findViewById(R.id.layoutmyposts);
        searchbeliever = (LinearLayout) this.findViewById(R.id.searchbeliever);
        layoutfeedback = (LinearLayout) this.findViewById(R.id.layoutfeedback);
        layoutsettings = (LinearLayout) this.findViewById(R.id.layoutsettings);
        layoutLogout = (LinearLayout) this.findViewById(R.id.layoutLogout);
//
        userImage = (ImageView) this.findViewById(R.id.userImageSilder);
        username = (TextView) this.findViewById(R.id.usernameSlider);
//
        LinearLayout btnmenu = (LinearLayout) this.findViewById(R.id.btnmenu);
        btnmenu.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                //slidingMenu.toggle();
                if (mDrawerLayout.isDrawerOpen(GravityCompat.START)) {
                    mDrawerLayout.closeDrawer(GravityCompat.START);
                } else {
                    mDrawerLayout.openDrawer(GravityCompat.START);

                }

            }
        });

        layoutdailyverse.setOnClickListener(listener);
        layoutmyposts.setOnClickListener(listener);
        searchbeliever.setOnClickListener(listener);
        layoutfeedback.setOnClickListener(listener);
        layoutsettings.setOnClickListener(listener);
        layoutLogout.setOnClickListener(listener);
        userImage.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent mIntent = new Intent(mContext, EditProfileActivity.class);
                startActivity(mIntent);
            }
        });

        userDataList();
        userServerRequest();
    }

    void setupDrawerToggle() {
        actionBarDrawerToggle = new ActionBarDrawerToggle(this, mDrawerLayout, R.string.app_name, R.string.app_name);
        actionBarDrawerToggle.syncState();
    }

    public View.OnClickListener listener = new View.OnClickListener() {
        @Override
        public void onClick(View v) {
            mDrawerLayout.closeDrawer(GravityCompat.START);
            switch (v.getId()) {
                case R.id.layoutdailyverse:
                    intent = new Intent(mContext, DailyverseActivity.class);
                    startActivity(intent);

                    break;

                case R.id.layoutmyposts:
                    intent = new Intent(mContext, MyPostActivity.class);
                    startActivity(intent);
                    break;

                case R.id.searchbeliever:
                    intent = new Intent(mContext, SearchBelievertActivity.class);
                    startActivity(intent);
                    break;

                case R.id.layoutfeedback:
                    intent = new Intent(mContext, FeedabckActivity.class);
                    startActivity(intent);
                    break;
                case R.id.layoutsettings:

                    intent = new Intent(mContext, SettingsActivity.class);
                    startActivity(intent);
                    break;


                case R.id.layoutLogout:
                    alertfornewuser();
                    break;

                default:
                    break;
            }

        }
    };


    public View.OnClickListener listenerForTab = new View.OnClickListener() {
        @Override
        public void onClick(View v) {
            switch (v.getId()) {

                case R.id.layout_home:
                    if (selection != 0) {
                        selectedDeselectedLayut();
                        intent = new Intent(mContext, HomeActivity.class);
                        intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP | Intent.FLAG_ACTIVITY_NEW_TASK);
                        startActivity(intent);
                        finish();

                    }

                    break;
                case R.id.layout_prayer:
                    if (selection != 1) {
                        selectedDeselectedLayut();
                        intent = new Intent(mContext, PrayerActivity.class);
                        intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP | Intent.FLAG_ACTIVITY_NEW_TASK);
                        startActivity(intent);
                        finish();

                    }

                    break;


                case R.id.layout_profile:
                    if (selection != 2) {
                        selectedDeselectedLayut();
                        intent = new Intent(mContext, FriendsActivity.class);
                        intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP | Intent.FLAG_ACTIVITY_NEW_TASK);
                        startActivity(intent);
                        finish();
                    }


                    break;
                case R.id.layout_notificaion:
                    if (selection != 3) {
                        selectedDeselectedLayut();
                        intent = new Intent(mContext, NotificaionActivity.class);
                        intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP | Intent.FLAG_ACTIVITY_NEW_TASK);
                        startActivity(intent);
                        finish();
                    }


                    break;
                case R.id.layout_chat:
                    if (selection != 4) {
                        selectedDeselectedLayut();
                        intent = new Intent(mContext, MessageUserListActivity.class);
                        intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP | Intent.FLAG_ACTIVITY_NEW_TASK);
                        startActivity(intent);
                        finish();
                    }


                    break;
                case R.id.layout_group:
                    if (selection != 5) {
                        selectedDeselectedLayut();
                        intent = new Intent(mContext, GroupActivity.class);
                        intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP | Intent.FLAG_ACTIVITY_NEW_TASK);
                        startActivity(intent);
                        finish();
                    }

                    break;


                default:
                    break;

            }
        }
    };

    public void selectedDeselectedLayut() {
        layout_home = (LinearLayout) this.findViewById(R.id.layout_home);
        layout_prayer = (LinearLayout) this.findViewById(R.id.layout_prayer);
        layout_profile = (LinearLayout) this.findViewById(R.id.layout_profile);
        layout_notificaion = (LinearLayout) this.findViewById(R.id.layout_notificaion);
        layout_group = (LinearLayout) this.findViewById(R.id.layout_group);
        layout_chat = (LinearLayout) this.findViewById(R.id.layout_chat);

        selection = Myapplication.selection;
        layout_home.setSelected(false);
        layout_prayer.setSelected(false);
        layout_profile.setSelected(false);
        layout_notificaion.setSelected(false);
        layout_chat.setSelected(false);
        layout_group.setSelected(false);

        if (selection == 0) {
            layout_home.setSelected(true);
        } else if (selection == 1) {
            layout_prayer.setSelected(true);
        } else if (selection == 2) {
            layout_profile.setSelected(true);
        } else if (selection == 3) {
            layout_notificaion.setSelected(true);
        } else if (selection == 4) {
            layout_chat.setSelected(true);
        } else if (selection == 5) {
            layout_group.setSelected(true);
        }


    }

    @Override
    protected void onResume() {
        super.onResume();
        overridePendingTransition(0, 0);
        userDataList();
    }

    private void userDetailsServerRequest() {
        if (!Helpers.isNetworkAvailable(mContext)) {
            Helpers.showOkayDialog(mContext, R.string.no_internet_connection);
            return;
        }
        HashMap<String, String> allHashMap = new HashMap<>();
        HashMap<String, String> allHashMapHeader = new HashMap<>();
        allHashMapHeader.put("appKey", AllUrls.APP_KEY);
        allHashMapHeader.put("authToken", PersistentUser.getUserDetails(mContext));
        final String url = AllUrls.BASEURL + "userdetails";
        ServerCallsProvider.volleyPostRequest(url, allHashMap, allHashMapHeader, TAG, new ServerResponse() {
            @Override
            public void onSuccess(String statusCode, String responseServer) {
                try {
                    Logger.debugLog("responseServer", responseServer);
                    JSONObject mJsonObject = new JSONObject(responseServer);
                    if (mJsonObject.getBoolean("success")) {
//                        JSONObject data = mJsonObject.getJSONObject("data");
//                        String name = data.getString("name");
//                        String pic = data.getString("pic");
//                        ConstantFunctions.loadImageForCircel(pic, usserImage);
//                        usserText.setText(name);

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

    AlertDialog alertDialog = null;

    public void alertfornewuser() {
        AlertDialog.Builder builder = new AlertDialog.Builder(BaseActivity.this);
        LayoutInflater inflater = getLayoutInflater();
        View mView = inflater.inflate(R.layout.dialog_app_logout, null);
        builder.setView(mView);
        builder.setCancelable(false);
        alertDialog = builder.create();
        alertDialog.getWindow().setBackgroundDrawable(new ColorDrawable(android.graphics.Color.TRANSPARENT));
        alertDialog.show();
        TextView no_botton = (TextView) mView.findViewById(R.id.no_botton);
        TextView yes_btn = (TextView) mView.findViewById(R.id.yes_btn);
        no_botton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                alertDialog.dismiss();

            }
        });
        yes_btn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                alertDialog.dismiss();
                PersistentUser.resetAllData(mContext);
                Intent intent = new Intent(BaseActivity.this, LoginActivity.class);
                intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP | Intent.FLAG_ACTIVITY_NEW_TASK);
                startActivity(intent);
                finish();
            }
        });
    }

//    @Override
//    public boolean onKeyDown(int keyCode, KeyEvent event) {
//        //Changes 'back' button action
//        if (keyCode == KeyEvent.KEYCODE_BACK) {
//            alertfornewuser();
//        }
//        return true;
//    }

    public void userDataList() {
        try {

            JSONObject mJsonObject = new JSONObject(PersistentUser.getUserDetails(mContext));
            if (mJsonObject.getBoolean("success")) {
                JSONObject data = mJsonObject.getJSONObject("data");
                String fname = data.getString("fname");
                String lname = data.getString("lname");
                String profile_pic = data.getString("profile_pic");
                Log.e("profile_pic",profile_pic);
                username.setText((fname + " " + lname));
                ConstantFunctions.loadImageForCircel(profile_pic, userImage);


            }
        } catch (Exception ex) {
            Log.e("Exception", ex.getMessage());

        }


    }

    private void userServerRequest() {
        if (!Helpers.isNetworkAvailable(mContext)) {
            Helpers.showOkayDialog(mContext, R.string.no_internet_connection);
            return;
        }
        HashMap<String, String> allHashMap = new HashMap<>();
        HashMap<String, String> allHashMapHeader = new HashMap<>();
        allHashMapHeader.put("appKey", AllUrls.APP_KEY);
        allHashMapHeader.put("authToken", PersistentUser.getUserToken(mContext));
        final String url = AllUrls.BASEURL + "loginTrack";
        ServerCallsProvider.volleyPostRequest(url, allHashMap, allHashMapHeader, TAG, new ServerResponse() {
            @Override
            public void onSuccess(String statusCode, String responseServer) {
                try {
                    Logger.debugLog("responseServer", responseServer);


                } catch (Exception e) {

                }
            }

            @Override
            public void onFailed(String statusCode, String serverResponse) {
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

