package de.uni_due.paluno.se.palaver.utils.api.request;

import java.util.List;

import de.uni_due.paluno.se.palaver.utils.api.ChatMessage;
import de.uni_due.paluno.se.palaver.utils.api.MagicCallback;
import de.uni_due.paluno.se.palaver.utils.api.response.ApiResponse;
import de.uni_due.paluno.se.palaver.utils.api.response.GetMessagesWithOffsetApiResponse;

public class GetMessagesWithOffsetApiRequest extends ApiRequest<List<ChatMessage>> {
    public GetMessagesWithOffsetApiRequest(MagicCallback<List<ChatMessage>> callback) {
        super(callback);
    }

    private String recipient;
    private String offset;

    @Override
    public String getApiEndpoint() {
        return "/api/message/getoffset";
    }

    @Override
    public Class<? extends ApiResponse> getResponseType() {
        return GetMessagesWithOffsetApiResponse.class;
    }

    public String getRecipient() {
        return recipient;
    }

    public void setRecipient(String recipient) {
        this.recipient = recipient;
    }

    public String getOffset() {
        return offset;
    }

    public void setOffset(String offset) {
        this.offset = offset;
    }
}
