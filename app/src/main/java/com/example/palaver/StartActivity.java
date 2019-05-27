package com.example.palaver;

import android.app.Activity;
import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;

public class StartActivity extends Activity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        // launch according to credentials
        if (UserCredentials.checkLogin(getApplicationContext())) {
            // TODO: change stage/scene to main menu
            Intent intent = new Intent(StartActivity.this, SplashActivity.class);
            startActivity(intent);
            finish();
        } else {
            // TODO: splash screen öffnen und danach zum register/login
            Intent intent = new Intent(StartActivity.this, SplashActivity.class);
            startActivity(intent);
            finish();
        }

    }

}
