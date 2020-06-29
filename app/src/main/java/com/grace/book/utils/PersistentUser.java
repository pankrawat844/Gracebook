package com.grace.book.utils;

import android.content.Context;
import android.content.SharedPreferences;
import android.content.SharedPreferences.Editor;

public class PersistentUser {

    //common data
    private static final String PREFS_FILE_NAME = "PersistentUser";
    private static final String USERDetails = "user_details";
    private static final String USERID = "userid";
    private static final String USERTOKEN = "user_token";
    private static final String USERPASSWORD = "userpassword";
    private static final String PROFILEIMAGEPATH = "PROFILEIMAGEPATH";
    private static final String Latitude = "latitude";
    private static final String Longitude = "longitude";
    private static final String PUSHTOKEN = "PushToken";
    private static final String VERSE_DATA = "VERSE_DATA";
    private static final String VERSE_DATE = "VERSE_DATE";


    public static String getVerseDate(final Context ctx) {
        return ctx.getSharedPreferences(PersistentUser.PREFS_FILE_NAME,
                Context.MODE_PRIVATE).getString(PersistentUser.VERSE_DATE, "");
    }

    public static void setVerseDate(final Context ctx, final String data) {
        final SharedPreferences prefs = ctx.getSharedPreferences(
                PersistentUser.PREFS_FILE_NAME, Context.MODE_PRIVATE);
        final Editor editor = prefs.edit();
        editor.putString(PersistentUser.VERSE_DATE, data);
        editor.commit();
    }

    public static String getVerse(final Context ctx) {
        return ctx.getSharedPreferences(PersistentUser.PREFS_FILE_NAME,
                Context.MODE_PRIVATE).getString(PersistentUser.VERSE_DATA, "");
    }

    public static void setVerse(final Context ctx, final String data) {
        final SharedPreferences prefs = ctx.getSharedPreferences(
                PersistentUser.PREFS_FILE_NAME, Context.MODE_PRIVATE);
        final Editor editor = prefs.edit();
        editor.putString(PersistentUser.VERSE_DATA, data);
        editor.commit();
    }


    public static String getPushToken(final Context ctx) {
        return ctx.getSharedPreferences(PersistentUser.PREFS_FILE_NAME,
                Context.MODE_PRIVATE).getString(PersistentUser.PUSHTOKEN, "");
    }

    public static void setPushToken(final Context ctx, final String data) {
        final SharedPreferences prefs = ctx.getSharedPreferences(
                PersistentUser.PREFS_FILE_NAME, Context.MODE_PRIVATE);
        final Editor editor = prefs.edit();
        editor.putString(PersistentUser.PUSHTOKEN, data);
        editor.commit();
    }


    public static String getLongitude(final Context ctx) {
        return ctx.getSharedPreferences(PersistentUser.PREFS_FILE_NAME,
                Context.MODE_PRIVATE).getString(PersistentUser.Longitude, "0.0");
    }

    public static void setLongitude(final Context ctx, final String data) {
        final SharedPreferences prefs = ctx.getSharedPreferences(
                PersistentUser.PREFS_FILE_NAME, Context.MODE_PRIVATE);
        final Editor editor = prefs.edit();
        editor.putString(PersistentUser.Longitude, data);
        editor.commit();
    }

    public static String getLatitude(final Context ctx) {
        return ctx.getSharedPreferences(PersistentUser.PREFS_FILE_NAME,
                Context.MODE_PRIVATE).getString(PersistentUser.Latitude, "0.0");
    }

    public static void setLatitude(final Context ctx, final String data) {
        final SharedPreferences prefs = ctx.getSharedPreferences(
                PersistentUser.PREFS_FILE_NAME, Context.MODE_PRIVATE);
        final Editor editor = prefs.edit();
        editor.putString(PersistentUser.Latitude, data);
        editor.commit();
    }


    public static String getImagePath(final Context ctx) {
        return ctx.getSharedPreferences(PersistentUser.PREFS_FILE_NAME,
                Context.MODE_PRIVATE).getString(PersistentUser.PROFILEIMAGEPATH, "");
    }

    public static void setImagePath(final Context ctx, final String data) {
        final SharedPreferences prefs = ctx.getSharedPreferences(
                PersistentUser.PREFS_FILE_NAME, Context.MODE_PRIVATE);
        final Editor editor = prefs.edit();
        editor.putString(PersistentUser.PROFILEIMAGEPATH, data);
        editor.commit();
    }

    public static String getUserToken(final Context ctx) {
        return ctx.getSharedPreferences(PersistentUser.PREFS_FILE_NAME,
                Context.MODE_PRIVATE).getString(PersistentUser.USERTOKEN, "");
    }

    public static void setUserToken(final Context ctx, final String data) {
        final SharedPreferences prefs = ctx.getSharedPreferences(
                PersistentUser.PREFS_FILE_NAME, Context.MODE_PRIVATE);
        final Editor editor = prefs.edit();
        editor.putString(PersistentUser.USERTOKEN, data);
        editor.commit();
    }

    public static String getUserDetails(final Context ctx) {
        return ctx.getSharedPreferences(PersistentUser.PREFS_FILE_NAME,
                Context.MODE_PRIVATE).getString(PersistentUser.USERDetails, "");
    }

    public static void setUserDetails(final Context ctx, final String data) {
        final SharedPreferences prefs = ctx.getSharedPreferences(
                PersistentUser.PREFS_FILE_NAME, Context.MODE_PRIVATE);
        final Editor editor = prefs.edit();
        editor.putString(PersistentUser.USERDetails, data);
        editor.commit();
    }

    public static String getUserpassword(final Context ctx) {
        return ctx.getSharedPreferences(PersistentUser.PREFS_FILE_NAME,
                Context.MODE_PRIVATE).getString(PersistentUser.USERPASSWORD, "");
    }

    public static void setUserpassword(final Context ctx, final String data) {
        final SharedPreferences prefs = ctx.getSharedPreferences(
                PersistentUser.PREFS_FILE_NAME, Context.MODE_PRIVATE);
        final Editor editor = prefs.edit();
        editor.putString(PersistentUser.USERPASSWORD, data);
        editor.commit();
    }


    public static String getUserID(final Context ctx) {
        return ctx.getSharedPreferences(PREFS_FILE_NAME, Context.MODE_PRIVATE).getString(USERID, "");
    }

    public static void setUserID(final Context ctx, final String id) {
        final SharedPreferences prefs = ctx.getSharedPreferences(
                PREFS_FILE_NAME, Context.MODE_PRIVATE);
        final Editor editor = prefs.edit();
        editor.putString(USERID, id);
        editor.commit();
    }

    public static void logOut(Context c) {
        final SharedPreferences prefs = c.getSharedPreferences(PREFS_FILE_NAME,
                Context.MODE_PRIVATE);
        prefs.edit().putBoolean("LOGIN", false).commit();

    }

    public static void setLogin(Context c) {
        final SharedPreferences prefs = c.getSharedPreferences(PREFS_FILE_NAME,
                Context.MODE_PRIVATE);
        prefs.edit().putBoolean("LOGIN", true).commit();
    }

    public static boolean isLogged(Context c) {
        final SharedPreferences prefs = c.getSharedPreferences(PREFS_FILE_NAME,
                Context.MODE_PRIVATE);
        return prefs.getBoolean("LOGIN", false);
    }


    public static void resetAllData(Context c) {
        setUserID(c, "");
        logOut(c);

    }

}
