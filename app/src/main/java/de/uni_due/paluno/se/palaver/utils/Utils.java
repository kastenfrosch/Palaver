package de.uni_due.paluno.se.palaver.utils;

import android.content.Context;
import android.widget.Toast;

import com.google.gson.FieldNamingPolicy;
import com.google.gson.Gson;
import com.google.gson.GsonBuilder;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Locale;

import de.uni_due.paluno.se.palaver.utils.api.response.ApiResponse;

public class Utils extends ContextAware {

    public static void t(String text) {
        Toast.makeText(getCtx(), text, Toast.LENGTH_LONG).show();
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

    private static final SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss", Locale.GERMANY);
    public static Date parseDateTime(String dt) {
        String cut = dt.replaceAll("\\.\\d+$", "");
        try {
            Date d = sdf.parse(cut);
            return d;
        } catch(ParseException e) {
            throw new IllegalArgumentException("unable to parse datetime " + dt);
        }
    }
    public static String stringifyDateTime(Date d) {
        return sdf.format(d);
    }
}
