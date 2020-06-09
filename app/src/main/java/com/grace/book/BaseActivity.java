package com.grace.book;

import android.app.AlertDialog;
import android.content.Context;
import android.content.Intent;
import android.graphics.drawable.ColorDrawable;
import android.os.Bundle;
import android.view.KeyEvent;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.FrameLayout;
import android.widget.LinearLayout;
import android.widget.TextView;

import androidx.appcompat.app.AppCompatActivity;

import com.grace.book.callbackinterface.ServerResponse;
import com.grace.book.myapplication.Myapplication;
import com.grace.book.networkcalls.ServerCallsProvider;
import com.grace.book.slidermenu.SlidingMenu;
import com.grace.book.utils.AllUrls;
import com.grace.book.utils.Helpers;
import com.grace.book.utils.Logger;
import com.grace.book.utils.PersistentUser;

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
    private SlidingMenu slidingMenu;
    private int selection = 0;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_base);
        mContext = this;
        bottomView();
    }

    public void bottomView() {
        slidingMenu = new SlidingMenu(this);
        if (PersistentUser.isLanguage(mContext))
            slidingMenu.setMode(SlidingMenu.LEFT);
        else
            slidingMenu.setMode(SlidingMenu.RIGHT);

        slidingMenu.setTouchModeAbove(SlidingMenu.TOUCHMODE_FULLSCREEN);
        slidingMenu.setShadowDrawable(R.drawable.shadow);
        slidingMenu.setBehindOffsetRes(R.dimen.slidingmenu_offset);
        slidingMenu.setFadeDegree(0.8f);
        slidingMenu.attachToActivity(this, SlidingMenu.SLIDING_CONTENT);
        slidingMenu.setMenu(R.layout.home_side_menu);
        slidingMenu.setSlidingEnabled(true);


        layout_home = (LinearLayout) this.findViewById(R.id.layout_home);
        layout_prayer = (LinearLayout) this.findViewById(R.id.layout_prayer);
        layout_profile = (LinearLayout) this.findViewById(R.id.layout_profile);
        layout_notificaion = (LinearLayout) this.findViewById(R.id.layout_notificaion);

        layout_home.setOnClickListener(listenerForTab);
        layout_prayer.setOnClickListener(listenerForTab);
        layout_profile.setOnClickListener(listenerForTab);
        layout_notificaion.setOnClickListener(listenerForTab);

        LinearLayout btnmenu= (LinearLayout)this.findViewById(R.id.btnmenu);
        btnmenu.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                slidingMenu.toggle();
            }
        });

    }


    public View.OnClickListener listenerForTab = new View.OnClickListener() {
        @Override
        public void onClick(View v) {
            switch (v.getId()) {

                case R.id.layout_home:
                    intent = new Intent(mContext, HomeActivity.class);
                    intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP | Intent.FLAG_ACTIVITY_NEW_TASK);
                    startActivity(intent);
                    finish();

                    break;
                case R.id.layout_prayer:
                    selectedDeselectedLayut();
                    intent = new Intent(mContext, PrayerActivity.class);
                    intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP | Intent.FLAG_ACTIVITY_NEW_TASK);
                    startActivity(intent);
                    finish();

                    break;
                case R.id.layout_profile:
                    selectedDeselectedLayut();
                    intent = new Intent(mContext, ProfileActivity.class);
                    intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP | Intent.FLAG_ACTIVITY_NEW_TASK);
                    startActivity(intent);
                    finish();

                    break;

                case R.id.layout_notificaion:
                    selectedDeselectedLayut();
                    intent = new Intent(mContext, NotificaionActivity.class);
                    intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP | Intent.FLAG_ACTIVITY_NEW_TASK);
                    startActivity(intent);
                    finish();

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

        selection = Myapplication.selection;
        layout_home.setSelected(false);
        layout_prayer.setSelected(false);
        layout_profile.setSelected(false);
        layout_notificaion.setSelected(false);

        if (selection == 0) {
            layout_home.setSelected(true);
        } else if (selection == 1) {
            layout_prayer.setSelected(true);
        } else if (selection == 2) {
            layout_profile.setSelected(true);
        } else if (selection == 3) {
            layout_notificaion.setSelected(true);
        }
    }

    @Override
    protected void onResume() {
        super.onResume();
        overridePendingTransition(0, 0);
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
                finish();
            }
        });
    }

    @Override
    public boolean onKeyDown(int keyCode, KeyEvent event) {
        //Changes 'back' button action
        if (keyCode == KeyEvent.KEYCODE_BACK) {
            alertfornewuser();
        }
        return true;
    }


}

