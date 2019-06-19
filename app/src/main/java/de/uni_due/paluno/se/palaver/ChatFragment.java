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

import de.uni_due.paluno.se.palaver.utils.Utils;
import de.uni_due.paluno.se.palaver.utils.storage.ChatMessage;
import de.uni_due.paluno.se.palaver.utils.api.MagicCallback;
import de.uni_due.paluno.se.palaver.utils.api.PalaverApi;
import de.uni_due.paluno.se.palaver.utils.api.request.GetAllMessagesApiRequest;
import de.uni_due.paluno.se.palaver.utils.api.request.GetMessagesWithOffsetApiRequest;
import de.uni_due.paluno.se.palaver.utils.api.request.SendMessageApiRequest;
import de.uni_due.paluno.se.palaver.utils.api.response.DateTimeContainer;
import de.uni_due.paluno.se.palaver.utils.storage.ChatHistory;
import de.uni_due.paluno.se.palaver.utils.storage.Storage;

import java.util.Date;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.List;
import java.util.Locale;

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
            if (getArguments() != null) {
                initContact(getArguments().getString("contact"));
            }
        }

        return view;
    }

    private void addMessage(ChatMessage m) {
        LayoutInflater inflater = getActivity().getLayoutInflater();
        RelativeLayout rl;
        SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss", Locale.GERMANY); //the SDF still shits itself if I add optional ms ( [.SSS] )
        if (m.getSender().equalsIgnoreCase(Storage.getInstance().getUsername())) {
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
                if (d.after(this.lastMessageTime)) {
                    //this is done to avoid re-requesting the latest message
                    d.setTime(d.getTime() + 1000);
                    this.lastMessageTime = d;
                }
            } catch (ParseException ex) {
                Log.w("*****", "failed to parse date " + m.getDateTime());
                Log.w("*****", ex.getMessage());
            }
        }
        LinearLayout chatContainer = this.container.findViewById(R.id.chat_message_container);
        if(chatContainer == null) {
            Log.w("Palaver.ChatFragment", "chatContainer is null!!");
        } else {
            container.addView(rl);
        }
    }

    public void fetchNewMessages() {
        GetMessagesWithOffsetApiRequest req = new GetMessagesWithOffsetApiRequest(new MagicCallback<List<ChatMessage>>() {
            @Override
            public void onSuccess(List<ChatMessage> chatMessages) {
                if (chatMessages == null) return;
                for (ChatMessage m : chatMessages) {
                    addMessage(m);
                }

                scrollToBottom();
            }
        });
        SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss", Locale.GERMANY);
        req.setOffset(sdf.format(this.lastMessageTime));
        req.setRecipient(this.contact);
        PalaverApi.execute(req);
    }

    public void initContact(final String contact) {
        LinearLayout messageContainer = container.findViewById(R.id.chat_message_container);
        if(messageContainer != null) {
            messageContainer.removeAllViews();
        }

        final ChatHistory history = Storage.I().getChatHistory(contact);
        if (history == null) {
            final ChatHistory newHistory = new ChatHistory();
            GetAllMessagesApiRequest request = new GetAllMessagesApiRequest(new MagicCallback<List<ChatMessage>>() {
                @Override
                public void onSuccess(List<ChatMessage> chatMessages) {
                    for (ChatMessage m : chatMessages) {
                        addMessage(m);
                        newHistory.addMessage(m);
                    }
                    scrollToBottom();
                    Storage.I().getChatHistories().put(contact, newHistory);
                }
            });
            request.setRecipient(contact);
            PalaverApi.execute(request);
        } else {
            List<ChatMessage> cm = history.getMessages();
            for(ChatMessage m : cm)  {
                addMessage(m);
            }

            GetMessagesWithOffsetApiRequest req = new GetMessagesWithOffsetApiRequest(new MagicCallback<List<ChatMessage>>() {
                @Override
                public void onSuccess(List<ChatMessage> chatMessages) {
                    for(ChatMessage m : chatMessages) {
                        addMessage(m);
                        history.addMessage(m);
                    }
                    scrollToBottom();
                }
            });
            req.setRecipient(contact);
            req.setOffset(Utils.stringifyDateTime(new Date(history.getLastMessageTime())));
            PalaverApi.execute(req);
        }
        this.contact = contact;
    }

    public String getActiveContact() {
        return this.contact;
    }

    public void scrollToBottom() {
        container.findViewById(R.id.chat_message_sv).post(new Runnable() {
            @Override
            public void run() {
                ((ScrollView) container.findViewById(R.id.chat_message_sv)).fullScroll(ScrollView.FOCUS_DOWN);
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
                message.setSender(Storage.I().getUsername());
                message.setRecipient(contact);
                message.setMimetype("text/plain");
                message.setData(msgText);
                addMessage(message);
                scrollToBottom();
            }
        });
        req.setUsername(Storage.I().getUsername());
        req.setPassword(Storage.I().getPassword());
        req.setMimetype("text/plain");
        req.setRecipient(contact);
        req.setData(msgText);

        Log.d("*****", contact);

        text.getText().clear();


        PalaverApi.execute(req);

    }

}
