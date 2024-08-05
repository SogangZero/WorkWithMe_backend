package com.wwme.wwme.notification.service;

import com.google.auth.oauth2.GoogleCredentials;
import com.google.gson.JsonObject;
import com.wwme.wwme.group.domain.Group;
import com.wwme.wwme.notification.NotificationHistoryRepository;
import com.wwme.wwme.notification.domain.NotificationHistory;
import com.wwme.wwme.notification.domain.NotificationType;
import com.wwme.wwme.task.domain.Task;
import com.wwme.wwme.user.domain.User;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;

import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;
import java.time.LocalDateTime;
import java.util.*;

/**
 * Service for sending notification to user using
 * Firebase Cloud Messaging
 */
@Slf4j
@Service
public class NotificationSender {
    @Value("${notification.project_id}")
    private String projectId;

    private final String[] SCOPES = {"https://www.googleapis.com/auth/firebase.messaging"};
    private final NotificationHistoryRepository notificationHistoryRepository;
    private final NotificationSerializer serializer;

    public NotificationSender(NotificationHistoryRepository notificationHistoryRepository,
                              NotificationSerializer serializer) {
        this.notificationHistoryRepository = notificationHistoryRepository;
        this.serializer = serializer;
    }

    /**
     * Gets InputStream from firebase service-account.json file
     * @return InputStream of file
     * @throws FileNotFoundException when file does not exist
     */
    private InputStream getStream() throws FileNotFoundException {
        return new FileInputStream("service-account.json");
    }

    /**
     * Gets access token from Firebase API.
     * @return access token string
     * @throws IOException when access token cannot be obtained from the stream
     */
    private String getAccessToken() throws IOException {
        GoogleCredentials googleCredentials = GoogleCredentials
                .fromStream(getStream())
                .createScoped(Arrays.asList(SCOPES));
        googleCredentials.refresh();
        return googleCredentials.getAccessToken().getTokenValue();
    }


    /**
     * Sends notification about due date.
     * Also records the notification in NotificationHistory.
     * @param task The task that is close to the due date
     * @param user The user that is participating in the task
     */
    public void sendDueDateNotification(Task task, User user) {
        // check notification setting if notification is enabled
        if (!user.getNotificationSetting().getOnDueDate()) {
            return;
        }

        Map<String, String> dataMap = new HashMap<>();
        dataMap.put("type", NotificationType.DUE_DATE.toString());
        dataMap.put("task_id", task.getId().toString());

        String title = "임박한 과제가 있어요!";
        String body = "\"" + task.getTaskName() + "\"이 아직 끝나지 않았습니다!";

        var registrationToken = user.getNotificationSetting().getRegistrationToken();

        var sendJsonObject = serializer.makeSendJsonObject(title, body, dataMap, registrationToken);
        recordNotification(title, body, user,
                NotificationType.DUE_DATE, task.getId(), null);
        send(sendJsonObject);
    }

    /**
     * Sends notification about group entrance to one user.
     * @param group The group a new user has entered.
     * @param newUser The user that has entered.
     * @param registrationToken The registration token of notified user.
     */
    private void sendOneOnGroupEntranceNotification(Group group, User newUser, String registrationToken) {
        Map<String, String> dataMap = new HashMap<>();
        dataMap.put("type", NotificationType.GROUP_ENTRANCE.toString());
        dataMap.put("group_id", group.getId().toString());

        String title = "\"" +  newUser.getNickname() + "\"" + "님이 \""
                + group.getGroupName() + "\"그룹에 들어왔습니다.";
        String body = title;

        var sendJsonObject = serializer.makeSendJsonObject(title, body, dataMap, registrationToken);
        recordNotification(title, body, newUser,
                NotificationType.GROUP_ENTRANCE, null, group.getId());
        send(sendJsonObject);
    }

    /**
     * Sends notification about group entrance to all users in group.
     * @param group The group a new user has entered.
     * @param newUser The user that has entered the group.
     */
    public void sendOnGroupEntranceNotification(Group group, User newUser) {
        var userGroups = group.getUserGroupList();
        userGroups.forEach((userGroup) -> {
            var user = userGroup.getUser();
            // check notification setting if notification is enabled
            if (!user.getNotificationSetting().getOnGroupEntrance()) {
                return;
            }
            sendOneOnGroupEntranceNotification(group, newUser, user.getNotificationSetting().getRegistrationToken());
        });
    }

    /**
     * Send notification about task that is created as type "anyone".
     * @param task The task created.
     * @param creatingUser The user that has created the task.
     */
    private void sendOnMyTaskCreationAnyone(Task task, User creatingUser) {
        task.getUserTaskList().forEach(userTask -> {
            var notifiedUser = userTask.getUser();

            // check notification setting if notification is enabled
            if (!notifiedUser.getNotificationSetting().getOnMyTaskCreation()) {
                return;
            }

            Map<String, String> dataMap = new HashMap<>();
            dataMap.put("type", NotificationType.TASK_CREATION.toString());
            dataMap.put("task_id", task.getId().toString());
            var title = "아무나 할 수 있는 과제가 생겼습니다.";
            var body = "\"" + creatingUser.getNickname() + "\"님이 \"" +
                    task.getTaskName() + "\"과제룰 만들었습니다.";
            var registrationToken = notifiedUser.getNotificationSetting().getRegistrationToken();

            var sendJsonObject = serializer.makeSendJsonObject(title, body, dataMap, registrationToken);
            recordNotification(title, body, notifiedUser,
                    NotificationType.TASK_CREATION, task.getId(), null);
            send(sendJsonObject);
        });
    }

    /**
     * Send notification about task that is created as type "group".
     * @param task The task created.
     * @param creatingUser The user that has created the task.
     */
    private void sendOnMyTaskCreationGroup(Task task, User creatingUser) {
        task.getUserTaskList().forEach(userTask -> {
            var notifiedUser = userTask.getUser();

            // check notification setting if notification is enabled
            if (!notifiedUser.getNotificationSetting().getOnMyTaskCreation()) {
                return;
            }

            Map<String, String> dataMap = new HashMap<>();
            dataMap.put("type", NotificationType.TASK_CREATION.toString());
            dataMap.put("task_id", task.getId().toString());
            var title = "모두 해야 하는 과제가 생겼습니다.";
            var body = "\"" + creatingUser.getNickname() + "\"님이 \"" +
                    task.getTaskName() + "\"과제를 만들었습니다.";
            var registrationToken = notifiedUser.getNotificationSetting().getRegistrationToken();
            var sendJsonObject = serializer.makeSendJsonObject(title, body, dataMap, registrationToken);
            recordNotification(title, body, notifiedUser,
                    NotificationType.TASK_CREATION, task.getId(), null);
            send(sendJsonObject);
        });

    }

    /**
     * Send notification about task that is created as type "personal".
     * @param task The task created.
     * @param creatingUser The user that has created the task.
     */
    private void sendOnMyTaskCreationPersonal(Task task, User creatingUser) {
        task.getUserTaskList().forEach(userTask -> {
            var notifiedUser = userTask.getUser();

            // check notification setting if notification is enabled
            if (!notifiedUser.getNotificationSetting().getOnMyTaskCreation()) {
                return;
            }

            Map<String, String> dataMap = new HashMap<>();
            dataMap.put("type", NotificationType.TASK_CREATION.toString());
            dataMap.put("task_id", task.getId().toString());
            var title = "과제가 생겼습니다.";
            var body = "\"" + creatingUser.getNickname() + "\"님이 \"" +
                    task.getTaskName() + "\"과제를 만들었습니다.";
            var registrationToken = notifiedUser.getNotificationSetting().getRegistrationToken();
            var sendJsonObject = serializer.makeSendJsonObject(title, body, dataMap, registrationToken);
            recordNotification(title, body, notifiedUser,
                    NotificationType.TASK_CREATION, task.getId(), null);
            send(sendJsonObject);
        });
    }

    /**
     * Send notification about task that is created.
     * @param task The task created.
     * @param creatingUser The user that has created the task.
     */
    public void sendOnMyTaskCreation(Task task, User creatingUser) {
        switch (task.getTaskType()) {
            case "anyone" -> sendOnMyTaskCreationAnyone(task, creatingUser);
            case "group" -> sendOnMyTaskCreationGroup(task, creatingUser);
            case "personal" -> sendOnMyTaskCreationPersonal(task, creatingUser);
        }
    }

    /**
     * Send notification about task that has been changed.
     * @param task The task that has been changed
     * @param changedUser The users that gets the notification
     * @param changingUser The user that has changed the notification
     */
    public void sendOnMyTaskChange(Task task, Collection<User> changedUser, User changingUser) {
        changedUser.forEach(user -> {
            if (!user.getNotificationSetting().getOnMyTaskChange()) {
                return;
            }

            Map<String, String> dataMap = new HashMap<>();
            dataMap.put("type", NotificationType.TASK_CHANGE.toString());
            dataMap.put("task_id", task.getId().toString());
            var title = "과제가 수정되었습니다.";
            var body = "\"" + changingUser.getNickname() + "\"님이 \"" +
                    task.getTaskName() + "\"과제를 수정하였습니다..";
            var registrationToken = user.getNotificationSetting().getRegistrationToken();
            var sendJsonObject = serializer.makeSendJsonObject(title, body, dataMap, registrationToken);
            recordNotification(title, body, user,
                    NotificationType.TASK_CHANGE, task.getId(), null);
            send(sendJsonObject);
        });
    }

    /**
     * Records notification sending to NotificationSender.
     * @param title The title of the notification.
     * @param body The body of the notification.
     * @param user User that has received the notification.
     * @param type The notification type.
     * @param taskId Task id of the task related to the notification if there is one.
     * @param groupId Group id of the task related to the notification if there is one.
     */
    private void recordNotification(String title, String body, User user,
                                    NotificationType type, Long taskId, Long groupId) {
        NotificationHistory notification = NotificationHistory.builder()
                .notificationTitle(title)
                .notificationBody(body)
                .notificationTime(LocalDateTime.now())
                .type(type)
                .taskId(taskId)
                .groupId(groupId)
                .user(user)
                .build();

        notificationHistoryRepository.save(notification);
    }

    /**
     * Sends the notification given the json request body.
     * @param jsonObject The json to be sent to Firebase Cloud Messaging server.
     */
    private void send(JsonObject jsonObject) {
        try {
            log.info("Send notification: {}", jsonObject);
            // don't send if token is null
            if (jsonObject.getAsJsonObject("message").get("token").getAsString() == null) {
                return;
            }

            RestTemplate restTemplate = new RestTemplate();
            HttpHeaders headers = new HttpHeaders();
            headers.set("Authorization", "Bearer " + getAccessToken());
            headers.setContentType(MediaType.APPLICATION_JSON);

            String URI = "https://fcm.googleapis.com/v1/projects/" + projectId + "/messages:send";
            HttpEntity<String> httpEntity = new HttpEntity<>(jsonObject.toString(), headers);
            var result = restTemplate.postForEntity(URI, httpEntity, String.class);
            log.info("Notification result: {}", result);
        } catch (RuntimeException e) {
            log.error("Error in alarm send. ", e);
        } catch (IOException e) {
            log.error("Error in alarm send in getting the access token. ", e);
        }
    }
}
