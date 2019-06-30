package de.uni_due.paluno.se.palaver.utils.api;

import android.util.Log;

import com.android.volley.AuthFailureError;
import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response.ErrorListener;
import com.android.volley.Response.Listener;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.StringRequest;
import com.android.volley.toolbox.Volley;

import de.uni_due.paluno.se.palaver.utils.ContextAware;
import de.uni_due.paluno.se.palaver.utils.Utils;
import de.uni_due.paluno.se.palaver.utils.api.request.ApiRequest;
import de.uni_due.paluno.se.palaver.utils.api.response.ApiResponse;


public class PalaverApi extends ContextAware {

    private static RequestQueue requestQueue;

    public static void execute(final ApiRequest request) {
        if (requestQueue == null) {
            requestQueue = Volley.newRequestQueue(getCtx());
        }
        StringRequest req = new StringRequest(Request.Method.POST, request.getFullApiEndpoint(),
                new Listener<String>() {
                    @Override
                    public void onResponse(String response) {
                        ApiResponse r = Utils.deserializeApiResponse(response, request.getResponseType());
                        Log.d("Palaver*****", r.getInfo());
                        if (r.getMsgType() == 0) {
                            request.getCallback().onError(r);
                        } else {
                            //noinspection unchecked
                            request.getCallback().onSuccess(r.getData());
                        }
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
}
