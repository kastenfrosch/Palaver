package de.uni_due.paluno.se.palaver.utils.api.request;

import de.uni_due.paluno.se.palaver.utils.api.MagicCallback;
import de.uni_due.paluno.se.palaver.utils.api.response.DateTimeContainer;

public class SendMessageApiRequest extends ApiRequest<DateTimeContainer> {

    private String data;
    private String recipient;
    private String mimetype;

    public SendMessageApiRequest(MagicCallback<DateTimeContainer> callback) {
        super(callback);
    }

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
