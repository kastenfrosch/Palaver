package com.example.palaver.utils;

import android.content.Context;
import android.widget.Toast;

public class Utils {

    private static Context ctx;

    public static void init(Context _ctx) {
        ctx = _ctx;
    }
    public static void t(String text) {
        Toast.makeText(ctx, text, Toast.LENGTH_LONG).show();
    }
}
