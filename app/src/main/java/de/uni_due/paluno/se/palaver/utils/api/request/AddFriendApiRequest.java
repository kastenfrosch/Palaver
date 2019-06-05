package de.uni_due.paluno.se.palaver.utils.api.request;

import de.uni_due.paluno.se.palaver.utils.api.MagicCallback;

public class AddFriendApiRequest extends ApiRequest<String> {

    private String friend;

    public AddFriendApiRequest(MagicCallback<String> callback) {
        super(callback);
    }

    public String getFriend() {
        return friend;
    }

    public void setFriend(String friend) {
        this.friend = friend;
    }
}
