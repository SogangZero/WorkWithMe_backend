package com.wwme.wwme.notification;

import com.wwme.wwme.group.domain.Group;
import com.wwme.wwme.group.domain.UserGroup;
import com.wwme.wwme.notification.domain.NotificationHistory;
import com.wwme.wwme.task.domain.Task;
import com.wwme.wwme.user.domain.User;

import java.util.Collection;

public interface NotificationService {

    void sendDueDateNotification(Task task, User user);


    void sendOnGroupEntranceNotification(Group group, Collection<UserGroup> userGroups);

    void sendOnMyTaskCreation(Task task, User creatingUser);

    void sendOnMyTaskChange(Task task, Collection<User> changedUser, User changingUser);

    Collection<NotificationHistory> getNotificationHistoryOfUser(User user, Long lastId);
}
