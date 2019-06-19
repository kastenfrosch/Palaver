package de.uni_due.paluno.se.palaver.utils.api;

import android.util.Log;

import de.uni_due.paluno.se.palaver.utils.api.response.ApiResponse;

public abstract class MagicCallback<T> {
    abstract public void onSuccess(T t);

    public void onError(ApiResponse r) {
        Log.e(this.getClass().getName(), "ApiRequest failed: " + r.getInfo());
    }

    public void onError(Throwable t) {
        Log.e(this.getClass().getName(), "ApiRequest failed: " + t);
    }
}
