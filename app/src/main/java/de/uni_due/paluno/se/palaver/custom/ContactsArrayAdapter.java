package de.uni_due.paluno.se.palaver.custom;

import android.content.Context;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.constraint.ConstraintLayout;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.TextView;

import com.example.palaver.R;

import java.util.List;

public class ContactsArrayAdapter extends ArrayAdapter<ContactListEntry> {

    public ContactsArrayAdapter(@NonNull Context context, int resource) {
        super(context, resource);
    }

    public ContactsArrayAdapter(@NonNull Context context, int resource, int textViewResourceId) {
        super(context, resource, textViewResourceId);
    }

    public ContactsArrayAdapter(@NonNull Context context, int resource, @NonNull ContactListEntry[] objects) {
        super(context, resource, objects);
    }

    public ContactsArrayAdapter(@NonNull Context context, int resource, int textViewResourceId, @NonNull ContactListEntry[] objects) {
        super(context, resource, textViewResourceId, objects);
    }

    public ContactsArrayAdapter(@NonNull Context context, int resource, @NonNull List<ContactListEntry> objects) {
        super(context, resource, objects);
    }

    public ContactsArrayAdapter(@NonNull Context context, int resource, int textViewResourceId, @NonNull List<ContactListEntry> objects) {
        super(context, resource, textViewResourceId, objects);
    }

    @NonNull
    @Override
    public View getView(int position, @Nullable View convertView, @NonNull ViewGroup parent) {
        LayoutInflater inflater = (LayoutInflater) getContext().getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        ConstraintLayout row = (ConstraintLayout) inflater.inflate(R.layout.contact_list_entry, parent, false);
        TextView label = row.findViewById(R.id.label);
        TextView unread = row.findViewById(R.id.unread_message_count);

        label.setText(getItem(position).getName());
        unread.setText(getItem(position).getUnread());
        if(getItem(position).getUnread().equals("0")) {
            unread.setVisibility(View.INVISIBLE);
        } else {
            unread.setVisibility(View.VISIBLE);
        }
        return row;
    }

    public int getPositionByName(String name) {
        for(int i=0; i < getCount(); i++) {
            if(getItem(i).getName().equals(name)) {
                return i;
            }
        }
        return -1;
    }
}
