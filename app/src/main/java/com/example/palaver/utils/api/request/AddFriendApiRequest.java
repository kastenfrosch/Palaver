package com.example.palaver.utils.api.request;

import com.example.palaver.utils.api.MagicCallback;

public class AddFriendApiRequest extends ApiRequest<String> {

    private String friend;
    private transient MagicCallback<String> callback;

    public AddFriendApiRequest(MagicCallback<String> callback) {
        super(callback);
    }

    public String getFriend() {
        return friend;
    }

    public void setFriend(String friend) {
        this.friend = friend;
    }

    @Override
    public MagicCallback<String> getCallback() {
        return callback;
    }

    public void setCallback(MagicCallback<String> callback) {
        this.callback = callback;
    }
}
