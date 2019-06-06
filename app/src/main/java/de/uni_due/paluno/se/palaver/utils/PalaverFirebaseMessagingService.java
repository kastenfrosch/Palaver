package de.uni_due.paluno.se.palaver.utils;

import android.util.Log;

import com.google.firebase.messaging.FirebaseMessagingService;
import com.google.firebase.messaging.RemoteMessage;

import de.uni_due.paluno.se.palaver.utils.api.MagicCallback;
import de.uni_due.paluno.se.palaver.utils.api.RestApiConnection;
import de.uni_due.paluno.se.palaver.utils.api.request.UpdatePushTokenApiRequest;

public class PalaverFirebaseMessagingService extends FirebaseMessagingService {

    public PalaverFirebaseMessagingService() {
        Log.d("***********", "UP AND RUNNING");
    }
    @Override
    public void onMessageReceived(RemoteMessage remoteMessage) {
        Log.d("*****", "From: " + remoteMessage.getFrom());
        if(remoteMessage.getData().size() > 0) {
            Log.d("*****", "Remote data payload: " + remoteMessage.getData());
        }
        super.onMessageReceived(remoteMessage);
    }

    @Override
    public void onDeletedMessages() {
        super.onDeletedMessages();
    }

    @Override
    public void onNewToken(String s) {
        Utils.t("NEW PUSH TOKEN");
        updateTokenOnServer(s);
    }

    public static void updateTokenOnServer(String token) {
        UpdatePushTokenApiRequest req = new UpdatePushTokenApiRequest(new MagicCallback<Void>() {
            @Override
            public void onSuccess(Void aVoid) {
                Utils.t("Push token updated");
            }
        });
        req.setPushToken(token);
        RestApiConnection.updatePushToken(req);
    }
}
