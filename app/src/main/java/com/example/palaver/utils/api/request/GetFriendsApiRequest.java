package com.example.palaver.utils.api.request;

import com.example.palaver.utils.api.MagicCallback;

import java.util.List;

public class GetFriendsApiRequest extends ApiRequest<List<String>> {
    public GetFriendsApiRequest(MagicCallback<List<String>> callback) {
        super(callback);
    }
}
