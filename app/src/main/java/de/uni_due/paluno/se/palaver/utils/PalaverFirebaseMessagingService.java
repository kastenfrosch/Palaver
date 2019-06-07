package de.uni_due.paluno.se.palaver.utils;

import android.util.Log;

import com.google.firebase.messaging.FirebaseMessagingService;
import com.google.firebase.messaging.RemoteMessage;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.LinkedList;
import java.util.List;

import de.uni_due.paluno.se.palaver.activity.MainActivity;
import de.uni_due.paluno.se.palaver.utils.api.ChatMessage;
import de.uni_due.paluno.se.palaver.utils.api.MagicCallback;
import de.uni_due.paluno.se.palaver.utils.api.RestApiConnection;
import de.uni_due.paluno.se.palaver.utils.api.request.UpdatePushTokenApiRequest;

public class PalaverFirebaseMessagingService extends FirebaseMessagingService {

    private static MainActivity mainActivity;
    private static List<PalaverPushMessage> queuedMessages = new ArrayList<>();

    public static void setMainActivity(MainActivity mainActivity) {
        PalaverFirebaseMessagingService.mainActivity = mainActivity;
        if(queuedMessages.size() > 0) {
            Iterator<PalaverPushMessage> i = queuedMessages.iterator();
            while(i.hasNext()) {
                mainActivity.onFirebasePushMessageReceived(i.next());
                i.remove();
            }
        }
    }

    @Override
    public void onMessageReceived(RemoteMessage remoteMessage) {
        super.onMessageReceived(remoteMessage);
        if(remoteMessage.getData().size() > 0) {
            PalaverPushMessage pushMessage = new PalaverPushMessage();
            Log.d("*", remoteMessage.getData().toString());
            pushMessage.setPreview(remoteMessage.getData().get("preview"));
            pushMessage.setSender(remoteMessage.getData().get("sender"));
            if(mainActivity == null) {
                queuedMessages.add(pushMessage);
            } else {

                mainActivity.onFirebasePushMessageReceived(pushMessage);
            }
        }

    }



    @Override
    public void onDeletedMessages() {
        super.onDeletedMessages();
    }

    @Override
    public void onNewToken(String s) {
        super.onNewToken(s);
        updateTokenOnServer(s);
    }

    public static void updateTokenOnServer(String token) {
        UpdatePushTokenApiRequest req = new UpdatePushTokenApiRequest(new MagicCallback<Void>() {
            @Override
            public void onSuccess(Void aVoid) {
                Log.d("*****", "push token updated @ palaver api");
            }
        });
        req.setPushToken(token);
        RestApiConnection.updatePushToken(req);
    }
}
