package com.example.idaon;

import android.content.Context;
import android.content.SharedPreferences;

import java.util.HashMap;
import java.util.Map;

public class SaveSharedPreference {

    private static boolean DEFULT_VALUE_BOOLEAN = false;

    private static String userID, userPW;

    private static final String PREFERENCES_NAME = "my_preferences";

    static SharedPreferences getPreferences (Context ctx) {
        return ctx.getSharedPreferences(PREFERENCES_NAME, Context.MODE_PRIVATE);
    }

    public static void setLogin (Context ctx, String uid, String upw) {
        SharedPreferences prefs = getPreferences(ctx);
        SharedPreferences.Editor editor = prefs.edit();
        editor.putString("userID", uid);
        editor.putString("userPW", upw);
        editor.apply();
    }

    public static Map<String, String> getLogin (Context ctx) {
        SharedPreferences prefs = getPreferences(ctx);
        Map<String, String> Login = new HashMap<>();
        String userID = prefs.getString("userID", "");
        String userPW = prefs.getString("userPW", "");

        Login.put("userID", userID);
        Login.put("userPW", userPW);

        return Login;
    }

    public static void setSaveLoginData(Context ctx, String key, boolean value) {
        SharedPreferences prefs = getPreferences(ctx);
        SharedPreferences.Editor editor = prefs.edit();
        editor.putBoolean(key, value);
        editor.apply();
    }

    public static boolean getSaveLoginData(Context ctx, String key) {
        SharedPreferences prefs = getPreferences(ctx);
        return prefs.getBoolean(key, DEFULT_VALUE_BOOLEAN);
    }

    public static void setUserID(Context ctx, String uid) {
        SharedPreferences prefs = getPreferences(ctx);
        SharedPreferences.Editor editor = prefs.edit();
        editor.putString("userID", uid);
        editor.apply();
    }

    public static void setUserPW(Context ctx, String upw) {
        SharedPreferences prefs = getPreferences(ctx);
        SharedPreferences.Editor editor = prefs.edit();
        editor.putString("userPW", upw);
        editor.apply();
    }

    public static String getUserID(Context ctx) {
        SharedPreferences prefs = getPreferences(ctx);
        return prefs.getString("userID", "");
    }

    public static String getUserPW(Context ctx) {
        SharedPreferences prefs = getPreferences(ctx);
        return prefs.getString("userPW", "");
    }

    public static void clearShared(Context ctx) {
        SharedPreferences prefs = getPreferences(ctx);
        SharedPreferences.Editor editor = prefs.edit();
        editor.clear();
        editor.apply();
    }
}
