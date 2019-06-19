package de.uni_due.paluno.se.palaver.utils;

import android.arch.lifecycle.Lifecycle;
import android.arch.lifecycle.LifecycleObserver;
import android.arch.lifecycle.OnLifecycleEvent;
import android.arch.lifecycle.ProcessLifecycleOwner;
import android.util.Log;

import com.google.firebase.messaging.FirebaseMessagingService;
import com.google.firebase.messaging.RemoteMessage;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

import de.uni_due.paluno.se.palaver.activity.MainActivity;
import de.uni_due.paluno.se.palaver.utils.api.MagicCallback;
import de.uni_due.paluno.se.palaver.utils.api.PalaverApi;
import de.uni_due.paluno.se.palaver.utils.api.request.UpdatePushTokenApiRequest;

public class PalaverFirebaseMessagingService extends FirebaseMessagingService  implements LifecycleObserver {

    private static MainActivity mainActivity;
    private static List<PalaverPushMessage> queuedMessages = new ArrayList<>();
    private boolean inBackground = false;

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
        Log.d("*", "" + remoteMessage.getMessageType());
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
    public void onNewToken(String s) {
        super.onNewToken(s);
        updateTokenOnServer(s);
    }

    @Override
    public void onCreate() {
        super.onCreate();
        ProcessLifecycleOwner.get().getLifecycle().addObserver(this);
    }

    @OnLifecycleEvent(Lifecycle.Event.ON_STOP)
    public void onAppBackgrounded() {
        Log.d("*", "app in background now");
    }

    @OnLifecycleEvent(Lifecycle.Event.ON_START)
    public void onAppForegrounded() {
        Log.d("*", "app in foreground now");
    }

    public static void updateTokenOnServer(String token) {
        UpdatePushTokenApiRequest req = new UpdatePushTokenApiRequest(new MagicCallback<Void>() {
            @Override
            public void onSuccess(Void aVoid) {
                Log.d("*****", "push token updated @ palaver api");
            }
        });
        req.setPushToken(token);
        PalaverApi.execute(req);
    }
}
