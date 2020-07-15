package de.uni_due.paluno.se.palaver.utils.storage;

import android.support.annotation.Nullable;

import java.util.Objects;

import de.uni_due.paluno.se.palaver.utils.Utils;

public class ChatMessage implements Comparable<ChatMessage>{
    private String sender;
    private String recipient;
    private String mimetype;
    private Object data;
    private String dateTime;

    public String getSender() {
        return sender;
    }

    public void setSender(String sender) {
        this.sender = sender;
    }

    public String getRecipient() {
        return recipient;
    }

    public void setRecipient(String recipient) {
        this.recipient = recipient;
    }

    public String getMimetype() {
        return mimetype;
    }

    public void setMimetype(String mimetype) {
        this.mimetype = mimetype;
    }

    public Object getData() {
        return data;
    }

    public void setData(Object data) {
        this.data = data;
    }

    public String getDateTime() {
        return dateTime;
    }

    public void setDateTime(String dateTime) {
        this.dateTime = dateTime;
    }

    @Override
    public int compareTo(ChatMessage o) {
        return Long.compare(
                Utils.parseDateTime(this.getDateTime()).getTime(),
                Utils.parseDateTime(o.getDateTime()).getTime()
        );
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        ChatMessage that = (ChatMessage) o;
        return sender.equals(that.sender) &&
                recipient.equals(that.recipient) &&
                mimetype.equals(that.mimetype) &&
                data.equals(that.data) &&
                dateTime.equals(that.dateTime);
    }

    @Override
    public int hashCode() {
        return Objects.hash(sender, recipient, mimetype, data, dateTime);
    }
}
