package de.uni_due.paluno.se.palaver.utils.api.request;

import de.uni_due.paluno.se.palaver.utils.api.ChatMessage;
import de.uni_due.paluno.se.palaver.utils.api.MagicCallback;
import de.uni_due.paluno.se.palaver.utils.api.response.ApiResponse;
import de.uni_due.paluno.se.palaver.utils.api.response.GetAllMessagesApiResponse;

import java.util.List;

public class GetAllMessagesApiRequest extends ApiRequest<List<ChatMessage>> {


    private String recipient;

    public GetAllMessagesApiRequest(MagicCallback<List<ChatMessage>> callback) {
        super(callback);
    }

    @Override
    public String getApiEndpoint() {
        return "/api/message/get";
    }

    @Override
    public Class<? extends ApiResponse> getResponseType() {
        return GetAllMessagesApiResponse.class;
    }

    public String getRecipient() {
        return recipient;
    }

    public void setRecipient(String recipient) {
        this.recipient = recipient;
    }
}
