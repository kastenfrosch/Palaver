package de.uni_due.paluno.se.palaver;

import android.app.AlertDialog;
import android.content.DialogInterface;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.text.InputType;
import android.util.Log;
import android.view.ContextMenu;
import android.view.LayoutInflater;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.EditText;
import android.widget.ListView;

import com.example.palaver.R;

import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import de.uni_due.paluno.se.palaver.custom.ContactListEntry;
import de.uni_due.paluno.se.palaver.custom.ContactsArrayAdapter;
import de.uni_due.paluno.se.palaver.utils.Utils;
import de.uni_due.paluno.se.palaver.utils.api.MagicCallback;
import de.uni_due.paluno.se.palaver.utils.api.PalaverApi;
import de.uni_due.paluno.se.palaver.utils.api.request.AddFriendApiRequest;
import de.uni_due.paluno.se.palaver.utils.api.request.DeleteFriendApiRequest;
import de.uni_due.paluno.se.palaver.utils.api.request.GetFriendsApiRequest;
import de.uni_due.paluno.se.palaver.utils.api.request.GetMessagesWithOffsetApiRequest;
import de.uni_due.paluno.se.palaver.utils.api.response.ApiResponse;
import de.uni_due.paluno.se.palaver.utils.storage.ChatHistory;
import de.uni_due.paluno.se.palaver.utils.storage.ChatMessage;
import de.uni_due.paluno.se.palaver.utils.storage.Storage;

public class ContactsFragment extends Fragment implements AdapterView.OnItemClickListener, AdapterView.OnCreateContextMenuListener {
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

        registerForContextMenu(lv);

        refreshContacts();


        return view;
    }

    public void refreshContacts() {
        adapter.clear();
        PalaverApi.execute(new GetFriendsApiRequest(new MagicCallback<List<String>>() {
            @Override
            public void onSuccess(List<String> result) {
                for (String s : result) {
                    adapter.add(new ContactListEntry(s, "0"));
                    checkForNew(s);
                }
                loadFromStorage();
            }

            @Override
            public void onError(ApiResponse r) {
                loadFromStorage();
            }

            @Override
            public void onError(Throwable t) {
                loadFromStorage();
            }

            private void loadFromStorage() {
                //we could either be offline or a friend was deleted but we still want to load the existing chat history
                for(String contact : Storage.I().getChatHistories().keySet()) {
                    if(adapter.getPositionByName(contact) == -1) {
                        adapter.add(new ContactListEntry(contact, "0"));
                    }
                }
            }
        }));


    }

    private void checkForNew(final String contact) {
        final ChatHistory ch = Storage.I().getChatHistory(contact);
        if(ch == null) return;
        ch.sort();
        final long last = ch.getMessages().size() > 0 ? Utils.parseDateTime(ch.getMessages().get(ch.getMessages().size()-1).getDateTime()).getTime() + 1000 : 0;
        GetMessagesWithOffsetApiRequest req = new GetMessagesWithOffsetApiRequest(new MagicCallback<List<ChatMessage>>() {
            @Override
            public void onSuccess(List<ChatMessage> chatMessages) {
                if(chatMessages != null && chatMessages.size() > 0) {
                    Log.d("'*****", "new: " + chatMessages.get(0).getDateTime());
                    Log.d("*****", (String)chatMessages.get(0).getData());
                    Log.d("*****", "old: " + Utils.stringifyDateTime(new Date(last)));
                    Log.d("*****", (String)ch.getMessages().get(ch.getMessages().size()-1).getData());
                    setUnread(contact, "*");
                }
            }
        });
        req.setOffset(Utils.stringifyDateTime(new Date(last)));
        req.setRecipient(contact);
        PalaverApi.execute(req);
    }


    public void setUnread(final String contact, final String val) {

        getActivity().runOnUiThread(new Runnable() {
            @Override
            public void run() {
                if(adapter.getPositionByName(contact) == -1) {
                    ContactListEntry cle = new ContactListEntry();
                    cle.setUnread(val);
                    cle.setName(contact);
                    adapter.add(cle);
                }
                adapter.getItem(adapter.getPositionByName(contact)).setUnread(val);
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
                req.setFriend(input.getText().toString());
                PalaverApi.execute(req);
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

    @Override
    public void onCreateContextMenu(ContextMenu menu, View v, ContextMenu.ContextMenuInfo menuInfo) {
        super.onCreateContextMenu(menu, v, menuInfo);
        if (v.getId()==R.id.contacts_listview) {
            MenuInflater inflater = getActivity().getMenuInflater();
            inflater.inflate(R.menu.menu_delete, menu);
        }
    }

    @Override
    public boolean onContextItemSelected(MenuItem item) {
        AdapterView.AdapterContextMenuInfo info = (AdapterView.AdapterContextMenuInfo) item.getMenuInfo();
        switch(item.getItemId()) {
            case R.id.delete:
                int pos = info.position;
                ListView lv = getActivity().findViewById(R.id.contacts_listview);
                ContactListEntry cle = (ContactListEntry) lv.getAdapter().getItem(pos);
                DeleteFriendApiRequest req = new DeleteFriendApiRequest(new MagicCallback<String>() {
                    @Override
                    public void onSuccess(String s) {
                        Utils.t("Friend removed from contacts.");
                        refreshContacts();
                    }
                });
                req.setFriend(cle.getName());
                PalaverApi.execute(req);
                return true;
            default:
                return super.onContextItemSelected(item);
        }
    }

}
