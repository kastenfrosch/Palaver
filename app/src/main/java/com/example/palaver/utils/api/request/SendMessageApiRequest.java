package com.example.palaver.utils.api.request;

import com.example.palaver.utils.api.request.ApiRequest;

public class SendMessageApiRequest extends ApiRequest {

    private String data;
    private String recipient;
    private String mimetype;

    public String getData() {
        return data;
    }

    public void setData(String data) {
        this.data = data;
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
}
