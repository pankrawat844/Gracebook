package com.grace.book.utils;

import android.util.Log;


/**
 */

public class Logger {

    public static void debugLog(String logTag, String s) {
        final int chunkSize = 2048;
        for (int i = 0; i < s.length(); i += chunkSize) {
            Log.d(logTag, s.substring(i, Math.min(s.length(), i + chunkSize)));
        }
    }

    public static void infoLog(String logTag, String s) {
  //      Log.i(logTag, s);
//        }
    }

    public static void errorLog(String logTag, String s) {
//        if (BuildConfig.Debug) {
//        Log.e(logTag, logTag + " ->" + s);
//        }
    }

    public static void warnLog(String logTag, String s) {
//        if (BuildConfig.Debug) {
 //       Log.w(logTag, logTag + " ->" + s);
//        }
    }
}
