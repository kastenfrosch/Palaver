package de.uni_due.paluno.se.palaver;

import android.app.Activity;
import android.content.ContentResolver;
import android.content.Intent;
import android.database.Cursor;
import android.graphics.Color;
import android.media.projection.MediaProjection;
import android.net.Uri;
import android.os.Bundle;
import android.provider.MediaStore;
import android.provider.OpenableColumns;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v4.content.FileProvider;
import android.support.v7.widget.AppCompatEditText;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;
import android.widget.PopupMenu;
import android.widget.RelativeLayout;
import android.widget.ScrollView;
import android.widget.TextView;

import com.example.palaver.R;

import de.uni_due.paluno.se.palaver.utils.LocationUtils;
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

import java.io.BufferedInputStream;
import java.io.DataInputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOError;
import java.io.IOException;
import java.io.InputStream;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.Date;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.List;
import java.util.Locale;
import java.util.concurrent.Executors;

public class ChatFragment extends Fragment {
    public static final String TAG = "FRAGMENT_CHAT";
    private final int FILE_CHOOSER_REQUEST_CODE = 61;
    private String contact;
    private ViewGroup container;
    private Date lastMessageTime = new Date(1);
    private LocationUtils locationUtils;

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

        locationUtils = new LocationUtils(getActivity().getApplicationContext());
        locationUtils.getLocation();

        return view;
    }

    private void addMessage(ChatMessage m) {
        LayoutInflater inflater = getActivity().getLayoutInflater();
        RelativeLayout rl;
        SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss", Locale.GERMANY); //the SDF still shits itself if I add optional ms ( [.SSS] )
        if (m.getSender().equalsIgnoreCase(Storage.I().getUsername())) {
            rl = (RelativeLayout) inflater.inflate(R.layout.my_message, null);
            TextView message = rl.findViewById(R.id.message_body);
            if (m.getMimetype().equals("text/plain")) {
                message.setText((String) m.getData());
            } else if (m.getMimetype().equals("application/octet-stream")) {
                Log.d("******", "Datei: " + ((String) m.getData()).split(";")[0]);
                message.setText(((String) m.getData()).split(";")[0]);
                message.setTextColor(Color.CYAN);
                //TODO: add OnLongClickListener for opening the file
                //save file if its not open with File f = new File(getActivity().getFilesDir(), "filename")
            } else {
                message.setText("unsupported mimetype: " + m.getMimetype());
            }
        } else {
            rl = (RelativeLayout) inflater.inflate(R.layout.their_message, null);
            TextView name = rl.findViewById(R.id.name);
            TextView message = rl.findViewById(R.id.message_body);
            name.setText(m.getSender());
            if (m.getMimetype().equals("text/plain")) {
                message.setText((String) m.getData());
            } else if (m.getMimetype().equals("application/octet-stream")) {
                message.setText(((String) m.getData()).split(";")[0]);
                message.setTextColor(Color.CYAN);
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
        if (chatContainer == null) {
            Log.w("Palaver.ChatFragment", "chatContainer is null!!");
        } else {
            chatContainer.addView(rl);
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
        Log.d("*****", "Recipient: " + this.contact);
        req.setRecipient(this.contact);
        PalaverApi.execute(req);
    }

    public void initContact(final String contact) {
        LinearLayout messageContainer = container.findViewById(R.id.chat_message_container);
        if (messageContainer != null) {
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
            for (ChatMessage m : cm) {
                addMessage(m);
            }

            GetMessagesWithOffsetApiRequest req = new GetMessagesWithOffsetApiRequest(new MagicCallback<List<ChatMessage>>() {
                @Override
                public void onSuccess(List<ChatMessage> chatMessages) {
                    for (ChatMessage m : chatMessages) {
                        addMessage(m);
                        history.addMessage(m);
                    }
                    scrollToBottom();
                }
            });
            req.setRecipient(this.contact);
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
        req.setMimetype("text/plain");
        req.setRecipient(contact);
        req.setData(msgText);

        Log.d("*****", contact);

        text.getText().clear();

        PalaverApi.execute(req);

    }

    public void sendLocation() {

        double latitude = locationUtils.getLatitude();
        double longitude = locationUtils.getLongitude();

        String mapsUrl = "https://www.google.com/maps/search/?api=1&query=";
        mapsUrl = mapsUrl + latitude + "," + longitude;
        String explanation = "(press long for google maps position)";

        final String msgText = "Here is my location:\n" + mapsUrl + "\n" + explanation;
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

        PalaverApi.execute(req);
    }

    public void onAttachmentClicked(View view) {

        final PopupMenu pmenu = new PopupMenu(getActivity(), view);
        pmenu.getMenuInflater().inflate(R.menu.menu_attachment, pmenu.getMenu());

        pmenu.setOnMenuItemClickListener(new PopupMenu.OnMenuItemClickListener() {
            @Override
            public boolean onMenuItemClick(MenuItem item) {
                switch (item.getItemId()) {
                    case R.id.attachment:
                        Intent intent = new Intent(Intent.ACTION_OPEN_DOCUMENT);
                        intent.setType("*/*");
                        startActivityForResult(Intent.createChooser(intent, "Datei auswählen"), FILE_CHOOSER_REQUEST_CODE);
                        break;
                    case R.id.location:
                        sendLocation();
                        break;
                    default:
                        break;
                }
                return true;
            }
        });
        pmenu.show();
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        if (requestCode == FILE_CHOOSER_REQUEST_CODE && resultCode == Activity.RESULT_OK) {
            if (data == null) {
                return;
            }

            final Uri uri = data.getData();
            final ContentResolver resolver = getActivity().getContentResolver();
            Cursor c = resolver.query(uri, null, null, null, null);

            if (!c.moveToFirst()) {
                Utils.t("unknown error");
                return;
            }

            final long fileSize = c.getLong(c.getColumnIndex(OpenableColumns.SIZE));
            final String fileName = c.getString(c.getColumnIndex(OpenableColumns.DISPLAY_NAME));

            if (fileSize > Integer.MAX_VALUE) {
                Utils.t("Diese Datei ist zu groß.");
                return;
            }

            Log.i("******", "Starting send process for file: " + fileName);

            Runnable runnable = new Runnable() {
                @Override
                public void run() {
                    InputStream i = null;
                    try {
                        i = resolver.openInputStream(uri);
                        DataInputStream dis = new DataInputStream(i);
                        byte[] b = new byte[(int) fileSize];
                        Log.i("******", "Reading file...");
                        dis.readFully(b);

                        Log.i("******", "Hexifying file...");
                        final String dataToSend = fileName + ";" + Utils.hexify(b);
                        SendMessageApiRequest req = new SendMessageApiRequest(new MagicCallback<DateTimeContainer>() {
                            @Override
                            public void onSuccess(DateTimeContainer dateTimeContainer) {
                                ChatMessage cm = new ChatMessage();
                                cm.setData(dataToSend);
                                cm.setMimetype("application/octet-stream");
                                cm.setRecipient(contact);
                                cm.setSender(Storage.I().getUsername());
                                addMessage(cm);
                            }
                        });
                        req.setData(dataToSend);
                        req.setMimetype("application/octet-stream");
                        req.setRecipient(contact);
                        Log.i("******", "Sending file...");
                        PalaverApi.execute(req);
                    } catch (IOException e) {
                        Utils.t("Die Datei konnte nicht gesendet werden");
                        Log.e("******", e.getMessage());
                    }
                }
            };
            runnable.run();
        }
    }
}
