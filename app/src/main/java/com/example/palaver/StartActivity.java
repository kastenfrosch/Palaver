package com.example.palaver;

import android.content.Context;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import com.android.volley.AuthFailureError;
import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.StringRequest;
import com.android.volley.toolbox.Volley;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.UnsupportedEncodingException;

public class StartActivity extends AppCompatActivity {

    private EditText usernameField;
    private EditText passwordField;
    private Button sendBtn;
    private  RequestQueue requestQueue;
    private  String url = "http://palaver.se.paluno.uni-due.de";


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_start);

        usernameField = findViewById(R.id.start_username);
        passwordField = findViewById(R.id.start_password);
        sendBtn = findViewById(R.id.start_sendBTN);

        sendBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String username = String.valueOf(usernameField.getText());
                String password = String.valueOf(passwordField.getText());
                RestApiConnection
                        .registerUser(JsonObjectWizard
                                .registerUser(username, password), getApplicationContext());

                // TODO: dann stage wechseln zum hauptmenü!

            }
        });


    }




}
