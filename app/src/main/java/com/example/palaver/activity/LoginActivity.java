package com.example.palaver.activity;

import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import com.example.palaver.R;
import com.example.palaver.utils.JsonObjectWizard;
import com.example.palaver.utils.api.RestApiConnection;
import com.example.palaver.utils.UserCredentials;

public class LoginActivity extends AppCompatActivity {

    private EditText usernameField;
    private EditText passwordField;
    private Button sendBtn;
    private Button registerBtn;
    private Button getCredsBtn;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);

        usernameField = (EditText) findViewById(R.id.login_username);
        passwordField = (EditText) findViewById(R.id.login_password);
        sendBtn = (Button) findViewById(R.id.login_send_btn);
        registerBtn = (Button) findViewById(R.id.login_register_btn);
        getCredsBtn = (Button) findViewById(R.id.login_getcred_btn);

        sendBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String username = String.valueOf(usernameField.getText());
                String password = String.valueOf(passwordField.getText());

                // RestApiConnection.createUser(JsonObjectWizard.registerUser(username, password), getApplicationContext());

                // check if user credentials are ok
                RestApiConnection
                        .verifyPassword(JsonObjectWizard
                                .createUser(username, password));
                    // creating preferences
                    // TODO: change to main menu --> not yet written
                    //Intent intent = new Intent(LoginActivity.this, MainActivity.class);
                    //startActivity(intent);
                    //finish();




            }
        });

        registerBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                Intent intent = new Intent(LoginActivity.this, RegisterActivity.class);
                startActivity(intent);
                finish();

            }
        });

        getCredsBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                if (UserCredentials.checkLogin()) {
                    usernameField.setText(UserCredentials.getUsername());
                    passwordField.setText(UserCredentials.getPassword());
                } else {
                    Toast.makeText(getApplicationContext(),
                            "Es sind keine Login-Daten vorhanden!", Toast.LENGTH_LONG).show();
                }

            }
        });


    }
}
