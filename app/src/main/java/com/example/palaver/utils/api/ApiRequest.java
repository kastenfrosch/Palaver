package com.example.palaver.utils.api;

import com.example.palaver.utils.UserCredentials;

public class ApiRequest {
    private String username;
    private String password;

    public ApiRequest() {
        username = UserCredentials.getUsername();
        password = UserCredentials.getPassword();
    }

    public String getUsername() {
        return username;
    }

    public void setUsername(String username) {
        this.username = username;
    }

    public String getPassword() {
        return password;
    }

    public void setPassword(String password) {
        this.password = password;
    }
}
