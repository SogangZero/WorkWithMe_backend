package com.wwme.wwme.notification;

import com.google.gson.JsonObject;
import com.wwme.wwme.group.domain.Group;
import com.wwme.wwme.group.domain.UserGroup;
import com.wwme.wwme.task.domain.Task;
import com.wwme.wwme.user.domain.User;

import java.util.Collection;

public interface NotificationService {
    void updateRegistrationToken(String registrationToken, User user);

    void updateRegistrationToken(String registrationToken, Long userId);

    void updateNotificationSetting(
            boolean onDueDate,
            boolean onMyTaskCreation,
            boolean onMyTaskChange,
            boolean onGroupEntrance,
            User user
    );

    void updateNotificationSetting(
            boolean onDueDate,
            boolean onMyTaskCreation,
            boolean onMyTaskChange,
            boolean onGroupEntrance,
            long userId
    );

    void sendTest();

    void sendDueDateNotification(Task task, User user);


    void sendOnGroupEntranceNotification(Group group, Collection<UserGroup> userGroups);

    void sendOnMyTaskCreation(Task task, User creatingUser);

    void sendOnMyTaskChange(Task task, Collection<User> changedUser, User changingUser);
}
