package com.example.palaver.utils.api;

import android.content.Context;
import android.content.Intent;
import android.util.Log;
import android.widget.Toast;

import com.android.volley.AuthFailureError;
import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.StringRequest;
import com.android.volley.toolbox.Volley;
import com.example.palaver.activity.MainActivity;
import com.example.palaver.utils.UserCredentials;
import com.example.palaver.utils.Utils;
import com.google.gson.FieldNamingPolicy;
import com.google.gson.Gson;
import com.google.gson.GsonBuilder;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.UnsupportedEncodingException;


public class RestApiConnection {

    private static RequestQueue requestQueue;
    private static Context ctx;
    private static String url = "http://palaver.se.paluno.uni-due.de";

    // TODO: hier kommen alle methoden rein wie login, logout, register, etc.

    public static void init(Context _ctx) {
        requestQueue = Volley.newRequestQueue(_ctx);
        ctx = _ctx;
    }

    public static void registerUser(JSONObject user) {

        final Context context = ctx;
        final String userString = user.toString();
        String registerUrl = url + "/api/user/register";

        StringRequest stringRequest = new StringRequest(Request.Method.POST, registerUrl, new Response.Listener<String>() {
            @Override
            public void onResponse(String response) {
                try {
                    JSONObject serverResponse = new JSONObject(response);
                    if ((int) serverResponse.get("MsgType") == 1) {
                        String erfolg = "Der Benutzer wurde erfolgreich angelegt!";
                        Toast.makeText(context, erfolg, Toast.LENGTH_LONG).show();
                    } else {
                        Toast.makeText(context, serverResponse.toString(), Toast.LENGTH_LONG).show();
                    }

                } catch (JSONException e) {
                    Toast.makeText(context, "Server Error", Toast.LENGTH_LONG).show();
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

    public static void verifyPassword(final JSONObject user) {

        final Context context = ctx;
        final String userString = user.toString();
        String verifyPasswordUrl = url + "/api/user/validate";

        requestQueue = Volley.newRequestQueue(context);

        StringRequest stringRequest = new StringRequest(Request.Method.POST, verifyPasswordUrl, new Response.Listener<String>() {
            @Override
            public void onResponse(String response) {
                try {
                    JSONObject serverResponse = new JSONObject(response);
                    if ((int) serverResponse.get("MsgType") == 1) {
                        String successMessage = "Der Benutzer wurde erfolgreich validiert!";
                        Toast.makeText(context, successMessage, Toast.LENGTH_LONG).show();
                        onVerifySuccess(user, context);
                    } else {
                        Toast.makeText(context, serverResponse.toString(), Toast.LENGTH_LONG).show();
                    }

                } catch (JSONException e) {
                    Toast.makeText(context, "Server Error", Toast.LENGTH_LONG).show();
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

    public static void addFriend(final AddFriendRequest data, final VoidCallback callback) {
        StringRequest req = new StringRequest(Request.Method.POST, url + "/api/friends/add",
                new Response.Listener<String>() {
                    @Override
                    public void onResponse(String response) {
                        ApiResponse r = getResponse(response, ApiResponse.class);
                        Utils.t(r.getInfo());
                        if(callback != null) {
                            callback.onSuccess();
                        }
                    }
                },
                new Response.ErrorListener() {
                    @Override
                    public void onErrorResponse(VolleyError error) {
                        Utils.t("Could not add friend: " + error.getMessage());
                    }
                }) {

            @Override
            public String getBodyContentType() {
                return "application/json; charset=utf-8";
            }

            @Override
            public byte[] getBody() {
                return serialize(data);
            }
        };

        requestQueue.add(req);
    }

    public static void getFriends(final ApiRequest data, final VolleyCallback callback) {
        StringRequest req = new StringRequest(Request.Method.POST, url + "/api/friends/get",
                new Response.Listener<String>() {
                    @Override
                    public void onResponse(String response) {
                        GetFriendsApiResponse r = getResponse(response, GetFriendsApiResponse.class);
                        if (r.getMsgType() == 1) {
                            callback.onSuccess(r.getData());
                        }
                    }
                },
                new Response.ErrorListener() {
                    @Override
                    public void onErrorResponse(VolleyError error) {
                        Utils.t("Could not get friends: " + error.getMessage());
                    }
                }) {
            @Override
            public String getBodyContentType() {
                return "application/json; charset=utf-8";
            }

            @Override
            public byte[] getBody() throws AuthFailureError {
                return serialize(data);
            }
        };
        requestQueue.add(req);
    }

    private static void onVerifySuccess(JSONObject user, Context ctx) {
        UserCredentials.createLogin(user);

        Intent intent = new Intent(ctx, MainActivity.class);
        intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
        ctx.startActivity(intent);
    }

    private static <T extends ApiResponse> T getResponse(String s, Class<T> clazz) {
        GsonBuilder b = new GsonBuilder();
        b.setFieldNamingPolicy(FieldNamingPolicy.UPPER_CAMEL_CASE);
        Gson gson = b.create();

        return gson.fromJson(s, clazz);
    }

    private static byte[] serialize(Object obj) {
        Gson gson = new GsonBuilder().setFieldNamingPolicy(FieldNamingPolicy.UPPER_CAMEL_CASE).create();
        return gson.toJson(obj).getBytes();
    }

    // some stuff to do


}
