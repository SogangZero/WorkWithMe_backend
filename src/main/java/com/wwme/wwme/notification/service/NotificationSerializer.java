package com.wwme.wwme.notification.service;

import com.google.gson.JsonObject;

import java.util.Map;

/**
 * Serializes notification message to json.
 * Matches the required api Firebase Cloud Messaging Requires.
 */
public class NotificationSerializer {

    /**
     * Makes json of notification with title and body
     * @param title Title of notification.
     * @param body Body of notification.
     * @return The resulting JsonObject
     */
    private JsonObject makeNotificationJsonObject(String title, String body) {
        JsonObject notification = new JsonObject();
        notification.addProperty("title", title);
        notification.addProperty("body", body);

        return notification;
    }

    /**
     * Makes json of data given a map of key-values to be added.
     * @param map map of key-value to be put in json
     * @return The resulting JsonObject
     */
    private JsonObject makeDataJsonObject(Map<String, String> map) {
        JsonObject data = new JsonObject();
        map.forEach(data::addProperty);
        return data;
    }


    /**
     * Makes json of notification.
     * @param title Title of notification.
     * @param body Body of notification.
     * @param dataMap Map of key-value to be added as additional data with notification.
     * @param receiveRegistrationToken The registration token of user in which the notification is sent.
     * @return The resulting JsonObject
     */
    public JsonObject makeSendJsonObject(String title, String body, Map<String, String> dataMap, String receiveRegistrationToken) {
        JsonObject notification = makeNotificationJsonObject(title, body);
        JsonObject data = makeDataJsonObject(dataMap);
        JsonObject message = new JsonObject();
        message.addProperty("name", "1");
        message.add("notification", notification);
        message.add("data", data);
        message.addProperty("token", receiveRegistrationToken);

        JsonObject sendJsonObject = new JsonObject();
        sendJsonObject.addProperty("validateOnly", false);
        sendJsonObject.add("message", message);

        return sendJsonObject;
    }
}
