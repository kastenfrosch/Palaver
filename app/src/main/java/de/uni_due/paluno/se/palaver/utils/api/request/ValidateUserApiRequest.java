package de.uni_due.paluno.se.palaver.utils.api.request;

import de.uni_due.paluno.se.palaver.utils.api.MagicCallback;
import de.uni_due.paluno.se.palaver.utils.api.response.ApiResponse;

//TODO: this thing
public class ValidateUserApiRequest extends ApiRequest<Void> {
    public ValidateUserApiRequest(MagicCallback<Void> callback) {
        super(callback);
    }

    @Override
    public String getApiEndpoint() {
        return null;
    }

    @Override
    public Class<? extends ApiResponse> getResponseType() {
        return null;
    }
}
