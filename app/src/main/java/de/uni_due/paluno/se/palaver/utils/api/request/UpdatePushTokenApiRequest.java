package de.uni_due.paluno.se.palaver.utils.api.request;

import de.uni_due.paluno.se.palaver.utils.api.MagicCallback;
import de.uni_due.paluno.se.palaver.utils.api.response.ApiResponse;
import de.uni_due.paluno.se.palaver.utils.api.response.UpdatePushTokenApiResponse;

public class UpdatePushTokenApiRequest extends ApiRequest<Void> {
    private String pushToken;

    public UpdatePushTokenApiRequest(MagicCallback<Void> callback) {
        super(callback);
    }

    @Override
    public String getApiEndpoint() {
        return "/api/user/pushtoken";
    }

    @Override
    public Class<? extends ApiResponse> getResponseType() {
        return UpdatePushTokenApiResponse.class;
    }

    public String getPushToken() {
        return pushToken;
    }

    public void setPushToken(String pushToken) {
        this.pushToken = pushToken;
    }
}
