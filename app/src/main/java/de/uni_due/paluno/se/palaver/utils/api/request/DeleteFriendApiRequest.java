package de.uni_due.paluno.se.palaver.utils.api.request;

import de.uni_due.paluno.se.palaver.utils.api.MagicCallback;
import de.uni_due.paluno.se.palaver.utils.api.response.AddFriendApiResponse;
import de.uni_due.paluno.se.palaver.utils.api.response.ApiResponse;

public class DeleteFriendApiRequest extends ApiRequest<String> {

    private String friend;

    public DeleteFriendApiRequest(MagicCallback<String> callback) {
        super(callback);
    }

    @Override
    public String getApiEndpoint() {
        return "/api/friends/remove";
    }

    @Override
    public Class<? extends ApiResponse> getResponseType() {
        return AddFriendApiResponse.class;
    }

    public String getFriend() {
        return friend;
    }

    public void setFriend(String friend) {
        this.friend = friend;
    }
}

