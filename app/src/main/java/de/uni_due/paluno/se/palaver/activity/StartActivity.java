package de.uni_due.paluno.se.palaver.activity;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;

import de.uni_due.paluno.se.palaver.utils.api.RestApiConnection;
import de.uni_due.paluno.se.palaver.utils.UserCredentials;
import de.uni_due.paluno.se.palaver.utils.Utils;

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
