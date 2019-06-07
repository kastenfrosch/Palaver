package de.uni_due.paluno.se.palaver.utils.api.request;

import de.uni_due.paluno.se.palaver.utils.api.MagicCallback;
import de.uni_due.paluno.se.palaver.utils.api.response.ApiResponse;
import de.uni_due.paluno.se.palaver.utils.api.response.GetFriendsApiResponse;

import java.util.List;

public class GetFriendsApiRequest extends ApiRequest<List<String>> {
    public GetFriendsApiRequest(MagicCallback<List<String>> callback) {
        super(callback);
    }

    @Override
    public String getApiEndpoint() {
        return "/api/friends/get";
    }

    @Override
    public Class<? extends ApiResponse> getResponseType() {
        return GetFriendsApiResponse.class;
    }
}
