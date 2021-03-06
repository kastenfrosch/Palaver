package de.uni_due.paluno.se.palaver.utils.api.request;

import de.uni_due.paluno.se.palaver.utils.api.MagicCallback;
import de.uni_due.paluno.se.palaver.utils.api.response.ApiResponse;
import de.uni_due.paluno.se.palaver.utils.api.response.GenericApiResponse;

//TODO: this thing
public class ValidateUserApiRequest extends ApiRequest<Void> {
    public ValidateUserApiRequest(MagicCallback<Void> callback) {
        super(callback);
    }

    @Override
    public String getApiEndpoint() {
        return "/api/user/validate";
    }

    @Override
    public Class<? extends ApiResponse> getResponseType() {
        return GenericApiResponse.class;
    }

}
