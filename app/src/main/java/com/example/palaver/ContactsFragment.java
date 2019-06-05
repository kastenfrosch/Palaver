package com.example.palaver;

import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.text.InputType;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.EditText;
import android.widget.ListView;

import com.example.palaver.activity.LoginActivity;
import com.example.palaver.utils.FABClickListener;
import com.example.palaver.utils.UserCredentials;
import com.example.palaver.utils.Utils;
import com.example.palaver.utils.api.MagicCallback;
import com.example.palaver.utils.api.request.AddFriendApiRequest;
import com.example.palaver.utils.api.request.ApiRequest;
import com.example.palaver.utils.api.RestApiConnection;
import com.example.palaver.utils.api.VoidCallback;
import com.example.palaver.utils.api.ObjectCallback;
import com.example.palaver.utils.api.request.GetFriendsApiRequest;

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

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch(item.getTitle().toString()) {
            case "Logout":
                Utils.t("Logged out");
                UserCredentials.logout();
                Intent intent = new Intent(getContext(), LoginActivity.class);
                startActivity(intent);
                break;
            default:
                break;
        }
        return super.onOptionsItemSelected(item);
    }

    private void refreshContacts() {
        adapter.clear();
        RestApiConnection.getFriends(new GetFriendsApiRequest(new MagicCallback<List<String>>() {
            @Override
            public void onSuccess(List<String> result) {
                for(String s :  result) {
                    adapter.add(s);
                }
            }
        }));
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
                AddFriendApiRequest req = new AddFriendApiRequest(new MagicCallback<String>() {
                    @Override
                    public void onSuccess(String s) {
                        Utils.t("Friend added.");
                        refreshContacts();
                    }
                });
                req.setUsername(UserCredentials.getUsername());
                req.setPassword(UserCredentials.getPassword());
                req.setFriend(input.getText().toString());
                RestApiConnection.addFriend(req);
            }
        });

        builder.setNegativeButton("Cancel", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {

            }
        });

        builder.show();
    }
}
