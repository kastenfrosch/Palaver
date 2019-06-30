package de.uni_due.paluno.se.palaver.utils.storage;

import android.content.Context;
import android.content.SharedPreferences;

import com.google.gson.FieldNamingPolicy;
import com.google.gson.Gson;
import com.google.gson.GsonBuilder;

import java.util.HashMap;
import java.util.Map;

import de.uni_due.paluno.se.palaver.utils.ContextAware;

public class Storage extends ContextAware {
    private transient static Storage instance;
    private transient static final String PREFS_NAME = "palaver.json";
    private String username;
    private String password;

    private Map<String, Map<String, ChatHistory>> chatHistories;

    private Storage() {
        this.chatHistories = new HashMap<>();
        if (this.chatHistories.get(getUsername()) == null) {
            this.chatHistories.put(getUsername(), new HashMap<String, ChatHistory>());
        }
    }

    public void persist() {
        Gson gson = new GsonBuilder()
                .setFieldNamingPolicy(FieldNamingPolicy.LOWER_CASE_WITH_UNDERSCORES)
                .create();
        SharedPreferences prefs = getCtx().getSharedPreferences(PREFS_NAME, Context.MODE_PRIVATE);
        String json = gson.toJson(instance);
        prefs.edit()
                .putString("data", json)
                .apply();
    }

    public static Storage I() {
        return getInstance();
    }

    public static Storage getInstance() {
        if (instance == null) {
            Gson gson = new GsonBuilder()
                    .setFieldNamingPolicy(FieldNamingPolicy.LOWER_CASE_WITH_UNDERSCORES)
                    .create();
            SharedPreferences prefs = getCtx().getSharedPreferences(PREFS_NAME, Context.MODE_PRIVATE);
            instance = gson.fromJson(prefs.getString("data", "{}"), Storage.class);
        }
        return instance;
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

    public Map<String, ChatHistory> getChatHistories() {
        if (chatHistories.get(getUsername()) == null) {
            chatHistories.put(getUsername(), new HashMap<String, ChatHistory>());
        }
        return chatHistories.get(getUsername());
    }

    public ChatHistory getChatHistory(String contact) {
        return chatHistories.get(getUsername()).get(contact);
    }
}
