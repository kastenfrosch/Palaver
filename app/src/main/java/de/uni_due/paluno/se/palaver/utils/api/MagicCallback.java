package de.uni_due.paluno.se.palaver.utils.api;

import android.util.Log;

public abstract class MagicCallback<T> {
    abstract public void onSuccess(T t);

    public void onError(Throwable reason) {
        Log.e(this.getClass().getName(), "ApiRequest failed: " + reason);
    }
}
