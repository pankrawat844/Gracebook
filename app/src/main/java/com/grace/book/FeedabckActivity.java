package com.grace.book;

import android.content.Context;
import android.os.Bundle;
import android.os.Handler;
import android.util.Log;
import android.view.View;
import android.widget.EditText;
import android.widget.LinearLayout;

import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.grace.book.adapter.MyPostListAdapter;
import com.grace.book.callbackinterface.ServerResponse;
import com.grace.book.customview.VerticalSpaceItemDecoration;
import com.grace.book.model.FeedList;
import com.grace.book.networkcalls.ServerCallsProvider;
import com.grace.book.utils.AllUrls;
import com.grace.book.utils.BusyDialog;
import com.grace.book.utils.ConstantFunctions;
import com.grace.book.utils.Helpers;
import com.grace.book.utils.Logger;
import com.grace.book.utils.PersistentUser;
import com.grace.book.utils.ToastHelper;
import com.grace.book.utils.ValidateEmail;

import org.json.JSONObject;

import java.util.ArrayList;
import java.util.HashMap;

public class FeedabckActivity extends AppCompatActivity {
    private static final String TAG = FeedabckActivity.class.getSimpleName();
    private Context mContext;
    private EditText edittextFname;
    private EditText edittexEmail;
    private EditText edittexSubject;
    private EditText edittextMessage;
    private BusyDialog mBusyDialog;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_feedback);
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
        findViewById(R.id.tvSubmit).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                vaidation();
            }
        });
        edittextFname = (EditText) this.findViewById(R.id.edittextFname);
        edittexEmail = (EditText) this.findViewById(R.id.edittexEmail);
        edittexSubject = (EditText) this.findViewById(R.id.edittexSubject);
        edittextMessage = (EditText) this.findViewById(R.id.edittextMessage);
        userDataList();

    }

    public void vaidation() {
        String full_name = edittextFname.getText().toString().trim();
        String email = edittexEmail.getText().toString().trim();
        String subject = edittexSubject.getText().toString().trim();
        String message = edittextMessage.getText().toString().trim();

        if (full_name.equalsIgnoreCase("")) {
            edittextFname.setError("Enter full name");
            return;
        } else if (email.equalsIgnoreCase("")) {
            edittexEmail.setError("Enter Email");
            return;
        } else if (!ValidateEmail.validateEmail(email)) {
            edittexEmail.setError("Enter valid Email");
            return;
        } else if (subject.equalsIgnoreCase("")) {
            edittexSubject.setError("Enter subject");
            return;
        } else if (message.equalsIgnoreCase("")) {
            edittextMessage.setError("Enter message");
            return;
        } else {

            HashMap<String, String> allHashMap = new HashMap<>();
            allHashMap.put("full_name", full_name);
            allHashMap.put("email", email);
            allHashMap.put("subject", subject);
            allHashMap.put("message", message);
            ServerRequest(allHashMap);
        }

    }

    private void ServerRequest(HashMap<String, String> allHashMap) {
        if (!Helpers.isNetworkAvailable(mContext)) {
            Helpers.showOkayDialog(mContext, R.string.no_internet_connection);
            return;
        }
        mBusyDialog = new BusyDialog(mContext);
        mBusyDialog.show();
        HashMap<String, String> allHashMapHeader = new HashMap<>();
        allHashMapHeader.put("appKey", AllUrls.APP_KEY);
        allHashMapHeader.put("authToken", PersistentUser.getUserToken(mContext));
        final String url = AllUrls.BASEURL + "feedback";
        ServerCallsProvider.volleyPostRequest(url, allHashMap, allHashMapHeader, TAG, new ServerResponse() {
            @Override
            public void onSuccess(String statusCode, String responseServer) {
                try {
                    mBusyDialog.dismis();
                    Logger.debugLog("responseServer", responseServer);
                    JSONObject mJsonObject = new JSONObject(responseServer);
                    if (mJsonObject.getBoolean("success")) {
                        edittexSubject.setText("");
                        edittextMessage.setText("");
                        ToastHelper.showToast(mContext, "Feedback send successfully");

                    }
                } catch (Exception e) {
                    Logger.debugLog("Exception", e.getMessage());

                }
            }

            @Override
            public void onFailed(String statusCode, String serverResponse) {
                mBusyDialog.dismis();
            }
        });
    }

    public void userDataList() {
        try {

            JSONObject mJsonObject = new JSONObject(PersistentUser.getUserDetails(mContext));
            if (mJsonObject.getBoolean("success")) {
                JSONObject data = mJsonObject.getJSONObject("data");
                String fname = data.getString("fname");
                String lname = data.getString("lname");
                String email = data.getString("email");
                edittextFname.setText((fname + " " + lname));
                edittexEmail.setText(email);
            }
        } catch (Exception ex) {
            Log.e("Exception", ex.getMessage());

        }


    }
}
