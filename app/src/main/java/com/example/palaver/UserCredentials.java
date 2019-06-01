package com.example.palaver;

import android.content.Context;
import android.content.SharedPreferences;
import android.widget.Toast;

import static android.content.Context.MODE_PRIVATE;

public class UserCredentials {

    private static String myPreferences = "UserCredentials";
    private static String usernamePref = "Username";
    private static String passwordPref = "Password";


    public static boolean checkLogin(Context appContext) {

        boolean hasLogin = false;
        SharedPreferences preferences = appContext
                .getSharedPreferences(myPreferences, MODE_PRIVATE);

        if (preferences.getString(usernamePref, null) != null ||
                preferences.getString(passwordPref, null) != null) {
            hasLogin = true;
        }

        return hasLogin;
    }

    public static void createLogin(String user, String password, Context appContext) {

        SharedPreferences preferences = appContext
                .getSharedPreferences(myPreferences, MODE_PRIVATE);
        SharedPreferences.Editor editor = preferences.edit();

        editor.putString(usernamePref, user);
        editor.putString(passwordPref, password);
        editor.commit();

        Toast.makeText(appContext,"Login erfolgreich!", Toast.LENGTH_LONG).show();
    }

    public static void logout(Context appContext) {

        SharedPreferences preferences = appContext
                .getSharedPreferences(myPreferences, MODE_PRIVATE);
        SharedPreferences.Editor editor = preferences.edit();

        editor.clear();
        editor.commit();

        // TODO: do we have to check if that was succesful?
    }

    public static String getUsername(Context appContext) {
        SharedPreferences preferences = appContext
                .getSharedPreferences(myPreferences, MODE_PRIVATE);

        return preferences.getString(usernamePref, null);
    }

    public static String getPassword(Context appContext) {
        SharedPreferences preferences = appContext
                .getSharedPreferences(myPreferences, MODE_PRIVATE);

        return preferences.getString(passwordPref, null);
    }

}
