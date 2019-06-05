package com.example.palaver.utils.api;

import android.content.Context;
import android.content.Intent;
import android.widget.Toast;

import com.android.volley.AuthFailureError;
import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.Response.ErrorListener;
import com.android.volley.Response.Listener;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.StringRequest;
import com.android.volley.toolbox.Volley;
import com.example.palaver.activity.MainActivity;
import com.example.palaver.utils.UserCredentials;
import com.example.palaver.utils.Utils;
import com.example.palaver.utils.api.request.AddFriendApiRequest;
import com.example.palaver.utils.api.request.GetAllMessagesApiRequest;
import com.example.palaver.utils.api.request.GetFriendsApiRequest;
import com.example.palaver.utils.api.request.SendMessageApiRequest;
import com.example.palaver.utils.api.response.AddFriendApiResponse;
import com.example.palaver.utils.api.response.GetFriendsApiResponse;
import com.example.palaver.utils.api.response.SendMessageApiResponse;

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
        }, new ErrorListener() {
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
        }, new ErrorListener() {
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

    public static void addFriend(final AddFriendApiRequest data) {
        StringRequest req = new StringRequest(Request.Method.POST, url + "/api/friends/add",
                new Response.Listener<String>() {
                    @Override
                    public void onResponse(String response) {
                        AddFriendApiResponse r = Utils.deserialize(response, AddFriendApiResponse.class);
                        Utils.t(r.getInfo());
                        data.getCallback().onSuccess(r.getData());
                    }
                },
                new ErrorListener() {
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
                return Utils.serialize(data);
            }
        };

        requestQueue.add(req);
    }

    public static void getFriends(final GetFriendsApiRequest request) {
        StringRequest req = new StringRequest(Request.Method.POST, url + "/api/friends/get",
                new Response.Listener<String>() {
                    @Override
                    public void onResponse(String response) {
                        GetFriendsApiResponse r = Utils.deserialize(response, GetFriendsApiResponse.class);
                        if (r.getMsgType() == 1) {
                            request.getCallback().onSuccess(r.getData());
                        }
                    }
                },
                new ErrorListener() {
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
                return Utils.serialize(request);
            }
        };
        requestQueue.add(req);
    }

    public static void sendMessage(final SendMessageApiRequest request, final ObjectCallback callback) {
        StringRequest req = new StringRequest(Request.Method.POST, url + "/api/message/send",
                new Listener<String>() {
                    @Override
                    public void onResponse(String response) {
                        SendMessageApiResponse r = Utils.deserialize(response, SendMessageApiResponse.class);
                        if(r.getMsgType() == 0) {
                            Utils.t(r.getInfo());
                        }
                        else {
                            callback.onSuccess(r.getData().getDateTime());
                        }
                    }
                },
                new ErrorListener() {
                    @Override
                    public void onErrorResponse(VolleyError error) {
                        Utils.t("shit broke yo");
                    }
                });
        requestQueue.add(req);
    }

    public void getMessages(GetAllMessagesApiRequest request, final ObjectCallback callback) {
        StringRequest req = new StringRequest(Request.Method.POST, url + "/api/message/get",
                new Listener<String>() {
                    @Override
                    public void onResponse(String response) {

                    }
                },
                new ErrorListener(){

                    @Override
                    public void onErrorResponse(VolleyError error) {

                    }
                });
    }

    private static void onVerifySuccess(JSONObject user, Context ctx) {
        UserCredentials.createLogin(user);

        Intent intent = new Intent(ctx, MainActivity.class);
        intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
        ctx.startActivity(intent);
    }

    // some stuff to do


}
