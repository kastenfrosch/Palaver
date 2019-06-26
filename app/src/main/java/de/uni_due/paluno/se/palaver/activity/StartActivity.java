package de.uni_due.paluno.se.palaver.activity;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.security.keystore.UserNotAuthenticatedException;

import java.net.UnknownHostException;

import de.uni_due.paluno.se.palaver.utils.ContextAware;
import de.uni_due.paluno.se.palaver.utils.Utils;
import de.uni_due.paluno.se.palaver.utils.api.MagicCallback;
import de.uni_due.paluno.se.palaver.utils.api.PalaverApi;
import de.uni_due.paluno.se.palaver.utils.api.request.ValidateUserApiRequest;
import de.uni_due.paluno.se.palaver.utils.api.response.ApiResponse;
import de.uni_due.paluno.se.palaver.utils.storage.Storage;

public class StartActivity extends Activity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        ContextAware.initialize(getApplicationContext());

        // TODO: check for permissions (location, see UB062)

        if (Storage.I().getUsername() != null && Storage.I().getPassword() != null) {
            ValidateUserApiRequest req = new ValidateUserApiRequest(new MagicCallback<Void>() {
                @Override
                public void onSuccess(Void aVoid) {
                    Intent intent = new Intent(StartActivity.this, MainActivity.class);
                    startActivity(intent);
                    finish();
                }

                @Override
                public void onError(Throwable t) {
                    if (t instanceof UnknownHostException) {
                        onSuccess(null);
                    } else {
                        if (t != null) {
                            Utils.t(t.getMessage());
                        }
                        goToSplash();
                    }
                }

                @Override
                public void onError(ApiResponse r) {
                    onError(new UserNotAuthenticatedException("login error: " + r.getInfo()));
                }
            });
            PalaverApi.execute(req);
        } else {
            goToSplash();
        }
    }

    private void goToSplash() {
        Intent intent = new Intent(StartActivity.this, SplashActivity.class);
        startActivity(intent);
        finish();
    }

}
