package de.uni_due.paluno.se.palaver.utils;

import android.content.Context;

public abstract class ContextAware {
    private static Context ctx;

    public static void initialize(Context ctx) {
        ContextAware.ctx = ctx;
    }

    protected static Context getCtx() {
        if(ContextAware.ctx == null) {
            throw new IllegalStateException("call to getCtx() before context is initialized");
        }
        return ContextAware.ctx;
    }
}
