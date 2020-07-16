package de.uni_due.paluno.se.palaver;

import android.app.Activity;
import android.content.ContentResolver;
import android.content.Context;
import android.content.Intent;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Color;
import android.graphics.ImageDecoder;
import android.location.Location;
import android.media.projection.MediaProjection;
import android.net.Uri;
import android.os.Bundle;
import android.provider.MediaStore;
import android.provider.OpenableColumns;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v7.widget.AppCompatEditText;
import android.text.method.LinkMovementMethod;
import android.util.Base64;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.PopupMenu;
import android.widget.RelativeLayout;
import android.widget.ScrollView;
import android.widget.TextView;

import com.example.palaver.R;

import de.uni_due.paluno.se.palaver.utils.LocationUtils;
import de.uni_due.paluno.se.palaver.utils.Utils;
import de.uni_due.paluno.se.palaver.utils.api.response.ApiResponse;
import de.uni_due.paluno.se.palaver.utils.storage.ChatMessage;
import de.uni_due.paluno.se.palaver.utils.api.MagicCallback;
import de.uni_due.paluno.se.palaver.utils.api.PalaverApi;
import de.uni_due.paluno.se.palaver.utils.api.request.GetAllMessagesApiRequest;
import de.uni_due.paluno.se.palaver.utils.api.request.GetMessagesWithOffsetApiRequest;
import de.uni_due.paluno.se.palaver.utils.api.request.SendMessageApiRequest;
import de.uni_due.paluno.se.palaver.utils.api.response.DateTimeContainer;
import de.uni_due.paluno.se.palaver.utils.storage.ChatHistory;
import de.uni_due.paluno.se.palaver.utils.storage.Storage;

import java.io.ByteArrayOutputStream;
import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.util.Arrays;
import java.util.Date;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.List;
import java.util.Locale;

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
        if (container != null) {
            this.container = container;
        }
        setHasOptionsMenu(true);

        View view = inflater.inflate(R.layout.fragment_chat, container, false);

        locationUtils = new LocationUtils();

        return view;
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        if (savedInstanceState == null) {
            if (getArguments() != null) {
                initContact(getArguments().getString("contact"));
            }
        }
    }

    private void addMessage(ChatMessage m) {
        if(getActivity() == null) return; //TODO check this out
        LayoutInflater inflater = getActivity().getLayoutInflater();
        RelativeLayout rl;
        SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss", Locale.GERMANY); //the SDF still shits itself if I add optional ms ( [.SSS] )
        if (m.getSender().equalsIgnoreCase(Storage.I().getUsername())) {
            if (m.getMimetype().equals("text/plain")) {
                rl = (RelativeLayout) inflater.inflate(R.layout.my_message, null);
                TextView message = rl.findViewById(R.id.message_body);
                rl = (RelativeLayout) inflater.inflate(R.layout.my_message, null);
                message = rl.findViewById(R.id.message_body);
                message.setText((String) m.getData());
                //message.setMovementMethod(LinkMovementMethod.getInstance());
            } else if (m.getMimetype().equals("image/*")) {
                rl = (RelativeLayout) inflater.inflate(R.layout.my_image, null);
                ImageView message = rl.findViewById(R.id.image_body);
                try {
                    message = rl.findViewById(R.id.image_body);
                    if (((String) m.getData()).split("!").length != 2) return;
                    byte[] data = Base64.decode(((String) m.getData()).split("!")[1], 0);
                    File f = new File(getActivity().getFilesDir(), ((String) m.getData()).split("!")[0]);
                    if (!f.exists()) {
                        try {
                            Log.d("Palaver*****", f.getAbsolutePath());
                            Log.d("Palaver*****", Arrays.toString(data));
                            FileOutputStream fos = new FileOutputStream(f);
                            DataOutputStream dos = new DataOutputStream(fos);
                            dos.write(data);

                            Log.d("Palaver*****", "file written");
                        } catch (Exception ex) {
                            ex.printStackTrace();
                        }
                    }
                    Bitmap bmp = BitmapFactory.decodeFile(f.getAbsolutePath());
                    Log.d("Palaver*****", "" + bmp);
                    message.setImageBitmap(BitmapFactory.decodeFile(f.getAbsolutePath()));

                } catch (Exception e) {
                    e.printStackTrace();
                }

//                message.setText(((String) m.getData()).split("!")[0]);
//                message.setTextColor(Color.CYAN);

            } else {
                rl = (RelativeLayout) inflater.inflate(R.layout.my_message, null);
                TextView message = rl.findViewById(R.id.message_body);
                message.setText("unsupported mimetype: " + m.getMimetype());
            }
        } else {
            rl = (RelativeLayout) inflater.inflate(R.layout.their_message, null);
            TextView name = rl.findViewById(R.id.name);
            name.setText(m.getSender());
            if (m.getMimetype().equals("text/plain")) {
                TextView message = rl.findViewById(R.id.message_body);
                message.setText((String) m.getData());
            } else if (m.getMimetype().equals("image/*")) {
                rl = (RelativeLayout) inflater.inflate(R.layout.their_image, null);
                ImageView message = rl.findViewById(R.id.image_body);

                message = rl.findViewById(R.id.image_body);
                if (((String) m.getData()).split("!").length != 2) return;
                byte[] data = Base64.decode(((String) m.getData()).split("!")[1], 0);
                File f = new File(getActivity().getFilesDir(), ((String) m.getData()).split("!")[0]);
                if (!f.exists()) {
                    try {
                        Log.d("Palaver*****", f.getAbsolutePath());
                        Log.d("Palaver*****", Arrays.toString(data));
                        FileOutputStream fos = new FileOutputStream(f);
                        DataOutputStream dos = new DataOutputStream(fos);
                        dos.write(data);

                        Log.d("Palaver*****", "file written");
                    } catch (Exception ex) {
                        ex.printStackTrace();
                    }
                }
                Bitmap bmp = BitmapFactory.decodeFile(f.getAbsolutePath());
                Log.d("Palaver*****", "" + bmp);
                message.setImageBitmap(BitmapFactory.decodeFile(f.getAbsolutePath()));
                TextView label = rl.findViewById(R.id.name);
                label.setText(contact);


//                message.setText(((String) m.getData()).split("!")[0]);
//                message.setTextColor(Color.CYAN);
            } else {
                TextView message = rl.findViewById(R.id.message_body);
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
        scrollToBottom();
    }

    public void fetchNewMessages() {
        GetMessagesWithOffsetApiRequest req = new GetMessagesWithOffsetApiRequest(new MagicCallback<List<ChatMessage>>() {
            @Override
            public void onSuccess(List<ChatMessage> chatMessages) {
                if (chatMessages == null) return;
                Log.d("PALAVER", "Adding new messages");
                for (ChatMessage m : chatMessages) {
                    if(!m.getSender().equals(Storage.I().getUsername())) {
                        addMessage(m);
                        ChatHistory ch = Storage.I().getChatHistory(contact);
                        if(ch != null && !ch.getMessages().contains(m)) {
                            ch.addMessage(m);
                        }
                    }
                }
                Storage.I().persist();
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
//        LinearLayout messageContainer = getActivity().findViewById(R.id.chat_message_container);
        if (messageContainer != null) {
            messageContainer.removeAllViews();
            Log.d("*", "yield");
        } else {
            System.out.println("wtf");
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
                    Storage.I().persist();
                }
            });
            request.setRecipient(contact);
            PalaverApi.execute(request);
        } else {
            List<ChatMessage> cm = history.getMessages();
            for (ChatMessage m : cm) {
                Log.d("PALAVER", "Adding old message");
                addMessage(m);
            }

            GetMessagesWithOffsetApiRequest req = new GetMessagesWithOffsetApiRequest(new MagicCallback<List<ChatMessage>>() {
                @Override
                public void onSuccess(List<ChatMessage> chatMessages) {
                    for (ChatMessage m : chatMessages) {
                        addMessage(m);
                        history.addMessage(m);
                    }
                    Storage.I().persist();
                    scrollToBottom();
                }
            });
            req.setRecipient(contact);
            req.setOffset(Utils.stringifyDateTime(new Date(history.getLastMessageTime()+1000)));
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
                ChatMessage message = new ChatMessage();
                message.setSender(Storage.I().getUsername());
                message.setRecipient(contact);
                message.setMimetype("text/plain");
                message.setData(msgText);
                message.setDateTime(dateTimeContainer.getDateTime());
                addMessage(message);
                Storage.I().getChatHistory(contact).addMessage(message);
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
        Location loc;
        try {
            loc = locationUtils.getLocation();
        } catch(Exception ex) {
            Utils.t("No location");
            return;
        }
        double latitude = loc.getLatitude();
        double longitude = loc.getLongitude();

        String mapsUrl = "https://www.google.com/maps/search/?api=1&query=";
        mapsUrl = mapsUrl + latitude + "," + longitude;

        final String msgText = "Here is my location:\n" + mapsUrl + "\n";
        SendMessageApiRequest req = new SendMessageApiRequest(new MagicCallback<DateTimeContainer>() {
            @Override
            public void onSuccess(DateTimeContainer dateTimeContainer) {
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
                        intent.setType("image/*");
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

            Log.i("******", "Starting send process for file: " + fileName + " size " + fileSize);

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
                        Log.d("Palaver*****", "length " + b.length);

                        Log.i("******", "Encoding file...");
                        final String dataToSend = fileName + "!" + Base64.encodeToString(b, 0);
                        Log.d("Palaver*****", Arrays.toString(b));
                        SendMessageApiRequest req = new SendMessageApiRequest(new MagicCallback<DateTimeContainer>() {
                            @Override
                            public void onSuccess(DateTimeContainer dateTimeContainer) {
                                ChatMessage cm = new ChatMessage();
                                cm.setData(dataToSend);
                                cm.setMimetype("image/*");
                                cm.setRecipient(contact);
                                cm.setSender(Storage.I().getUsername());
                                addMessage(cm);
                            }

                            @Override
                            public void onError(Throwable t) {
                                Utils.t("Fehler: Palaver hat die Datei abgelehnt (500). Datei ist evtl. zu groß.");
                            }
                        });
                        req.setData(dataToSend);
                        req.setMimetype("image/*");
                        req.setRecipient(contact);
                        Log.i("******", "Sending file...");
                        PalaverApi.execute(req);
                    } catch (IOException e) {
                        Utils.t("Die Datei konnte nicht gesendet werden");
                        Log.e("******", "" + e.getMessage());
                        e.printStackTrace();
                    }
                }
            };
            runnable.run();
        }
    }
}
