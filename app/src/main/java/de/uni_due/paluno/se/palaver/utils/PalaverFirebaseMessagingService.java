package de.uni_due.paluno.se.palaver.utils;

import android.util.Log;

import com.google.firebase.messaging.FirebaseMessagingService;
import com.google.firebase.messaging.RemoteMessage;

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
}
