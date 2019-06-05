package de.uni_due.paluno.se.palaver.utils.api.request;

import de.uni_due.paluno.se.palaver.utils.UserCredentials;
import de.uni_due.paluno.se.palaver.utils.api.MagicCallback;

public class ApiRequest<T> {
    private String username;
    private String password;
    private transient MagicCallback<T> callback;

    public ApiRequest(MagicCallback<T> callback) {
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
}
