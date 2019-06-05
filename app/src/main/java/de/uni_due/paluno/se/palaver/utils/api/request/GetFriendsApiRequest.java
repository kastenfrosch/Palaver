package de.uni_due.paluno.se.palaver.utils.api.request;

import de.uni_due.paluno.se.palaver.utils.api.MagicCallback;

import java.util.List;

public class GetFriendsApiRequest extends ApiRequest<List<String>> {
    public GetFriendsApiRequest(MagicCallback<List<String>> callback) {
        super(callback);
    }
}
