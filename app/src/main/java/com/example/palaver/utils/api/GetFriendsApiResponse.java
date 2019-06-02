package com.example.palaver.utils.api;

import java.util.List;

public class GetFriendsApiResponse extends ApiResponse {

    public List<String> getFriends() {
        return (List<String>) getData();
    }
}
