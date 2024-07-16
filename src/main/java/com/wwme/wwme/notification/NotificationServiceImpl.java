package com.wwme.wwme.notification;

import com.google.auth.oauth2.GoogleCredentials;
import com.google.gson.JsonObject;
import com.wwme.wwme.group.domain.Group;
import com.wwme.wwme.group.domain.UserGroup;
import com.wwme.wwme.notification.domain.NotificationHistory;
import com.wwme.wwme.notification.domain.NotificationType;
import com.wwme.wwme.task.domain.Task;
import com.wwme.wwme.user.domain.User;
import com.wwme.wwme.user.repository.UserRepository;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.client.RestTemplate;

import java.io.*;
import java.time.LocalDateTime;
import java.util.*;

@Slf4j
@Service
@Transactional
public class NotificationServiceImpl implements NotificationService {
    @Value("${notification.project_id}")
    private String projectId;

    private final String[] SCOPES = {"https://www.googleapis.com/auth/firebase.messaging"};
    private final UserRepository userRepository;
    private final NotificationHistoryRepository notificationHistoryRepository;

    public NotificationServiceImpl(UserRepository userRepository, NotificationHistoryRepository notificationHistoryRepository) {
        this.userRepository = userRepository;
        this.notificationHistoryRepository = notificationHistoryRepository;
    }

    private InputStream getStream() throws FileNotFoundException {
        return new FileInputStream("service-account.json");
    }

    private String getAccessToken() throws IOException {
        GoogleCredentials googleCredentials = GoogleCredentials
                .fromStream(getStream())
                .createScoped(Arrays.asList(SCOPES));
        googleCredentials.refresh();
        return googleCredentials.getAccessToken().getTokenValue();
    }


    @Override
    public void sendDueDateNotification(Task task, User user) {
        // check notification setting if notification is enabled
        if (!user.getNotificationSetting().getOnDueDate()) {
            return;
        }

        Map<String, String> dataMap = new HashMap<>();
        dataMap.put("type", NotificationType.DUE_DATE.toString());
        dataMap.put("task_id", task.getId().toString());

        String title = "임박한 할 일이 있어요!";
        String body = "\"" + task.getTaskName() + "\"" + "이 " + "오늘까지인데 아직 끝나지 않았습니다!";

        var registrationToken = user.getNotificationSetting().getRegistrationToken();

        var sendJsonObject = makeSendJsonObject(title, body, dataMap, registrationToken);
        recordNotification(title, body, user,
                NotificationType.DUE_DATE, task.getId(), null);
        send(sendJsonObject);
    }


    private void sendOneOnGroupEntranceNotification(Group group, User newUser, String registrationToken) {
        Map<String, String> dataMap = new HashMap<>();
        dataMap.put("type", NotificationType.GROUP_ENTRANCE.toString());
        dataMap.put("group_id", group.getId().toString());

        String title = group.getGroupName() + "에 " + newUser.getNickname() + "님이 들어왔습니다!";
        String body = title;

        var sendJsonObject = makeSendJsonObject(title, body, dataMap, registrationToken);
        recordNotification(title, body, newUser,
                NotificationType.GROUP_ENTRANCE, null, group.getId());
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
            dataMap.put("type", NotificationType.TASK_CREATION.toString());
            dataMap.put("task_id", task.getId().toString());
            var title = "아무나 할 수 있는 일이 생겼습니다.";
            var body = "\"" + task.getTaskName() + "\"을 " +
                    creatingUser.getNickname() + "님이 만들었습니다.";
            var registrationToken = notifiedUser.getNotificationSetting().getRegistrationToken();

            var sendJsonObject = makeSendJsonObject(title, body, dataMap, registrationToken);
            recordNotification(title, body, notifiedUser,
                    NotificationType.TASK_CREATION, task.getId(), null);
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
            dataMap.put("type", NotificationType.TASK_CREATION.toString());
            dataMap.put("task_id", task.getId().toString());
            var title = "모두 해야 하는 일이 생겼습니다.";
            var body = "\"" + task.getTaskName() + "\"을 " +
                    creatingUser.getNickname() + "이 만들었습니다.";
            var registrationToken = notifiedUser.getNotificationSetting().getRegistrationToken();
            var sendJsonObject = makeSendJsonObject(title, body, dataMap, registrationToken);
            recordNotification(title, body, notifiedUser,
                    NotificationType.TASK_CREATION, task.getId(), null);
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
            dataMap.put("type", NotificationType.TASK_CREATION.toString());
            dataMap.put("task_id", task.getId().toString());
            var title = "할 일이 생겼습니다.";
            var body = "\"" + task.getTaskName() + "\"을 " +
                    creatingUser.getNickname() + " 님이 만들었습니다.";
            var registrationToken = notifiedUser.getNotificationSetting().getRegistrationToken();
            var sendJsonObject = makeSendJsonObject(title, body, dataMap, registrationToken);
            recordNotification(title, body, notifiedUser,
                    NotificationType.TASK_CREATION, task.getId(), null);
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

    @Override
    public void sendOnMyTaskChange(Task task, Collection<User> changedUser, User changingUser) {
        changedUser.forEach(user -> {
            if (!user.getNotificationSetting().getOnMyTaskChange()) {
                return;
            }

            Map<String, String> dataMap = new HashMap<>();
            dataMap.put("type", NotificationType.TASK_CHANGE.toString());
            dataMap.put("task_id", task.getId().toString());
            var title = "할 일이 수정되었습니다.";
            var body = changingUser.getNickname() + "님이 "
                    + "\"" + task.getTaskName() + "\"을 수정하였습니다.";
            var registrationToken = user.getNotificationSetting().getRegistrationToken();
            var sendJsonObject = makeSendJsonObject(title, body, dataMap, registrationToken);
            recordNotification(title, body, user,
                    NotificationType.TASK_CHANGE, task.getId(), null);
            send(sendJsonObject);
        });
    }

    @Override
    public List<NotificationHistory> getNotificationHistoryOfUser(User user, Long lastId) {
        Pageable pageRequest = PageRequest.of(0, 20);
        return notificationHistoryRepository.findAllByUserAndLastId(user, lastId, pageRequest);
    }

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
        } catch (Exception e) {
            log.error("Error in alarm send. ", e);
        }
    }


}
