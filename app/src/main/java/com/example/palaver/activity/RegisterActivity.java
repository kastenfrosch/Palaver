package com.example.palaver.activity;

import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;

import com.example.palaver.R;
import com.example.palaver.utils.JsonObjectWizard;
import com.example.palaver.utils.api.RestApiConnection;
import com.example.palaver.utils.UserCredentials;

public class RegisterActivity extends AppCompatActivity {

    private EditText usernameField;
    private EditText passwordField;
    private Button sendBtn;
    private Button registerBtn;
    private Button logoutBtn;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_register);

        usernameField = (EditText) findViewById(R.id.register_username);
        passwordField = (EditText) findViewById(R.id.register_password);
        sendBtn = (Button) findViewById(R.id.register_send_btn);
        registerBtn = (Button) findViewById(R.id.register_login_btn);
        logoutBtn = (Button) findViewById(R.id.register_logout_btn);

        sendBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String username = String.valueOf(usernameField.getText());
                String password = String.valueOf(passwordField.getText());

                RestApiConnection
                        .registerUser(JsonObjectWizard
                                .createUser(username, password));


                // creating preferences
                UserCredentials.createLogin(username, password);

                // dann stage wechseln zum login!
                Intent intent = new Intent(RegisterActivity.this, LoginActivity.class);
                startActivity(intent);
                finish();

            }
        });

        registerBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                Intent intent = new Intent(RegisterActivity.this, LoginActivity.class);
                startActivity(intent);
                finish();

            }
        });

        logoutBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                // deleting preferences to log out
                UserCredentials.logout();

            }
        });


    }
}
