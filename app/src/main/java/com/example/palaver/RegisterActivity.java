package com.example.palaver;

import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;

public class RegisterActivity extends AppCompatActivity {

    private EditText usernameField;
    private EditText passwordField;
    private Button sendBtn;
    private Button retrieveBtn;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_register);

        usernameField = findViewById(R.id.register_username);
        passwordField = findViewById(R.id.register_password);
        sendBtn = findViewById(R.id.register_send_btn);
        retrieveBtn = findViewById(R.id.register_retrieve_btn);

        sendBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String username = String.valueOf(usernameField.getText());
                String password = String.valueOf(passwordField.getText());
                RestApiConnection
                        .registerUser(JsonObjectWizard
                                .registerUser(username, password), getApplicationContext());


                // creating preferences
                UserCredentials.createLogin(username, password, getApplicationContext());

                usernameField.getText().clear();
                passwordField.getText().clear();

                // TODO: dann stage wechseln zum login!

            }
        });

        retrieveBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String username = UserCredentials.getPassword(getApplicationContext());
                String password = UserCredentials.getPassword(getApplicationContext());

                usernameField.setText(username);
                passwordField.setText(password);

            }
        });


    }
}
