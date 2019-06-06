package de.uni_due.paluno.se.palaver.utils.api.request;

import de.uni_due.paluno.se.palaver.utils.api.MagicCallback;

public class UpdatePushTokenApiRequest extends ApiRequest<Void> {
    private String pushToken;

    public UpdatePushTokenApiRequest(MagicCallback<Void> callback) {
        super(callback);
    }

    public String getPushToken() {
        return pushToken;
    }

    public void setPushToken(String pushToken) {
        this.pushToken = pushToken;
    }
}
