package com.zocia.book.myapplication;

import android.app.Application;
import android.content.Context;
import android.content.Intent;
import android.content.res.Configuration;
import android.net.Uri;
import android.text.style.ClickableSpan;
import android.text.style.URLSpan;
import android.util.Log;
import android.widget.TextView;

import androidx.multidex.MultiDex;

import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.toolbox.Volley;
import com.google.firebase.FirebaseApp;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.FirebaseDatabase;
import com.zocia.book.R;

import org.chat21.android.core.ChatManager;
import org.chat21.android.core.users.models.ChatUser;
import org.chat21.android.core.users.models.IChatUser;
import org.chat21.android.ui.ChatUI;
import org.chat21.android.ui.contacts.activites.ContactListActivity;
import org.chat21.android.ui.conversations.listeners.OnNewConversationClickListener;
import org.chat21.android.ui.messages.listeners.OnMessageClickListener;
import org.chat21.android.utils.IOUtils;

import java.util.ArrayList;

import static org.chat21.android.core.ChatManager._SERIALIZED_CHAT_CONFIGURATION_LOGGED_USER;

public class Myapplication extends Application {
    private static Myapplication mInstance;
    private RequestQueue requestQueue;
    private static Context mContext;
    public static int selection = 0;
    public static String NEW_MESSAGE_ACTION = "NEW_MESSAGE_ACTION";
    public static int selectionOrder = 0;
    public static int selectionComment = 0;
    String crashReporterPath = "/Android/data/Gracebook/files/crashReports";
    ArrayList<String> arrayList = new ArrayList<>();
    @Override
    public void onCreate() {
        super.onCreate();
        mInstance = this;
        mContext = this;
        FirebaseApp.initializeApp(this);
        //CrashReporter.initialize(this, crashReporterPath);

        initChatSDK();
    }

    @Override
    protected void attachBaseContext(Context base) {
        super.attachBaseContext(base);

        MultiDex.install(this); // add this
    }

    public static Context getContext() {
        return mContext;
    }

    public static Myapplication getInstance() {
        return mInstance;
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

    public void initChatSDK() {

        //enable persistence must be made before any other usage of FirebaseDatabase instance.
        FirebaseDatabase.getInstance().setPersistenceEnabled(true);

        // it creates the chat configurations
        ChatManager.Configuration mChatConfiguration =
                new ChatManager.Configuration.Builder(getString(R.string.chat_firebase_appId))
                        .firebaseUrl(getString(R.string.chat_firebase_url))
                        .storageBucket(getString(R.string.chat_firebase_storage_bucket))
                        .build();

        FirebaseUser currentUser = FirebaseAuth.getInstance().getCurrentUser();

        // assuming you have a login, check if the logged user (converted to IChatUser) is valid
//        if (currentUser != null) {
        if (currentUser != null) {
            IChatUser iChatUser = (IChatUser) IOUtils.getObjectFromFile(mInstance,
                    _SERIALIZED_CHAT_CONFIGURATION_LOGGED_USER);

//            IChatUser iChatUser = new ChatUser();
//            iChatUser.setId(currentUser.getUid());
//            iChatUser.setEmail(currentUser.getEmail());

            ChatManager.start(this, mChatConfiguration, iChatUser);
            Log.i("TAG", "chat has been initialized with success");

//            ChatManager.getInstance().initContactsSyncronizer();

            ChatUI.getInstance().setContext(mInstance);
            ChatUI.getInstance().enableGroups(false);

            ChatUI.getInstance().setOnMessageClickListener(new OnMessageClickListener() {
                @Override
                public void onMessageLinkClick(TextView message, ClickableSpan clickableSpan) {
                    String text = ((URLSpan) clickableSpan).getURL();

                    Uri uri = Uri.parse(text);
                    Intent browserIntent = new Intent(Intent.ACTION_VIEW, uri);
                    browserIntent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                    startActivity(browserIntent);
                }
            });

            // set on new conversation click listener
//            final IChatUser support = new ChatUser("support", "Chat21 Support");
            final IChatUser support = null;
            ChatUI.getInstance().setOnNewConversationClickListener(new OnNewConversationClickListener() {
                @Override
                public void onNewConversationClicked() {
                    if (support != null) {
                        ChatUI.getInstance().openConversationMessagesActivity(support);
                    } else {
                        Intent intent = new Intent(mInstance, ContactListActivity.class);
                        intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK); // start activity from context

                        startActivity(intent);
                    }
                }
            });

//            // on attach button click listener
//            ChatUI.getInstance().setOnAttachClickListener(new OnAttachClickListener() {
//                @Override
//                public void onAttachClicked(Object object) {
//                    Toast.makeText(instance, "onAttachClickListener", Toast.LENGTH_SHORT).show();
//                }
//            });
//
//            // on create group button click listener
//            ChatUI.getInstance().setOnCreateGroupClickListener(new OnCreateGroupClickListener() {
//                @Override
//                public void onCreateGroupClicked() {
//                    Toast.makeText(instance, "setOnCreateGroupClickListener", Toast.LENGTH_SHORT).show();
//                }
//            });
            Log.i("TAG", "ChatUI has been initialized with success");

        } else {

            Log.w("TAG", "chat can't be initialized because chatUser is null");
        }
    }

}
