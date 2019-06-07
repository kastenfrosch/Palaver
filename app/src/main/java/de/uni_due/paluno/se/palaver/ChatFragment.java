package de.uni_due.paluno.se.palaver;

import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v7.widget.AppCompatEditText;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.ScrollView;
import android.widget.TextView;

import com.example.palaver.R;

import de.uni_due.paluno.se.palaver.utils.UserCredentials;
import de.uni_due.paluno.se.palaver.utils.Utils;
import de.uni_due.paluno.se.palaver.utils.api.ChatMessage;
import de.uni_due.paluno.se.palaver.utils.api.MagicCallback;
import de.uni_due.paluno.se.palaver.utils.api.RestApiConnection;
import de.uni_due.paluno.se.palaver.utils.api.request.GetAllMessagesApiRequest;
import de.uni_due.paluno.se.palaver.utils.api.request.GetMessagesWithOffsetApiRequest;
import de.uni_due.paluno.se.palaver.utils.api.request.SendMessageApiRequest;
import de.uni_due.paluno.se.palaver.utils.api.response.DateTimeContainer;

import java.util.Date;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.List;

public class ChatFragment extends Fragment {
    public static final String TAG = "FRAGMENT_CHAT";
    private String contact;
    private ViewGroup container;
    private Date lastMessageTime = new Date(1);

    @Nullable
    @Override
    public View onCreateView(final LayoutInflater inflater, @Nullable final ViewGroup container, @Nullable Bundle savedInstanceState) {
        super.onCreateView(inflater, container, savedInstanceState);
        this.container = container;
        setHasOptionsMenu(true);

        View view = inflater.inflate(R.layout.fragment_chat, container, false);
        if (savedInstanceState == null) {
            if(getArguments() != null) {
                updateContact(getArguments().getString("contact"));
            }
        }

        return view;
    }

    private void addMessage(ChatMessage m) {
        LayoutInflater inflater = getActivity().getLayoutInflater();
        RelativeLayout rl;
        SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss"); //the SDF still shits itself if I add optional ms ( [.SSS] )
        if (m.getSender().equalsIgnoreCase(UserCredentials.getUsername())) {
            rl = (RelativeLayout) inflater.inflate(R.layout.my_message, null);
            TextView message = rl.findViewById(R.id.message_body);
            if (m.getMimetype().equals("text/plain")) {
                message.setText((String) m.getData());
            } else {
                message.setText(("unsupported mimetype: " + m.getMimetype()));
            }
        } else {
            rl = (RelativeLayout) inflater.inflate(R.layout.their_message, null);
            TextView name = rl.findViewById(R.id.name);
            TextView message = rl.findViewById(R.id.message_body);
            name.setText(m.getSender());
            if (m.getMimetype().equals("text/plain")) {
                message.setText((String) m.getData());
            } else {
                message.setText("unsupported mimetype: " + m.getMimetype());
            }
            try {
                //remove milliseconds, we dont need those and the SDF will shit itself with them
                String cut = m.getDateTime().replaceAll("\\.\\d+$", "");
                Date d = sdf.parse(cut);
                if(d.after(this.lastMessageTime)) {
                    //this is done to avoid re-requesting the latest message
                    d.setTime(d.getTime()+1000);
                    this.lastMessageTime = d;
                }
            } catch(ParseException ex) {
                Log.w("*****", "failed to parse date " + m.getDateTime());
                Log.w("*****", ex.getMessage());
            }
        }
        ((LinearLayout) this.container.findViewById(R.id.chat_message_container)).addView(rl);
    }

    public void fetchNewMessages() {
        GetMessagesWithOffsetApiRequest req = new GetMessagesWithOffsetApiRequest(new MagicCallback<List<ChatMessage>>() {
            @Override
            public void onSuccess(List<ChatMessage> chatMessages) {
                if(chatMessages == null) return;
                for(ChatMessage m : chatMessages) {
                    addMessage(m);
                }

                scrollToBottom();
            }
        });
        SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss");
        req.setOffset(sdf.format(this.lastMessageTime));
        req.setRecipient(this.contact);
        RestApiConnection.execute(req);
    }

    public void updateContact(String contact) {
        GetAllMessagesApiRequest request = new GetAllMessagesApiRequest(new MagicCallback<List<ChatMessage>>() {
            @Override
            public void onSuccess(List<ChatMessage> chatMessages) {
                ((LinearLayout) container.findViewById(R.id.chat_message_container)).removeAllViews();
                for (ChatMessage m : chatMessages) {
                    addMessage(m);
                }
                scrollToBottom();
            }
        });
        request.setRecipient(contact);
        RestApiConnection.execute(request);

        this.contact = contact;
    }

    public String getActiveContact() {
        return this.contact;
    }

    public void scrollToBottom() {
        container.findViewById(R.id.chat_message_sv).post(new Runnable() {
            @Override
            public void run() {
                ((ScrollView)container.findViewById(R.id.chat_message_sv)).fullScroll(ScrollView.FOCUS_DOWN);
            }
        });
    }


    public void onSendClicked(View view) {
        AppCompatEditText text = getView().findViewById(R.id.message_input);
        final String msgText = text.getText().toString();
        SendMessageApiRequest req = new SendMessageApiRequest(new MagicCallback<DateTimeContainer>() {
            @Override
            public void onSuccess(DateTimeContainer dateTimeContainer) {
                Utils.t("Message @ " + dateTimeContainer.getDateTime());
                ChatMessage message = new ChatMessage();
                message.setSender(UserCredentials.getUsername());
                message.setRecipient(contact);
                message.setMimetype("text/plain");
                message.setData(msgText);
                addMessage(message);
                scrollToBottom();
            }
        });
        req.setUsername(UserCredentials.getUsername());
        req.setPassword(UserCredentials.getPassword());
        req.setMimetype("text/plain");
        req.setRecipient(contact);
        req.setData(msgText);

        Log.d("*****", contact);

        text.getText().clear();


        RestApiConnection.sendMessage(req);

    }

}
