package de.uni_due.paluno.se.palaver.utils;

import org.json.JSONException;
import org.json.JSONObject;

public class JsonObjectWizard {

    public static JSONObject createUser(String username, String password) {

        JSONObject user = new JSONObject();

        try {
            user.put("Username", username);
            user.put("Password", password);
        } catch (JSONException e) {
            e.printStackTrace();
        }

        return user;
    }

    public static JSONObject createNewPassword(String username, String oldPassword, String newPassword) {

        JSONObject changedPassword = new JSONObject();

        try {
            changedPassword.put("Username", username);
            changedPassword.put("Password", oldPassword);
            changedPassword.put("NewPassword", newPassword);
        } catch (JSONException e) {
            e.printStackTrace();
        }

        return changedPassword;
    }

    public static JSONObject createRefreshPushToken(String username, String password, String pushToken) {

        JSONObject refreshToken = new JSONObject();

        try {
            refreshToken.put("Username", username);
            refreshToken.put("Password", password);
            refreshToken.put("PushToken", pushToken);
        } catch (JSONException e) {
            e.printStackTrace();
        }

        return refreshToken;
    }

    public static JSONObject createSendMessage(String username, String password, String pushToken) {

        JSONObject refreshToken = new JSONObject();

        try {
            refreshToken.put("Username", username);
            refreshToken.put("Password", password);
            refreshToken.put("PushToken", pushToken);
        } catch (JSONException e) {
            e.printStackTrace();
        }

        return refreshToken;
    }

    public static JSONObject createRequestConversation(String username, String password, String recipient) {

        JSONObject requestConversation = new JSONObject();

        try {
            requestConversation.put("Username", username);
            requestConversation.put("Password", password);
            requestConversation.put("Recipient", recipient);
        } catch (JSONException e) {
            e.printStackTrace();
        }

        return requestConversation;
    }

    public static JSONObject createRequestConvoWithOffset(String username, String password, String recipient, String offset) {

        JSONObject requestConversation = new JSONObject();

        try {
            requestConversation.put("Username", username);
            requestConversation.put("Password", password);
            requestConversation.put("Recipient", recipient);
            requestConversation.put("Offset", offset);
        } catch (JSONException e) {
            e.printStackTrace();
        }

        return requestConversation;
    }

    public static JSONObject createAddFriend(String username, String password, String friend) {

        JSONObject addFriend = new JSONObject();

        try {
            addFriend.put("Username", username);
            addFriend.put("Password", password);
            addFriend.put("Friend", friend);
        } catch (JSONException e) {
            e.printStackTrace();
        }

        return addFriend;
    }

    public static JSONObject createRemoveFriend(String username, String password, String friend) {

        JSONObject removeFriend = new JSONObject();

        try {
            removeFriend.put("Username", username);
            removeFriend.put("Password", password);
            removeFriend.put("Friend", friend);
        } catch (JSONException e) {
            e.printStackTrace();
        }

        return removeFriend;
    }

    public static JSONObject createListFriends(String username, String password) {

        JSONObject listFriends = new JSONObject();

        try {
            listFriends.put("Username", username);
            listFriends.put("Password", password);
        } catch (JSONException e) {
            e.printStackTrace();
        }

        return listFriends;
    }


}
