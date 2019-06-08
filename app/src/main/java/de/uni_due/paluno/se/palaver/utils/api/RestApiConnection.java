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
import de.uni_due.paluno.se.palaver.utils.api.request.ApiRequest;
import de.uni_due.paluno.se.palaver.utils.api.response.ApiResponse;

import org.json.JSONException;
import org.json.JSONObject;

import java.nio.charset.StandardCharsets;


public class RestApiConnection {

    private static RequestQueue requestQueue;
    private static Context ctx;
    private static String url = "http://palaver.se.paluno.uni-due.de";

    public static void init(Context _ctx) {
        requestQueue = Volley.newRequestQueue(_ctx);
        ctx = _ctx;
    }

    //TODO: replace with Request/Response pattern, called from ??
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

    //TODO: replace with Request/Response pattern, called from UserPrefs
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

    //TODO: find out where this is called from and replace it with the Request/Response pattern
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
