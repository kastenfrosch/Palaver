package com.example.palaver;

import org.json.JSONException;
import org.json.JSONObject;

public class JsonObjectWizard {

    public static JSONObject registerUser(String username, String password) {
        JSONObject user = new JSONObject();

        try {
            user.put("Username", username);
            user.put("Password", password);
        } catch (JSONException e) {
            e.printStackTrace();
        }
       // System.out.println(user);
        return user;
    }


}
