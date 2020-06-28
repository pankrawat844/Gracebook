package com.grace.book.myapplication;

import android.app.Application;
import android.content.Context;
import android.content.res.Configuration;

import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.toolbox.Volley;
import com.google.firebase.FirebaseApp;

public class Myapplication extends Application {
    private static Myapplication mInstance;
    private RequestQueue requestQueue;
    private static Context mContext;
    public static  int selection =0;
    public static String  NEW_MESSAGE_ACTION ="NEW_MESSAGE_ACTION";

    @Override
    public void onCreate() {
        super.onCreate();
        mInstance = this;
        mContext = this;
        FirebaseApp.initializeApp(this);


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

    public void cancelAllRequests(String tag) {
        getRequestQueue().cancelAll(tag);
    }

    @Override
    public void onConfigurationChanged(Configuration newConfig) {
        super.onConfigurationChanged(newConfig);
    }



}
