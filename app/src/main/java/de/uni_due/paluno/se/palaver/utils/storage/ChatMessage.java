package de.uni_due.paluno.se.palaver.utils.storage;

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
}
