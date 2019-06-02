package com.example.palaver;

import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.text.InputType;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.example.palaver.utils.FABClickListener;
import com.example.palaver.utils.UserCredentials;
import com.example.palaver.utils.Utils;
import com.example.palaver.utils.api.AddFriendRequest;
import com.example.palaver.utils.api.ApiRequest;
import com.example.palaver.utils.api.RestApiConnection;
import com.example.palaver.utils.api.VoidCallback;
import com.example.palaver.utils.api.VolleyCallback;

import java.util.List;

public class ContactsFragment extends Fragment implements FABClickListener {
    private ListClickListener callback;
    private LinearLayout contactsContainer;
    private LayoutInflater inflater;

    @Override
    public void onAttach(Context ctx) {
        super.onAttach(ctx);

        try {
            callback = (ListClickListener) ctx;
        } catch (Exception ex) {
            throw new ClassCastException(ctx.toString() + " cannot be cast to ListClickListener");
        }
    }

    @Nullable
    @Override
    public View onCreateView(final LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        super.onCreateView(inflater, container, savedInstanceState);
        this.inflater = inflater;

        View view = inflater.inflate(R.layout.fragment_contacts, container, false);

        LinearLayout first = new LinearLayout(getContext());
        first.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                callback.onOptionClicked("" + getId());
            }
        });

        final LinearLayout contactsContainer = view.findViewById(R.id.contactsContainer);
        this.contactsContainer = contactsContainer;
        refreshContacts();

        contactsContainer.addView(first);

        return view;
    }

    private void refreshContacts() {
        contactsContainer.removeAllViews();
        RestApiConnection.getFriends(new ApiRequest(), new VolleyCallback() {
            @Override
            public void onSuccess(Object result) {
                for(String s : (List<String>) result) {
                    LinearLayout entry = (LinearLayout) inflater.inflate(R.layout.contact_ll, contactsContainer, true);
                    TextView t = (TextView) inflater.inflate(R.layout.contact_tv, entry, false);
                    t.setText(s);
                    entry.addView(t);
                }
            }
        });
    }


    @Override
    public void onFABClicked(View view) {
        AlertDialog.Builder builder = new AlertDialog.Builder(getContext());
        builder.setTitle("Add Friend");

        final EditText input = new EditText(getContext());

        input.setInputType(InputType.TYPE_CLASS_TEXT);
        builder.setView(input);

        builder.setPositiveButton("OK", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                AddFriendRequest req = new AddFriendRequest();
                req.setUsername(UserCredentials.getUsername());
                req.setPassword(UserCredentials.getPassword());
                req.setFriend(input.getText().toString());
                RestApiConnection.addFriend(req, new VoidCallback() {
                    @Override
                    public void onSuccess() {
                        refreshContacts();
                    }
                });
                Utils.t("Adding friend " + input.getText().toString());
            }
        });

        builder.setNegativeButton("Cancel", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {

            }
        });

        builder.show();
        Utils.t("FAB clicked");
    }
}
