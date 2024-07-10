package com.wwme.wwme.notification;

import com.google.auth.oauth2.GoogleCredentials;
import com.google.gson.JsonObject;
import com.wwme.wwme.group.domain.Group;
import com.wwme.wwme.group.domain.UserGroup;
import com.wwme.wwme.task.domain.Task;
import com.wwme.wwme.user.domain.User;
import com.wwme.wwme.user.repository.UserRepository;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.client.RestTemplate;

import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.nio.charset.StandardCharsets;
import java.util.Arrays;
import java.util.Collection;
import java.util.HashMap;
import java.util.Map;

@Slf4j
@Service
@Transactional
public class NotificationServiceImpl implements NotificationService {
    @Value("${notification.fcm_secrets}")
    private String rawFcmJson;

    @Value("${notification.project_id}")
    private String projectId;

    private final String[] SCOPES = {"https://www.googleapis.com/auth/firebase.messaging"};
    private final UserRepository userRepository;

    public NotificationServiceImpl(UserRepository userRepository) {
        this.userRepository = userRepository;
    }

    private InputStream getStream() {
        return new ByteArrayInputStream(rawFcmJson.getBytes(StandardCharsets.UTF_8));
    }

    private String getAccessToken() throws IOException {
        GoogleCredentials googleCredentials = GoogleCredentials
                .fromStream(getStream())
                .createScoped(Arrays.asList(SCOPES));
        googleCredentials.refresh();
        return googleCredentials.getAccessToken().getTokenValue();
    }

    @Override
    public void updateRegistrationToken(String registrationToken, User user) {
        updateRegistrationToken(registrationToken, user.getId());
    }

    @Override
    public void updateRegistrationToken(String registrationToken, Long userId) {
        userRepository.updateRegistrationToken(registrationToken, userId);
    }

    @Override
    public void updateNotificationSetting(boolean onDueDate, boolean onMyTaskCreation,
                                          boolean onMyTaskChange, boolean onGroupEntrance, User user) {
        updateNotificationSetting(onDueDate, onMyTaskCreation, onMyTaskChange, onGroupEntrance, user.getId());
    }

    @Override
    public void updateNotificationSetting(boolean onDueDate, boolean onMyTaskCreation,
                                          boolean onMyTaskChange, boolean onGroupEntrance, long userId) {
        userRepository.updateNotificationSetting(onDueDate, onMyTaskCreation, onMyTaskChange, onGroupEntrance, userId);
    }

    @Override
    public void sendTest() {
        send(makeSendJsonObject("title", "body", new HashMap<>(), "a"));
    }

    @Override
    public void sendDueDateNotification(Task task, User user) {
        // check notification setting if notification is enabled
        if (!user.getNotificationSetting().getOnDueDate()) {
            return;
        }

        Map<String, String> dataMap = new HashMap<>();
        dataMap.put("task_id", task.getId().toString());

        String title = "임박한 할 일이 있어요!";
        String body = "\"" + task.getTaskName() + "\"" + "이 " + "오늘까지인데 아직 끝나지 않았습니다!";

        var registrationToken = user.getNotificationSetting().getRegistrationToken();

        var sendJsonObject = makeSendJsonObject(title, body, dataMap, registrationToken);
        send(sendJsonObject);
    }


    private void sendOneOnGroupEntranceNotification(Group group, User newUser, String registrationToken) {
        Map<String, String> dataMap = new HashMap<>();
        dataMap.put("group_id", group.getId().toString());

        String titleBody = group.getGroupName() + "에" + newUser.getNickname() + "이 들어왔습니다!";

        var sendJsonObject = makeSendJsonObject(titleBody, titleBody, dataMap, registrationToken);
        send(sendJsonObject);
    }

    @Override
    public void sendOnGroupEntranceNotification(Group group, Collection<UserGroup> userGroups) {
        userGroups.forEach((userGroup) -> {
            var user = userGroup.getUser();

            // check notification setting if notification is enabled
            if (!user.getNotificationSetting().getOnGroupEntrance()) {
                return;
            }
            sendOneOnGroupEntranceNotification(group, user, user.getNotificationSetting().getRegistrationToken());
        });
    }

    private void sendOnMyTaskCreationAnyone(Task task, User creatingUser) {
        task.getUserTaskList().forEach(userTask -> {
            var notifiedUser = userTask.getUser();

            // check notification setting if notification is enabled
            if (!notifiedUser.getNotificationSetting().getOnMyTaskCreation()) {
                return;
            }

            Map<String, String> dataMap = new HashMap<>();
            dataMap.put("task_id", task.getId().toString());
            var title = "아무나 할 수 있는 일이 생겼습니다.";
            var body = "\"" + task.getTaskName() + "\"을 " +
                    creatingUser.getNickname() + "이 만들었습니다.";
            var registrationToken = notifiedUser.getNotificationSetting().getRegistrationToken();

            var sendJsonObject = makeSendJsonObject(title, body, dataMap, registrationToken);
            send(sendJsonObject);
        });
    }

    private void sendOnMyTaskCreationGroup(Task task, User creatingUser) {
        task.getUserTaskList().forEach(userTask -> {
            var notifiedUser = userTask.getUser();

            // check notification setting if notification is enabled
            if (!notifiedUser.getNotificationSetting().getOnMyTaskCreation()) {
                return;
            }

            Map<String, String> dataMap = new HashMap<>();
            dataMap.put("task_id", task.getId().toString());
            var title = "모두 해야 하는 일이 생겼습니다.";
            var body = "\"" + task.getTaskName() + "\"을 " +
                    creatingUser.getNickname() + "이 만들었습니다.";
            var registrationToken = notifiedUser.getNotificationSetting().getRegistrationToken();
            var sendJsonObject = makeSendJsonObject(title, body, dataMap, registrationToken);
            send(sendJsonObject);
        });

    }

    private void sendOnMyTaskCreationPersonal(Task task, User creatingUser) {
        task.getUserTaskList().forEach(userTask -> {
            var notifiedUser = userTask.getUser();

            // check notification setting if notification is enabled
            if (!notifiedUser.getNotificationSetting().getOnMyTaskCreation()) {
                return;
            }

            Map<String, String> dataMap = new HashMap<>();
            dataMap.put("task_id", task.getId().toString());
            var title = "할 일이 생겼습니다.";
            var body = "\"" + task.getTaskName() + "\"을 " +
                    creatingUser.getNickname() + "이 만들었습니다.";
            var registrationToken = notifiedUser.getNotificationSetting().getRegistrationToken();
            var sendJsonObject = makeSendJsonObject(title, body, dataMap, registrationToken);
            send(sendJsonObject);
        });
    }

    @Override
    public void sendOnMyTaskCreation(Task task, User creatingUser) {
        switch (task.getTaskType()) {
            case "anyone" -> sendOnMyTaskCreationAnyone(task, creatingUser);
            case "group" -> sendOnMyTaskCreationGroup(task, creatingUser);
            case "personal" -> sendOnMyTaskCreationPersonal(task, creatingUser);
        }
        ;
    }

    private void send(JsonObject jsonObject) {
        try {
            RestTemplate restTemplate = new RestTemplate();
            HttpHeaders headers = new HttpHeaders();
            headers.set("Authorization", "Bearer " + getAccessToken());
            headers.setContentType(MediaType.APPLICATION_JSON);

            String URI = "https://fcm.googleapis.com/v1/projects/" + projectId + "/messages:send";
            HttpEntity<String> httpEntity = new HttpEntity<>(jsonObject.toString(), headers);
            restTemplate.postForEntity(URI, httpEntity, String.class);
        } catch (Exception e) {
            log.error("Error in alarm send. ", e);
        }
    }

    private JsonObject makeNotificationJsonObject(String title, String body) {
        JsonObject notification = new JsonObject();
        notification.addProperty("title", title);
        notification.addProperty("body", body);

        return notification;
    }

    private JsonObject makeDataJsonObject(Map<String, String> dataMap) {
        JsonObject data = new JsonObject();
        dataMap.forEach(data::addProperty);
        return data;
    }

    private JsonObject makeSendJsonObject(String title, String body, Map<String, String> dataMap, String receiveRegistrationToken) {
        JsonObject notification = makeNotificationJsonObject(title, body);
        JsonObject data = makeDataJsonObject(dataMap);
        JsonObject message = new JsonObject();
        message.addProperty("name", "1");
        message.add("notification", notification);
        message.add("data", data);
        message.addProperty("token", receiveRegistrationToken);

        JsonObject sendJsonObject = new JsonObject();
        sendJsonObject.addProperty("validateOnly", true);
        sendJsonObject.add("message", message);

        return sendJsonObject;
    }
}
