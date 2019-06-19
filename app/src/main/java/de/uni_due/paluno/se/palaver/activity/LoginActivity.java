package de.uni_due.paluno.se.palaver.activity;

import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import com.example.palaver.R;

import de.uni_due.paluno.se.palaver.utils.UserPrefs;
import de.uni_due.paluno.se.palaver.utils.Utils;
import de.uni_due.paluno.se.palaver.utils.api.MagicCallback;
import de.uni_due.paluno.se.palaver.utils.api.PalaverApi;
import de.uni_due.paluno.se.palaver.utils.api.request.ValidateUserApiRequest;
import de.uni_due.paluno.se.palaver.utils.api.response.ApiResponse;
import de.uni_due.paluno.se.palaver.utils.storage.Storage;

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

                ValidateUserApiRequest req = new ValidateUserApiRequest(new MagicCallback<Void>() {
                    @Override
                    public void onSuccess(Void v) {

                        Storage.I().setUsername(usernameField.getText().toString());
                        Storage.I().setPassword(passwordField.getText().toString());
                        Storage.I().persist();
                        Intent intent = new Intent(LoginActivity.this, MainActivity.class);
                        startActivity(intent);
                        finish();
                    }

                    @Override
                    public void onError(ApiResponse r) {
                        Utils.t(r.getInfo());
                    }
                });
                req.setUsername(usernameField.getText().toString());
                req.setPassword(passwordField.getText().toString());
                PalaverApi.execute(req);
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

                if (UserPrefs.checkLogin()) {
                    usernameField.setText(UserPrefs.getUsername());
                    passwordField.setText(UserPrefs.getPassword());
                } else {
                    Toast.makeText(getApplicationContext(),
                            "Es sind keine Login-Daten vorhanden!", Toast.LENGTH_LONG).show();
                }

            }
        });


    }
}
