package de.uni_due.paluno.se.palaver.utils.api.request;

import de.uni_due.paluno.se.palaver.utils.api.MagicCallback;
import de.uni_due.paluno.se.palaver.utils.api.response.ApiResponse;
import de.uni_due.paluno.se.palaver.utils.api.response.GenericApiResponse;

public class RegisterUserApiRequest extends ApiRequest<Void> {
    public RegisterUserApiRequest(MagicCallback<Void> callback) {
        super(callback);
    }

    @Override
    public String getApiEndpoint() {
        return "/api/user/register";
    }

    @Override
    public Class<? extends ApiResponse> getResponseType() {
        return GenericApiResponse.class;
    }
}
