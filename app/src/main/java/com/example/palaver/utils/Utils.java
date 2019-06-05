package com.example.palaver.utils;

import android.content.Context;
import android.widget.Toast;

import com.example.palaver.utils.api.response.ApiResponse;
import com.google.gson.FieldNamingPolicy;
import com.google.gson.Gson;
import com.google.gson.GsonBuilder;

public class Utils {

    private static Context ctx;

    public static void init(Context _ctx) {
        ctx = _ctx;
    }
    public static void t(String text) {
        Toast.makeText(ctx, text, Toast.LENGTH_LONG).show();
    }

    public static <T> T deserialize(String s, Class<T> clazz) {
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
