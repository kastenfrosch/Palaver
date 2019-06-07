package de.uni_due.paluno.se.palaver.utils;

import android.content.Context;
import android.widget.Toast;

import com.google.gson.FieldNamingPolicy;
import com.google.gson.Gson;
import com.google.gson.GsonBuilder;

import de.uni_due.paluno.se.palaver.utils.api.request.ApiRequest;
import de.uni_due.paluno.se.palaver.utils.api.response.ApiResponse;

public class Utils {

    private static Context ctx;

    public static void init(Context _ctx) {
        ctx = _ctx;
    }
    public static void t(String text) {
        Toast.makeText(ctx, text, Toast.LENGTH_LONG).show();
    }

    public static <T extends ApiResponse> T deserializeApiResponse(String s, Class<T> clazz) {
        GsonBuilder b = new GsonBuilder();
        b.setFieldNamingPolicy(FieldNamingPolicy.UPPER_CAMEL_CASE);
        Gson gson = b.create();

        return gson.fromJson(s, clazz);
    }

    public static byte[] serialize(Object obj) {
        Gson gson = new GsonBuilder().setFieldNamingPolicy(FieldNamingPolicy.UPPER_CAMEL_CASE).create();
        return gson.toJson(obj).getBytes();
    }
}
