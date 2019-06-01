package com.example.palaver;

import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

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

        usernameField = findViewById(R.id.login_username);
        passwordField = findViewById(R.id.login_password);
        sendBtn = findViewById(R.id.login_send_btn);
        registerBtn = findViewById(R.id.login_register_btn);
        getCredsBtn = findViewById(R.id.login_getcred_btn);

        sendBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String username = String.valueOf(usernameField.getText());
                String password = String.valueOf(passwordField.getText());

                // RestApiConnection.registerUser(JsonObjectWizard.registerUser(username, password), getApplicationContext());

                // check if user credentials are ok
                if (RestApiConnection
                        .verifyUser(JsonObjectWizard
                                .createUser(username, password), getApplicationContext())) {
                    // creating preferences
                    UserCredentials.createLogin(username, password, getApplicationContext());

                    // TODO: change to main menu --> not yet written
                    //Intent intent = new Intent(LoginActivity.this, MainActivity.class);
                    //startActivity(intent);
                    //finish();
                }



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

                if (UserCredentials.checkLogin(getApplicationContext())) {
                    usernameField.setText(UserCredentials.getUsername(getApplicationContext()));
                    passwordField.setText(UserCredentials.getPassword(getApplicationContext()));
                } else {
                    Toast.makeText(getApplicationContext(),
                            "Es sind keine Login-Daten vorhanden!", Toast.LENGTH_LONG).show();
                }

            }
        });


    }
}
