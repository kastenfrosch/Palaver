package de.uni_due.paluno.se.palaver.utils;

import android.arch.lifecycle.Lifecycle;
import android.arch.lifecycle.LifecycleObserver;
import android.arch.lifecycle.OnLifecycleEvent;
import android.arch.lifecycle.ProcessLifecycleOwner;
import android.support.v4.app.Fragment;
import android.support.v4.app.NotificationCompat;
import android.support.v4.app.NotificationManagerCompat;
import android.util.Log;

import com.example.palaver.R;
import com.google.firebase.messaging.FirebaseMessagingService;
import com.google.firebase.messaging.RemoteMessage;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

import de.uni_due.paluno.se.palaver.ChatFragment;
import de.uni_due.paluno.se.palaver.activity.MainActivity;
import de.uni_due.paluno.se.palaver.utils.api.MagicCallback;
import de.uni_due.paluno.se.palaver.utils.api.PalaverApi;
import de.uni_due.paluno.se.palaver.utils.api.request.UpdatePushTokenApiRequest;

public class PalaverFirebaseMessagingService extends FirebaseMessagingService {

    private static MainActivity mainActivity;
    private static List<PalaverPushMessage> queuedMessages = new ArrayList<>();
    private int notifCounter = 0;

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
            pushMessage.setPreview(remoteMessage.getData().get("preview"));
            pushMessage.setSender(remoteMessage.getData().get("sender"));
            if(mainActivity == null) {
                queuedMessages.add(pushMessage);
            } else {
                mainActivity.onFirebasePushMessageReceived(pushMessage);
                ChatFragment chatFrag = (ChatFragment) mainActivity.getSupportFragmentManager().findFragmentByTag(ChatFragment.TAG);
//                if(chatFrag != null && chatFrag.getActiveContact() != null && chatFrag.getActiveContact().equals(pushMessage.getSender())) {
//                    return;
//                }
                NotificationCompat.Builder builder = new NotificationCompat.Builder(mainActivity, "PUSH_YAY")
                        .setContentTitle("New message")
                        .setSmallIcon(R.drawable.ic_notification)
                        .setContentText(pushMessage.getPreview())
                        .setPriority(NotificationCompat.PRIORITY_DEFAULT)
                        .setAutoCancel(true);
                NotificationManagerCompat nm = NotificationManagerCompat.from(mainActivity);
                nm.notify(notifCounter++, builder.build());
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
