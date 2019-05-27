package com.example.palaver;

import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;

public class LoginActivity extends AppCompatActivity {

    private EditText usernameField;
    private EditText passwordField;
    private Button sendBtn;
    private Button registerBtn;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);

        usernameField = findViewById(R.id.login_username);
        passwordField = findViewById(R.id.login_password);
        sendBtn = findViewById(R.id.login_send_btn);
        registerBtn = findViewById(R.id.login_register_btn);

        sendBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String username = String.valueOf(usernameField.getText());
                String password = String.valueOf(passwordField.getText());
                RestApiConnection
                        .registerUser(JsonObjectWizard
                                .registerUser(username, password), getApplicationContext());

                // check if user credentials are ok
                if (RestApiConnection
                        .verifyUser(JsonObjectWizard
                                .registerUser(username, password), getApplicationContext())) {
                    // creating preferences
                    UserCredentials.createLogin(username, password, getApplicationContext());
                    // change to main menu
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



    }
}
