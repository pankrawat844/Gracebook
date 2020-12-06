package org.chat21.android;

import android.app.Application;
import android.content.Context;
import android.content.Intent;
import android.content.res.Configuration;
import android.net.Uri;
import android.text.style.ClickableSpan;
import android.text.style.URLSpan;
import android.util.Log;
import android.widget.TextView;


import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.toolbox.Volley;
import com.google.firebase.FirebaseApp;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.FirebaseDatabase;

import org.chat21.android.core.ChatManager;
import org.chat21.android.core.users.models.IChatUser;
import org.chat21.android.ui.ChatUI;
import org.chat21.android.ui.contacts.activites.ContactListActivity;
import org.chat21.android.ui.conversations.listeners.OnNewConversationClickListener;
import org.chat21.android.ui.messages.listeners.OnMessageClickListener;
import org.chat21.android.utils.IOUtils;

import java.util.ArrayList;

import static org.chat21.android.core.ChatManager._SERIALIZED_CHAT_CONFIGURATION_LOGGED_USER;

public class Myapplication extends Application {
    public static int selection = 0;
    public static String NEW_MESSAGE_ACTION = "NEW_MESSAGE_ACTION";
    public static int selectionOrder = 0;
    public static int selectionComment = 0;
    private static Myapplication mInstance;
    private static Context mContext;
    String crashReporterPath = "/Android/data/Gracebook/files/crashReports";
    ArrayList<String> arrayList = new ArrayList<>();
    private RequestQueue requestQueue;

    public static Context getContext() {
        return mContext;
    }

    public static Myapplication getInstance() {
        return mInstance;
    }

    @Override
    public void onCreate() {
        super.onCreate();
        mInstance = this;
        mContext = this;
        FirebaseApp.initializeApp(this);
        //CrashReporter.initialize(this, crashReporterPath);

    }

    @Override
    protected void attachBaseContext(Context base) {
        super.attachBaseContext(base);

    }

    public RequestQueue getRequestQueue() {
        if (requestQueue == null)
            requestQueue = Volley.newRequestQueue(getApplicationContext());
        return requestQueue;
    }

    public void addToRequestQueue(Request request, String tag) {
        request.setTag(tag);
        getRequestQueue().add(request);

    }

    public ArrayList<String> getInterest() {
        return arrayList;

    }

    public void removeInterest(String request) {
        arrayList.remove(request);

    }

    public void saveInterest(String request) {
        arrayList.add(request);

    }

    public void cancelAllRequests(String tag) {
        getRequestQueue().cancelAll(tag);
    }

    @Override
    public void onConfigurationChanged(Configuration newConfig) {
        super.onConfigurationChanged(newConfig);
    }


}
