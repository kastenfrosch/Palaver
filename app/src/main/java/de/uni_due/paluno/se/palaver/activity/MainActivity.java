package de.uni_due.paluno.se.palaver.activity;

import android.app.ActionBar;
import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentTransaction;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;

import de.uni_due.paluno.se.palaver.ChatFragment;
import de.uni_due.paluno.se.palaver.ContactsFragment;

import com.example.palaver.R;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.iid.FirebaseInstanceId;
import com.google.firebase.iid.InstanceIdResult;

import de.uni_due.paluno.se.palaver.utils.PalaverFirebaseMessagingService;
import de.uni_due.paluno.se.palaver.utils.PalaverPushMessage;
import de.uni_due.paluno.se.palaver.utils.UserCredentials;
import de.uni_due.paluno.se.palaver.utils.Utils;

public class MainActivity extends AppCompatActivity implements ContactsFragment.OnContactSelectedListener, FragmentManager.OnBackStackChangedListener {

    @Override
    public void onAttachFragment(Fragment fragment) {
        if (fragment instanceof ContactsFragment) {
            ((ContactsFragment) fragment).setOnContactSelectedListener(this);
        }

        super.onAttachFragment(fragment);
    }

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        //setup toolbar
        Toolbar toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        getSupportActionBar().setCustomView(R.layout.actionbar);
        getSupportActionBar().setDisplayOptions(ActionBar.DISPLAY_SHOW_HOME | ActionBar.DISPLAY_SHOW_CUSTOM);


        if (savedInstanceState == null) {
            initUI();
        }

        FirebaseInstanceId.getInstance().getInstanceId().addOnCompleteListener(new OnCompleteListener<InstanceIdResult>() {
            @Override
            public void onComplete(@NonNull Task<InstanceIdResult> task) {
                if (!task.isSuccessful()) {
                    Log.w("*****", "getInstanceId failed", task.getException());
                    Utils.t("Failed to get Firebase InstanceId");
                    return;
                }
                String token = task.getResult().getToken();
                PalaverFirebaseMessagingService.updateTokenOnServer(token);
            }
        });

        PalaverFirebaseMessagingService.setMainActivity(this);

    }

    private void initUI() {
        FragmentManager fm = getSupportFragmentManager();
        ContactsFragment contactsFragment = new ContactsFragment();
        //single fragment mode mode
        if (findViewById(R.id.container_single) != null) {
            fm.beginTransaction().add(R.id.container_single, contactsFragment, ContactsFragment.TAG).commit();
            fm.addOnBackStackChangedListener(this);
            Utils.t("Single");
        } else {
            ChatFragment chatFragment = new ChatFragment();
            fm.beginTransaction()
                    .add(R.id.container_contacts, contactsFragment, ContactsFragment.TAG)
                    .add(R.id.container_chat, chatFragment, ChatFragment.TAG)
                    .commit();
            Utils.t("dual");
        }
    }

    public void onFirebasePushMessageReceived(PalaverPushMessage message) {
        ContactsFragment contacts = (ContactsFragment) getSupportFragmentManager().findFragmentByTag(ContactsFragment.TAG);
        ChatFragment chat = (ChatFragment) getSupportFragmentManager().findFragmentByTag(ChatFragment.TAG);
        if (chat != null && chat.isVisible() && chat.getActiveContact().equals(message.getSender())) {
            Log.d("*", "equals sender");
            chat.fetchNewMessages();
        } else {
            contacts.setUnread(message.getSender(), "*");
        }
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        menu.add("Logout");
        menu.add("Update push token");
        return super.onCreateOptionsMenu(menu);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        try {
            switch (item.getTitle().toString()) {
                case "Logout":
                    Utils.t("Logged out");
                    UserCredentials.logout();
                    Intent intent = new Intent(this, LoginActivity.class);
                    startActivity(intent);
                    break;
                case "Update push token":
                    //the onSuccessListener that has been registered will call the serverside update
                    FirebaseInstanceId.getInstance().getInstanceId();
                    break;
                default:
                    break;
            }
        } catch (NullPointerException ex) {
            //probably has something to do with the back button, should be safe to ignore here
        }
        return super.onOptionsItemSelected(item);
    }

    public void onFabClicked(View view) {
        ((ContactsFragment) getSupportFragmentManager().findFragmentByTag(ContactsFragment.TAG)).onFabClicked(view);
    }

    public void onSendClicked(View view) {
        ((ChatFragment) getSupportFragmentManager().findFragmentByTag(ChatFragment.TAG)).onSendClicked(view);
    }

    @Override
    public void onContactSelected(String contact) {
        FragmentManager fm = getSupportFragmentManager();

        if (findViewById(R.id.container_single) != null) { //we're in smartphone mode
            ChatFragment chatFragment = (ChatFragment) getSupportFragmentManager().findFragmentByTag(ChatFragment.TAG);
            if (chatFragment == null) {
                chatFragment = new ChatFragment();
            }
            Bundle args = new Bundle();
            args.putString("contact", contact);
            chatFragment.setArguments(args);
            FragmentTransaction transaction = fm.beginTransaction();
            transaction.setCustomAnimations(R.anim.enter_from_right, R.anim.exit_from_left);
            transaction.replace(R.id.container_single, chatFragment, ChatFragment.TAG);
            transaction.addToBackStack(null);
            transaction.commit();
        } else {
            ChatFragment chatFragment = (ChatFragment) fm.findFragmentByTag(ChatFragment.TAG);
            chatFragment.updateContact(contact);
        }
    }

    @Override
    public void onBackStackChanged() {
        if (getSupportFragmentManager().getBackStackEntryCount() > 0) {
            getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        } else {
            getSupportActionBar().setDisplayHomeAsUpEnabled(false);
        }
    }

    @Override
    public boolean onSupportNavigateUp() {
        getSupportFragmentManager().popBackStack();
        //((TextView)findViewById(R.id.actionbar_text)).setText(R.string.app_name);
        return true;
    }

    @Override
    public void onBackPressed() {
        getSupportFragmentManager().popBackStack();
    }
}