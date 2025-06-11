package com.example.mogok.utils;

import android.content.Context;
import android.content.SharedPreferences;

public class AuthManager {
    private static final String PREF_NAME = "AuthPrefs";
    private static final String KEY_AUTH_TOKEN = "AUTH_TOKEN";
    private static final String KEY_USER_NAME = "USER_NAME";

    private SharedPreferences sharedPreferences;
    private SharedPreferences.Editor editor;

    public AuthManager(Context context) {
        sharedPreferences = context.getSharedPreferences(PREF_NAME, Context.MODE_PRIVATE);
        editor = sharedPreferences.edit();
    }

    public void saveAuthToken(String token) {
        editor.putString(KEY_AUTH_TOKEN, token);
        editor.apply();
    }

    public String getAuthToken() {
        return sharedPreferences.getString(KEY_AUTH_TOKEN, null);
    }

    public void saveUserName(String name) {
        editor.putString(KEY_USER_NAME, name);
        editor.apply();
    }

    public String getUserName() {
        return sharedPreferences.getString(KEY_USER_NAME, "Guest");
    }

    public boolean isLoggedIn() {
        return getAuthToken() != null;
    }

    public void logout() {
        editor.clear();
        editor.apply();
    }
}