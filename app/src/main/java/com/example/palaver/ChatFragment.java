package com.example.palaver;

import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.design.widget.TextInputEditText;
import android.support.v4.app.Fragment;
import android.support.v7.widget.AppCompatEditText;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.example.palaver.utils.UserCredentials;
import com.example.palaver.utils.Utils;
import com.example.palaver.utils.api.RestApiConnection;
import com.example.palaver.utils.api.request.SendMessageApiRequest;

public class ChatFragment extends Fragment {

    private String contact;

    @Nullable
    @Override
    public View onCreateView(final LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        super.onCreateView(inflater, container, savedInstanceState);
        setHasOptionsMenu(true);

        View view = inflater.inflate(R.layout.fragment_chat, container, false);

        return view;
    }

    public void setContact(String contact) {
        this.contact = contact;
    }

    public void onSendClicked(View view) {
        AppCompatEditText text = getView().findViewById(R.id.message_input);
        text.getText().clear();
        SendMessageApiRequest req = new SendMessageApiRequest();
        req.setUsername(UserCredentials.getUsername());
        req.setPassword(UserCredentials.getPassword());
        req.setMimetype("text/plain");
        req.setRecipient(contact);
    }
}
