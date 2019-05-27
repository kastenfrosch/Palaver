package com.example.palaver;

import android.app.DownloadManager;
import android.content.Context;
import android.os.AsyncTask;
import android.os.StrictMode;
import android.widget.TextView;
import android.widget.Toast;

import com.android.volley.AuthFailureError;
import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.StringRequest;
import com.android.volley.toolbox.Volley;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.DataOutputStream;
import java.io.IOException;
import java.io.StringReader;
import java.io.UnsupportedEncodingException;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;


public class RestApiConnection {

    private static RequestQueue requestQueue;
    private static String url = "http://palaver.se.paluno.uni-due.de";

    // TODO: hier kommen alle methoden rein wie login, logout, register, etc.

    public static void registerUser(JSONObject user, Context appContext) {

        final Context context = appContext;
        final String userString = user.toString();
        String registerUrl = url + "/api/user/register";

        requestQueue = Volley.newRequestQueue(context);

        StringRequest stringRequest = new StringRequest(Request.Method.POST, registerUrl, new Response.Listener<String>() {
            @Override
            public void onResponse(String response) {
                try {
                    JSONObject serverResponse = new JSONObject(response);
                    if ((int)serverResponse.get("MsgType") == 1) {
                        String erfolg = "Der Benutzer wurde erfolgreich angelegt!";
                        Toast.makeText(context, erfolg, Toast.LENGTH_LONG).show();
                    } else {
                        Toast.makeText(context, serverResponse.toString(), Toast.LENGTH_LONG).show();
                    }

                } catch (JSONException e) {
                    Toast.makeText(context, "Server Error",  Toast.LENGTH_LONG).show();
                }
            }
        }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                Toast.makeText(context, error.getMessage(), Toast.LENGTH_LONG).show();
            }
        }) {
            @Override
            public String getBodyContentType() {
                return "application/json; charset=utf-8";
            }

            @Override
            public byte[] getBody() throws AuthFailureError {
                try {
                    return userString == null ? null : userString.getBytes("utf-8");
                } catch (UnsupportedEncodingException uee) {
                    return null;
                }
            }
        };

        requestQueue.add(stringRequest);

    }

    public static boolean verifyUser(JSONObject user, Context appContext) {
        boolean isVerified = false;

        // benutze /api/user/validate

        return isVerified;
    }

    // some stuff to do


}
