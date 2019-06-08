package de.uni_due.paluno.se.palaver.utils;

import android.content.Context;
import android.content.SharedPreferences;
import android.widget.Toast;

import org.json.JSONException;
import org.json.JSONObject;

import static android.content.Context.MODE_PRIVATE;

public class UserPrefs {

    private static String myPreferences = "UserPrefs";
    private static String usernamePref = "Username";
    private static String passwordPref = "Password";
    private static Context ctx;

    public static void initialize(Context _ctx) {
        ctx = _ctx;
    }

    public static boolean checkLogin() {
        boolean hasLogin = false;
        SharedPreferences preferences = ctx
                .getSharedPreferences(myPreferences, MODE_PRIVATE);

        if (preferences.getString(usernamePref, null) != null ||
                preferences.getString(passwordPref, null) != null) {
            hasLogin = true;
        }

        return hasLogin;
    }

    public static void createLogin(JSONObject user) {
        try {
            createLogin(user.getString(usernamePref), user.getString(passwordPref));
        } catch(JSONException ex) {
            throw new IllegalArgumentException("invalid user object");
        }
    }

    public static void createLogin(String user, String password) {

        SharedPreferences preferences = ctx
                .getSharedPreferences(myPreferences, MODE_PRIVATE);
        SharedPreferences.Editor editor = preferences.edit();

        editor.putString(usernamePref, user);
        editor.putString(passwordPref, password);
        editor.commit();

        Toast.makeText(ctx,"Login erfolgreich!", Toast.LENGTH_LONG).show();
    }

    public static void logout() {

        SharedPreferences preferences = ctx
                .getSharedPreferences(myPreferences, MODE_PRIVATE);
        SharedPreferences.Editor editor = preferences.edit();

        editor.clear();
        editor.commit();

        // TODO: do we have to check if that was succesful?
    }

    public static String getUsername() {
        SharedPreferences preferences = ctx
                .getSharedPreferences(myPreferences, MODE_PRIVATE);

        return preferences.getString(usernamePref, null);
    }

    public static String getPassword() {
        SharedPreferences preferences = ctx
                .getSharedPreferences(myPreferences, MODE_PRIVATE);

        return preferences.getString(passwordPref, null);
    }

}
