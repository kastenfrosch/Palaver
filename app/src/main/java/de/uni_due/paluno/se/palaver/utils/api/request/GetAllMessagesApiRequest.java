package de.uni_due.paluno.se.palaver.utils.api.request;

import de.uni_due.paluno.se.palaver.utils.api.ChatMessage;
import de.uni_due.paluno.se.palaver.utils.api.MagicCallback;

import java.util.List;

public class GetAllMessagesApiRequest extends ApiRequest<List<ChatMessage>> {


    private String recipient;

    public GetAllMessagesApiRequest(MagicCallback<List<ChatMessage>> callback) {
        super(callback);
    }

    public String getRecipient() {
        return recipient;
    }

    public void setRecipient(String recipient) {
        this.recipient = recipient;
    }
}
