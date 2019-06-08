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
import android.widget.EditText;
import android.widget.ListView;

import com.example.palaver.R;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import de.uni_due.paluno.se.palaver.custom.ContactListEntry;
import de.uni_due.paluno.se.palaver.custom.ContactsArrayAdapter;
import de.uni_due.paluno.se.palaver.utils.UserPrefs;
import de.uni_due.paluno.se.palaver.utils.Utils;
import de.uni_due.paluno.se.palaver.utils.api.MagicCallback;
import de.uni_due.paluno.se.palaver.utils.api.RestApiConnection;
import de.uni_due.paluno.se.palaver.utils.api.request.AddFriendApiRequest;
import de.uni_due.paluno.se.palaver.utils.api.request.GetFriendsApiRequest;

public class ContactsFragment extends Fragment implements AdapterView.OnItemClickListener {
    public static final String TAG = "FRAGMENT_CONTACTS";
    private ContactsArrayAdapter adapter;
    private Map<String, String> notifications = new HashMap<>();
    private OnContactSelectedListener contactSelectedListener;


    @Nullable
    @Override
    public View onCreateView(final LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        super.onCreateView(inflater, container, savedInstanceState);
        setHasOptionsMenu(true);

        View view = inflater.inflate(R.layout.fragment_contacts, container, false);

        this.adapter = new ContactsArrayAdapter(getContext(), R.layout.contact_list_entry);

        ListView lv = view.findViewById(R.id.contacts_listview);
        lv.setAdapter(this.adapter);
        lv.setOnItemClickListener(this);

        refreshContacts();


        return view;
    }

    @Override
    public void onCreateOptionsMenu(Menu menu, MenuInflater inflater) {
        menu.add("isDirty");
        super.onCreateOptionsMenu(menu, inflater);
    }

    private void refreshContacts() {
        adapter.clear();
        RestApiConnection.execute(new GetFriendsApiRequest(new MagicCallback<List<String>>() {
            @Override
            public void onSuccess(List<String> result) {
                for (String s : result) {
                    adapter.add(new ContactListEntry(s, "0"));

                    /**************************/

                }
                /*for (int i = 0; i < adapter.getCount(); i++) {
                    ConstraintLayout listEntry = (ConstraintLayout) adapter.getView(
                            i,
                            null,
                            (ListView) getView().findViewById(R.id.contacts_listview)
                    );
                    final TextView unread = listEntry.findViewById(R.id.unread_message_count);
                    unread.setVisibility(View.INVISIBLE);
                }*/
            }
        }));
    }


    public void setUnread(String contact, String val) {
        this.adapter.getItem(this.adapter.getPositionByName(contact)).setUnread(val);
        getActivity().runOnUiThread(new Runnable() {
            @Override
            public void run() {
                adapter.notifyDataSetChanged();
            }
        });
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
                req.setUsername(UserPrefs.getUsername());
                req.setPassword(UserPrefs.getPassword());
                req.setFriend(input.getText().toString());
                RestApiConnection.execute(req);
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
        this.contactSelectedListener.onContactSelected((ContactListEntry) parent.getItemAtPosition(position));
    }

    public void setOnContactSelectedListener(OnContactSelectedListener listener) {
        this.contactSelectedListener = listener;
    }

    public interface OnContactSelectedListener {
        void onContactSelected(ContactListEntry contact);
    }
}
