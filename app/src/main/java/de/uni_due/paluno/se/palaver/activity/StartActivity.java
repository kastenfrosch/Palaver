package de.uni_due.paluno.se.palaver.activity;

import android.Manifest;
import android.app.Activity;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.os.Bundle;
import android.security.keystore.UserNotAuthenticatedException;
import android.support.v4.app.ActivityCompat;

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

        if (ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED &&
                ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
            // no permission yet
            ActivityCompat.requestPermissions(this, new String[]{Manifest.permission.ACCESS_FINE_LOCATION}, 1);

        }else{
            // permission already given
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

    }

    private void goToSplash() {
        Intent intent = new Intent(StartActivity.this, SplashActivity.class);
        startActivity(intent);
        finish();
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, String[] permissions, int[] grantResults) {
        switch (requestCode) {
            case 1: {
                if (grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                    recreate();
                } else {
                    throw new RuntimeException("Should've given the permission...");
                }
                return;
            }
        }
    }

}
