package com.example.palaver;

import android.content.Context;
import android.content.SharedPreferences;
import static android.content.Context.MODE_PRIVATE;

public class UserCredentials {

    private static final String myPreferences = "UserCredentials";
    private static final String usernamePref = "Username";
    private static final String passwordPref = "Password";


    public static boolean checkLogin(Context appContext) {

        boolean hasLogin = false;
        SharedPreferences preferences = appContext
                .getSharedPreferences("UserCredentials", MODE_PRIVATE);

        if (preferences.getString("Username", null) != null ||
                preferences.getString("Password", null) != null) {
            hasLogin = true;
        }

        return hasLogin;
    }

    public static void createLogin(String user, String password, Context appContext) {

        SharedPreferences preferences = appContext
                .getSharedPreferences("UserCredentials", MODE_PRIVATE);
        SharedPreferences.Editor editor = preferences.edit();

        editor.putString("Username", user);
        editor.putString("Password", password);
        editor.commit();
    }

    public static void logout(Context appContext) {

        SharedPreferences preferences = appContext
                .getSharedPreferences("UserCredentials", MODE_PRIVATE);
        SharedPreferences.Editor editor = preferences.edit();

        editor.clear();
        editor.commit();

        // TODO: do we have to check if that was succesful?
    }

    public static String getUsername(Context appContext) {
        SharedPreferences preferences = appContext
                .getSharedPreferences("UserCredentials", MODE_PRIVATE);

        return preferences.getString("Username", null);
    }

    public static String getPassword(Context appContext) {
        SharedPreferences preferences = appContext
                .getSharedPreferences("UserCredentials", MODE_PRIVATE);

        return preferences.getString("Password", null);
    }

}
