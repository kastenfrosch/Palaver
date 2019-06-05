package com.example.palaver.utils.api.request;

public class GetAllMessagesApiRequest extends ApiRequest {
    private String recipient;

    public String getRecipient() {
        return recipient;
    }

    public void setRecipient(String recipient) {
        this.recipient = recipient;
    }
}
