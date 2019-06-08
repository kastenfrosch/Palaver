package de.uni_due.paluno.se.palaver.activity;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;

import de.uni_due.paluno.se.palaver.utils.UserPrefs;
import de.uni_due.paluno.se.palaver.utils.api.RestApiConnection;
import de.uni_due.paluno.se.palaver.utils.Utils;

public class StartActivity extends Activity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        //gotta init this first
        RestApiConnection.init(getApplicationContext());
        Utils.init(getApplicationContext());
        UserPrefs.initialize(getApplicationContext());

        // launch according to credentials
        if (UserPrefs.checkLogin()) {
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
