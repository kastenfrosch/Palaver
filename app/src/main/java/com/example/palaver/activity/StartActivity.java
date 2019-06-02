package com.example.palaver.activity;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;

import com.example.palaver.utils.api.RestApiConnection;
import com.example.palaver.utils.UserCredentials;
import com.example.palaver.utils.Utils;

public class StartActivity extends Activity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        //gotta init this first
        RestApiConnection.init(getApplicationContext());
        Utils.init(getApplicationContext());
        UserCredentials.initialize(getApplicationContext());

        // launch according to credentials
        if (UserCredentials.checkLogin()) {
            // TODO: change stage/scene to main menu
            Intent intent = new Intent(StartActivity.this, MainActivity.class);
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
