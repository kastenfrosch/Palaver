package de.uni_due.paluno.se.palaver.activity;

import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;

import com.example.palaver.R;

import de.uni_due.paluno.se.palaver.utils.Utils;
import de.uni_due.paluno.se.palaver.utils.api.MagicCallback;
import de.uni_due.paluno.se.palaver.utils.api.PalaverApi;
import de.uni_due.paluno.se.palaver.utils.api.request.RegisterUserApiRequest;
import de.uni_due.paluno.se.palaver.utils.api.response.ApiResponse;
import de.uni_due.paluno.se.palaver.utils.storage.Storage;

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

        usernameField = findViewById(R.id.register_username);
        passwordField = findViewById(R.id.register_password);
        sendBtn = findViewById(R.id.register_send_btn);
        registerBtn = findViewById(R.id.register_login_btn);
        logoutBtn = findViewById(R.id.register_logout_btn);

        sendBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                final String username = String.valueOf(usernameField.getText());
                final String password = String.valueOf(passwordField.getText());

                RegisterUserApiRequest req = new RegisterUserApiRequest(new MagicCallback<Void>() {
                    @Override
                    public void onSuccess(Void v) {
                        Storage.getInstance().setUsername(username);
                        Storage.getInstance().setPassword(password);
                        Storage.getInstance().persist();

                        Intent intent = new Intent(RegisterActivity.this, LoginActivity.class);
                        startActivity(intent);
                        finish();

                    }

                    @Override
                    public void onError(ApiResponse r) {
                        Utils.t(r.getInfo());
                    }
                });
                req.setUsername(username);
                req.setPassword(username);
                PalaverApi.execute(req);
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
                Storage.getInstance().setUsername(null);
                Storage.getInstance().setPassword(null);
            }
        });


    }
}
