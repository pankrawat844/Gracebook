package com.zocia.book;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import com.google.firebase.auth.FirebaseAuth;
import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.zocia.book.callbackinterface.ServerResponse;
import com.zocia.book.crashreport.Helper;
import com.zocia.book.model.BannerList;
import com.zocia.book.model.FeedList;
import com.zocia.book.networkcalls.ServerCallsProvider;
import com.zocia.book.utils.AllUrls;
import com.zocia.book.utils.BusyDialog;
import com.zocia.book.utils.DateUtility;
import com.zocia.book.utils.Helpers;
import com.zocia.book.utils.PersistentUser;

import org.json.JSONArray;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;

public class AddNewsActivity extends BaseActivity {

    private BusyDialog mBusyDialog;
    private EditText heading, content;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_add_news);
        heading = findViewById(R.id.heading);
        content = findViewById(R.id.content);
        TextView post = findViewById(R.id.postView);
        post.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (heading.getText().toString().isEmpty()) {
                    Toast.makeText(AddNewsActivity.this, "Please add heading", Toast.LENGTH_SHORT).show();
                } else if (content.getText().toString().isEmpty()) {
                    Toast.makeText(AddNewsActivity.this, "Please enter news content", Toast.LENGTH_SHORT).show();

                } else {
                    ServerRequest();
                }

            }
        });
    }

    private void ServerRequest() {
        if (!Helpers.isNetworkAvailable(this)) {
            Helpers.showOkayDialog(this, R.string.no_internet_connection);
            return;
        }
        mBusyDialog = new BusyDialog(this);
        mBusyDialog.show();

        HashMap<String, String> allHashMap = new HashMap<>();
        allHashMap.put("details", content.getText().toString());
        allHashMap.put("is_user", "1");
        allHashMap.put("post_time", DateUtility.getCurrentTimeForsend());
        allHashMap.put("heading", heading.getText().toString());
        HashMap<String, String> allHashMapHeader = new HashMap<>();
        allHashMapHeader.put("appKey", AllUrls.APP_KEY);
        allHashMapHeader.put("authToken", PersistentUser.getUserToken(this));
        final String url = AllUrls.BASEURL + "addNews";
        ServerCallsProvider.volleyPostRequest(url, allHashMap, allHashMapHeader, "TAG", new ServerResponse() {
            @Override
            public void onSuccess(String statusCode, String responseServer) {
                try {
                    mBusyDialog.dismis();

                    Log.w("responseServer", responseServer);
                    JSONObject mJsonObject = new JSONObject(responseServer);
                    if (mJsonObject.getBoolean("success")) {
//                        selectedDeselectedLayut();
                        Intent intent = new Intent(AddNewsActivity.this, NewsFeedActivity.class);
                        intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP | Intent.FLAG_ACTIVITY_NEW_TASK);
                        intent.putExtra("screen", getIntent().getStringExtra("screen"));

                        startActivity(intent);
                        finish();
                    }
                } catch (Exception e) {
                    mBusyDialog.dismis();
                }
            }

            @Override
            public void onFailed(String statusCode, String serverResponse) {
                mBusyDialog.dismis();
                if (statusCode.equalsIgnoreCase("404")) {
                    PersistentUser.resetAllData(AddNewsActivity.this);
                    Intent intent = new Intent(AddNewsActivity.this, LoginActivity.class);
                    intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP | Intent.FLAG_ACTIVITY_NEW_TASK);
                    startActivity(intent);
                    finish();
                }
            }
        });
    }

}