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
import de.uni_due.paluno.se.palaver.utils.api.request.SendMessageApiRequest;
import de.uni_due.paluno.se.palaver.utils.api.response.DateTimeContainer;

import java.util.List;

public class ChatFragment extends Fragment {
    public static final String TAG = "FRAGMENT_CHAT";
    private String contact;
    private ViewGroup container;

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
        }
        ((LinearLayout) this.container.findViewById(R.id.chat_message_container)).addView(rl);
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
        RestApiConnection.getMessages(request);

        this.contact = contact;
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
