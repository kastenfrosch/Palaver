package de.uni_due.paluno.se.palaver.utils.api;

import android.content.Context;
import android.content.Intent;
import android.util.Log;
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

import de.uni_due.paluno.se.palaver.activity.MainActivity;
import de.uni_due.paluno.se.palaver.utils.UserPrefs;
import de.uni_due.paluno.se.palaver.utils.Utils;
import de.uni_due.paluno.se.palaver.utils.api.request.AddFriendApiRequest;
import de.uni_due.paluno.se.palaver.utils.api.request.ApiRequest;
import de.uni_due.paluno.se.palaver.utils.api.request.GetAllMessagesApiRequest;
import de.uni_due.paluno.se.palaver.utils.api.request.GetFriendsApiRequest;
import de.uni_due.paluno.se.palaver.utils.api.request.SendMessageApiRequest;
import de.uni_due.paluno.se.palaver.utils.api.request.UpdatePushTokenApiRequest;
import de.uni_due.paluno.se.palaver.utils.api.response.AddFriendApiResponse;
import de.uni_due.paluno.se.palaver.utils.api.response.ApiResponse;
import de.uni_due.paluno.se.palaver.utils.api.response.GetAllMessagesApiResponse;
import de.uni_due.paluno.se.palaver.utils.api.response.GetFriendsApiResponse;
import de.uni_due.paluno.se.palaver.utils.api.response.SendMessageApiResponse;

import org.json.JSONException;
import org.json.JSONObject;

import java.nio.charset.StandardCharsets;


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
                return userString == null ? null : userString.getBytes(StandardCharsets.UTF_8);
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
                return userString == null ? null : userString.getBytes(StandardCharsets.UTF_8);
            }
        };

        requestQueue.add(stringRequest);
    }

    @Deprecated
    public static void addFriend(final AddFriendApiRequest data) {
        StringRequest req = new StringRequest(Request.Method.POST, url + "/api/friends/add",
                new Response.Listener<String>() {
                    @Override
                    public void onResponse(String response) {
                        AddFriendApiResponse r = Utils.deserializeApiResponse(response, AddFriendApiResponse.class);
                        Utils.t(r.getInfo());
                        data
                                .getCallback()
                                .onSuccess(
                                        r.getData());
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

    @Deprecated
    public static void getFriends(final GetFriendsApiRequest request) {
        StringRequest req = new StringRequest(Request.Method.POST, url + "/api/friends/get",
                new Response.Listener<String>() {
                    @Override
                    public void onResponse(String response) {
                        GetFriendsApiResponse r = Utils.deserializeApiResponse(response, GetFriendsApiResponse.class);
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

    @Deprecated
    public static void sendMessage(final SendMessageApiRequest request) {
        StringRequest req = new StringRequest(Request.Method.POST, url + "/api/message/send",
                new Listener<String>() {
                    @Override
                    public void onResponse(String response) {
                        SendMessageApiResponse r = Utils.deserializeApiResponse(response, SendMessageApiResponse.class);
                        if (r.getMsgType() == 0) {
                            Utils.t(r.getInfo());
                        } else {
                            request.getCallback().onSuccess(r.getData());
                        }
                    }
                },
                new ErrorListener() {
                    @Override
                    public void onErrorResponse(VolleyError error) {
                        Utils.t("shit broke yo");
                    }
                }
        ) {
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

    @Deprecated
    public static void getMessages(final GetAllMessagesApiRequest request) {
        StringRequest req = new StringRequest(Request.Method.POST, url + "/api/message/get",
                new Listener<String>() {
                    @Override
                    public void onResponse(String response) {
                        GetAllMessagesApiResponse r = Utils.deserializeApiResponse(response, GetAllMessagesApiResponse.class);
                        if (r.getMsgType() == 1) {
                            request.getCallback().onSuccess(r.getData());
                        } else {
                            Utils.t(r.getInfo());
                        }
                    }
                },
                new ErrorListener() {
                    @Override
                    public void onErrorResponse(VolleyError error) {
                        Utils.t("sum tin wen wong");
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


    @Deprecated
    public static void updatePushToken(final UpdatePushTokenApiRequest request) {
        StringRequest req = new StringRequest(Request.Method.POST, url + "/api/user/pushtoken",
                new Listener<String>() {
                    @Override
                    public void onResponse(String response) {
                        request.getCallback().onSuccess(null);
                    }
                },
                new ErrorListener() {
                    @Override
                    public void onErrorResponse(VolleyError error) {
                        Log.e("*******", "" + error.getCause());
                        for (StackTraceElement e : error.getStackTrace()) {
                            Log.e("* ", e.getClassName() + "#" + e.getLineNumber());
                        }
                        Log.e("**", new String(error.networkResponse.data), error);
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

    private static void onVerifySuccess(JSONObject user, Context ctx) {
        UserPrefs.createLogin(user);

        Intent intent = new Intent(ctx, MainActivity.class);
        intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
        ctx.startActivity(intent);
    }

    public static void execute(final ApiRequest request) {
        StringRequest req = new StringRequest(Request.Method.POST, request.getFullApiEndpoint(),
                new Listener<String>() {
                    @Override
                    public void onResponse(String response) {
                        ApiResponse r = Utils.deserializeApiResponse(response, request.getResponseType());
                        //noinspection unchecked
                        request.getCallback().onSuccess(r.getData());
                    }
                },
                new ErrorListener() {
                    @Override
                    public void onErrorResponse(VolleyError error) {
                        request.getCallback().onError(error.getCause());
                    }
                }) {
            @Override
            public String getBodyContentType() {
                return request.getContentType();
            }

            @Override
            public byte[] getBody() throws AuthFailureError {
                return Utils.serialize(request);
            }
        };

        requestQueue.add(req);
    }

    // some stuff to do


}
