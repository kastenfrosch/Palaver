package de.uni_due.paluno.se.palaver.utils.api.request;

import de.uni_due.paluno.se.palaver.utils.UserCredentials;
import de.uni_due.paluno.se.palaver.utils.api.MagicCallback;
import de.uni_due.paluno.se.palaver.utils.api.response.ApiResponse;

public abstract class ApiRequest<T> {

    private final String BASE_URL = "http://palaver.se.paluno.uni-due.de";
    private String username;
    private String password;
    private transient MagicCallback<T> callback;

    public ApiRequest(MagicCallback<T> callback) {
        if(getApiEndpoint() == null) {
            throw new IllegalArgumentException("getApiEndpoint() is null!");
        }
        username = UserCredentials.getUsername();
        password = UserCredentials.getPassword();
        this.callback = callback;
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

    public MagicCallback<T> getCallback() {
        return this.callback;
    }

    public abstract String getApiEndpoint();

    public String getFullApiEndpoint() {
        return BASE_URL + getApiEndpoint();
    }

    public String getContentType() {
        return "application/json; charset=utf-8";
    }

    public abstract Class<? extends ApiResponse> getResponseType();

}
