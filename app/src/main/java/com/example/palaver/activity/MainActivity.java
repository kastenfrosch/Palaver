package com.example.palaver.activity;

import android.app.ActionBar;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.FragmentManager;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.text.Html;
import android.view.View;
import android.widget.Toast;

import com.example.palaver.ListClickListener;
import com.example.palaver.ContactsFragment;
import com.example.palaver.R;
import com.example.palaver.utils.FABClickListener;

public class MainActivity extends AppCompatActivity implements ListClickListener, FABClickListener {
    FragmentManager fragmentManager;
    ContactsFragment contacts;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        setContentView(R.layout.activity_main);
        fragmentManager = getSupportFragmentManager();

        contacts = new ContactsFragment();

        if(savedInstanceState == null) {
            fragmentManager.beginTransaction().add(R.id.mainContainer, contacts).commit();
        }

        Toolbar toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        getSupportActionBar().setCustomView(R.layout.actionbar);
        getSupportActionBar().setDisplayOptions(ActionBar.DISPLAY_SHOW_HOME | ActionBar.DISPLAY_SHOW_CUSTOM);


    }

    @Override
    public void onOptionClicked(String option) {
        Toast.makeText(getApplicationContext(), "??? click", Toast.LENGTH_LONG).show();
    }


    @Override
    public void onFABClicked(View view) {
        contacts.onFABClicked(view);
    }

}
