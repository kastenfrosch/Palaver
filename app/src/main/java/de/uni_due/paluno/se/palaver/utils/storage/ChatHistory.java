package de.uni_due.paluno.se.palaver.utils.storage;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import de.uni_due.paluno.se.palaver.utils.Utils;

public class ChatHistory {
    private String contact;
    private List<ChatMessage> messages;
    private long lastMessageTime = -1;
    private boolean sorted = false;

    public ChatHistory() {
        this.messages = new ArrayList<>();
    }

    public void addMessage(ChatMessage cm) {
        this.messages.add(cm);
        updateLastMessageTime(cm);
    }

    public long getLastMessageTime() {
        updateLastMessageTime();

        return lastMessageTime;
    }

    private void updateLastMessageTime() {
        for (ChatMessage cm : messages) {
            updateLastMessageTime(cm);
        }
    }

    private void updateLastMessageTime(ChatMessage cm) {
        long time = Utils.parseDateTime(cm.getDateTime()).getTime();
        if (this.lastMessageTime < time) {
            this.lastMessageTime = time;
        }
    }

    public String getContact() {
        return contact;
    }

    public void setContact(String contact) {
        this.contact = contact;
    }

    public List<ChatMessage> getMessages() {
        return messages;
    }

    public void setMessages(List<ChatMessage> messages) {
        this.messages = messages;
    }

    public void sort() {
        this.sorted = true;
        Collections.sort(this.messages);
    }
}
