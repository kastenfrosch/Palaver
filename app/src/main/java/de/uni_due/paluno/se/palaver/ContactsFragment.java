package de.uni_due.paluno.se.palaver;

import android.app.AlertDialog;
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
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.EditText;
import android.widget.ListView;

import com.example.palaver.R;

import de.uni_due.paluno.se.palaver.utils.UserCredentials;
import de.uni_due.paluno.se.palaver.utils.Utils;
import de.uni_due.paluno.se.palaver.utils.api.MagicCallback;
import de.uni_due.paluno.se.palaver.utils.api.request.AddFriendApiRequest;
import de.uni_due.paluno.se.palaver.utils.api.RestApiConnection;
import de.uni_due.paluno.se.palaver.utils.api.request.GetFriendsApiRequest;

import java.util.List;

public class ContactsFragment extends Fragment implements AdapterView.OnItemClickListener {
    public static final String TAG = "FRAGMENT_CONTACTS";
    private ArrayAdapter<String> adapter;
    private OnContactSelectedListener contactSelectedListener;


    @Nullable
    @Override
    public View onCreateView(final LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        super.onCreateView(inflater, container, savedInstanceState);
        setHasOptionsMenu(true);

        View view = inflater.inflate(R.layout.fragment_contacts, container, false);

        this.adapter = new ArrayAdapter<>(getContext(), android.R.layout.simple_list_item_1);

        ListView lv = view.findViewById(R.id.contacts_listview);
        lv.setAdapter(this.adapter);
        lv.setOnItemClickListener(this);

        refreshContacts();


        return view;
    }

    @Override
    public void onCreateOptionsMenu(Menu menu, MenuInflater inflater) {
        super.onCreateOptionsMenu(menu, inflater);
    }

    private void refreshContacts() {
        adapter.clear();
        RestApiConnection.getFriends(new GetFriendsApiRequest(new MagicCallback<List<String>>() {
            @Override
            public void onSuccess(List<String> result) {
                for (String s : result) {
                    adapter.add(s);
                }
            }
        }));
    }

    public void onFabClicked(View view) {
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


    //contact list click listeners

    @Override
    public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
        this.contactSelectedListener.onContactSelected((String) parent.getItemAtPosition(position));
    }

    public void setOnContactSelectedListener(OnContactSelectedListener listener) {
        this.contactSelectedListener = listener;
    }

    public interface OnContactSelectedListener {
        void onContactSelected(String contact);
    }
}
