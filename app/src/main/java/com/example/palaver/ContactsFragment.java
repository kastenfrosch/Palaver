package com.example.palaver;

import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.text.InputType;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.ListView;
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
    private ArrayAdapter<String> adapter;

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
        setHasOptionsMenu(true);

        View view = inflater.inflate(R.layout.fragment_contacts, container, false);

        this.adapter = new ArrayAdapter<>(getContext(), android.R.layout.simple_list_item_1);

        ListView lv = view.findViewById(R.id.contacts_listview);
        lv.setAdapter(this.adapter);

        refreshContacts();


        return view;
    }

    @Override
    public void onCreateOptionsMenu(Menu menu, MenuInflater inflater) {
        menu.add("Logout");
        super.onCreateOptionsMenu(menu, inflater);
    }

    private void refreshContacts() {
        adapter.clear();
        RestApiConnection.getFriends(new ApiRequest(), new VolleyCallback() {
            @Override
            public void onSuccess(Object result) {
                for(String s : (List<String>) result) {
                    adapter.add(s);
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
