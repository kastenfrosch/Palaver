package com.example.palaver;

import android.app.DownloadManager;
import android.os.AsyncTask;
import android.os.StrictMode;
import android.widget.TextView;

import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.toolbox.StringRequest;
import com.android.volley.toolbox.Volley;

import org.json.JSONObject;

import java.io.DataOutputStream;
import java.io.IOException;
import java.io.StringReader;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;


public class RestApiConnection {
    private static RequestQueue requestQueue;
    private static String url = "http://palaver.se.paluno.uni-due.de";


}
